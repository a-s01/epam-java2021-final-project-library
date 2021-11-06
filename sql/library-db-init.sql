-- COMMENTS --
-- mysql 8.0 supports CHECK CONSTRAINS and enforces them by default (https://dev.mysql.com/doc/refman/8.0/en/create-table-check-constraints.html)
-- they are used here, so make sure mysql version is >= 8.0
-- TODO: For ids INT UNSIGNED is used, should be represented as long in java code

-- ---------------------------------------------
-- SCHEMA --
-- DROP SCHEMA IF EXISTS `library-app`;
CREATE SCHEMA IF NOT EXISTS `library-app` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE `library-app`;

-- ---------------------------------------------
-- DROP ALL TABLES --
-- Triggers for a table are also dropped if you drop the table.
SET FOREIGN_KEY_CHECKS=0; -- for being able to delete cross-referenced tables
DROP TABLE IF EXISTS editing_history;
DROP TABLE IF EXISTS book_in_booking;
DROP TABLE IF EXISTS booking;
DROP TABLE IF EXISTS book_author;
DROP TABLE IF EXISTS book;
DROP TABLE IF EXISTS author;
DROP TABLE IF EXISTS users;
SET FOREIGN_KEY_CHECKS=1;

-- ---------------------------------------------
-- CREATE ALL TABLES
-- ---------------------------------------------
-- TABLE user --
-- instead of role table ENUM inside users table is used to better aligns with java code. 
-- Role must be not null and '' means UNKNOWN role.
-- User with fine > 0 cannot create a new booking
-- User with fine > 0 cannot be deleted
-- User holding book (their booking state = 'delivered') cannot be deleted
-- If user is deleted, all their booking in state 'new' are canceled 
CREATE TABLE users (
	PRIMARY KEY (id),
	id				INT	UNSIGNED						NOT NULL	AUTO_INCREMENT,
    email			VARCHAR(50) 						NOT NULL,
					UNIQUE INDEX (email),
					CONSTRAINT valid_users_email
						-- CHECK (email RLIKE  '.*'), -- TODO should be checked and updated (add utf8 support)
                        CHECK (email RLIKE '^[[:alnum:]]+@[[:alnum:]\\.]+\\.[[:alpha:]]{2,6}$'), -- TODO should be checked and updated (add utf8 support)
	password		CHAR(128)							NOT NULL,
    salt			CHAR(50)							NOT NULL,
    role			ENUM('user', 'librarian', 'admin')	NOT NULL,	-- by default mysql behaviour first enum value will be default value 
																	-- for column. DON'T put 'admin' first!
    state			ENUM('valid', 'blocked')			NOT NULL,
    fine			INT UNSIGNED						NOT NULL	DEFAULT (0),
    name			VARCHAR(50),
    created			DATETIME							NOT NULL	DEFAULT CURRENT_TIMESTAMP,
    last_edit_id	INT UNSIGNED
)
AUTO_INCREMENT = 100; -- reserved space for system users

-- TABLE editing_history --
-- all edits made by users will be added here, the last edit will be saved in edit subject as reference
CREATE TABLE editing_history (
	PRIMARY KEY (id),
	id			INT UNSIGNED				NOT NULL	AUTO_INCREMENT,
	edit_time	DATETIME					NOT NULL	DEFAULT CURRENT_TIMESTAMP,
    edit_by		INT UNSIGNED,
				FOREIGN KEY (edit_by)
					REFERENCES users (id)
					ON DELETE SET NULL			
					ON UPDATE CASCADE,
    description	VARCHAR(200)				NOT NULL,   -- description of change, app must fill it in automatically. 
														-- Example "table X: attr Y is set to Z"             
	remark		VARCHAR(500)							-- field for user comment
);

ALTER TABLE users
    ADD CONSTRAINT `fk_last_edit_id`
			FOREIGN KEY (last_edit_id)
			REFERENCES editing_history (id)
			ON DELETE SET NULL
			ON UPDATE CASCADE;
            
-- TABLE author --
CREATE TABLE author (
	PRIMARY KEY (id),
	id				INT UNSIGNED				NOT NULL	AUTO_INCREMENT,
    name			VARCHAR(50)					NOT NULL,
					UNIQUE INDEX (name),
    created			DATETIME					NOT NULL	DEFAULT CURRENT_TIMESTAMP,
    last_edit_id	INT UNSIGNED,
					FOREIGN KEY (last_edit_id)
						REFERENCES editing_history (id)
                        ON DELETE SET NULL
                        ON UPDATE CASCADE        
);

-- TABLE book --
-- If book is added to booking, available_amount decreased by 1
-- If book was in booking, and its booking state marked as delivered was_booked_times increases by 1
CREATE TABLE book (
	PRIMARY KEY (id),
    id					INT UNSIGNED			NOT NULL	AUTO_INCREMENT,
    title				VARCHAR(256)			NOT NULL,
						UNIQUE INDEX (title),
    isbn				VARCHAR(17)				NOT NULL, 	-- 13 digits + 4 '-'
						UNIQUE INDEX (isbn),
    publication_year	YEAR					NOT NULL,
	total_amount		INT UNSIGNED			NOT NULL,	-- how many books library has in total, including available and not available for booking
						INDEX (total_amount),
                        CONSTRAINT
							CHECK (total_amount > 0),
	available_amount	INT UNSIGNED			NOT NULL	DEFAULT (total_amount), 	-- how many book are available for booking
						CONSTRAINT
							CHECK (available_amount <= total_amount),
    was_booked_times	INT UNSIGNED			NOT NULL	DEFAULT (0),  				-- how many times book was booked and this booking was in state 'delivered'
    keep_period			INT UNSIGNED			NOT NULL,	-- period of time in days book is allowed to be keeping by user
    created				DATETIME				NOT NULL	DEFAULT CURRENT_TIMESTAMP,
    last_edit_id		INT UNSIGNED,
						FOREIGN KEY (last_edit_id)
							REFERENCES editing_history (id)
							ON DELETE SET NULL
							ON UPDATE CASCADE     
);

-- TABLE book_author --
-- allows multiple author for book
CREATE TABLE book_author (
	PRIMARY KEY (book_id, author_id),
	book_id		INT UNSIGNED	NOT NULL,
				FOREIGN KEY (book_id)
					REFERENCES book (id)
					ON DELETE CASCADE
					ON UPDATE CASCADE,
    author_id	INT UNSIGNED	NOT NULL,
				FOREIGN KEY (author_id)
					REFERENCES author (id)
					ON DELETE RESTRICT
					ON UPDATE CASCADE
);

-- TABLE booking --
-- booking made by user (should not be available for admins or librarian -- business logic ensures).
-- user deleting cause deleting of the booking. To prevent deleting user holding a book a trigger must be used.
-- If user is deleted, all their booking in state 'new' are canceled (trigger)
-- User with fine > 0 cannot create a new booking (trigger)
-- booking in state 'new' cannot be moved to state 'done' (trigger)
-- booking in state 'delivered' cannot be moved to state 'new' (trigger)
-- booking in state 'done/canceled' cannot be moved to any new state (trigger)
CREATE TABLE booking (
	PRIMARY KEY (id),
	id				INT UNSIGNED		NOT NULL	AUTO_INCREMENT,
    user_id 		INT UNSIGNED		NOT NULL,
					FOREIGN KEY (user_id)
						REFERENCES users (id)
						ON DELETE CASCADE
						ON UPDATE CASCADE,
    state			ENUM('new', 'booked', 'delivered','done','canceled') NOT NULL,
    located			ENUM('library', 'user')					  NOT NULL,			
	created			DATETIME			NOT NULL	DEFAULT CURRENT_TIMESTAMP,
    last_edit_id		INT UNSIGNED,
						FOREIGN KEY (last_edit_id)
							REFERENCES editing_history (id)
							ON DELETE SET NULL
							ON UPDATE CASCADE
);

-- TABLE book_in_booking --
-- let's assume one user can take only one same book (can barely imagine other case)
-- adding book here means available book amount is decreased by one by trigger 
CREATE TABLE book_in_booking (
	PRIMARY KEY (booking_id, book_id),
    booking_id	INT UNSIGNED	NOT NULL,
				FOREIGN KEY (booking_id)
					REFERENCES booking (id)
					ON DELETE CASCADE
					ON UPDATE CASCADE,
    book_id		INT UNSIGNED	NOT NULL,
				FOREIGN KEY (book_id)
					REFERENCES book (id)
					ON DELETE CASCADE
					ON UPDATE CASCADE,
	keep_period	INT UNSIGNED			-- if null then default time from books table should be applied by application
);

-- ---------------------------------------------
-- TRIGGERS --
DELIMITER $$

-- adding book to booking means available book amount is decreased by 1 by trigger 
CREATE TRIGGER update_available_book_amount
	AFTER INSERT
    ON book_in_booking FOR EACH ROW
		UPDATE book 
		   SET available_amount = available_amount - 1 
		 WHERE book.id = NEW.book_id;
$$

CREATE TRIGGER update_available_book_amount_in_return_to_new_booking_state
	AFTER UPDATE
    ON booking FOR EACH ROW
		  IF OLD.state != 'new' AND OLD.state != 'booked' AND NEW.state = 'new'
        THEN 	
			 UPDATE book 
			    SET available_amount = available_amount - 1 
		      WHERE book.id
                 IN 
					( 
					  SELECT book_id
                        FROM book_in_booking
                       WHERE booking_id = NEW.id
					);
                     
	  END IF             
$$
-- User holding book (their booking state = 'delivered') cannot be deleted
CREATE TRIGGER check_booking_status_before_deleting_user
	BEFORE DELETE
    ON users FOR EACH ROW
		IF (SELECT COUNT(b.id) 
			  FROM booking AS b
			 WHERE b.user_id = OLD.id 
			   AND b.status IN ('delivered')
			) > 0 
		THEN
			SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'ERROR: Cannot delete user with active booking (booking state = "delivered")';
        END IF;
$$

-- User with fine > 0 cannot be deleted
CREATE TRIGGER user_with_fine_cannot_be_del
	BEFORE DELETE
    ON users FOR EACH ROW
		IF 	 OLD.fine > 0
        THEN SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'ERROR: Cannot delete user with dept (fine > 0)';
		END IF
$$

-- If user is deleted, all their booking in state 'new' are canceled 
CREATE TRIGGER after_deleting_user_cancel_their_bookings
	AFTER DELETE
    ON users FOR EACH ROW
		UPDATE booking AS b
           SET b.state = 'canceled'
		 WHERE b.user_id = OLD.id
           AND b.state = 'new';
$$

-- User with fine > 0 cannot create a new booking
CREATE TRIGGER check_user_fine_before_creating_booking
	BEFORE INSERT
    ON booking FOR EACH ROW
		IF (SELECT u.fine
			  FROM users AS u
			 WHERE u.id = NEW.user_id
            ) > 0
        THEN
			SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'ERROR: Cannot create booking for user with dept (fine > 0)';
        END IF;
$$

-- booking in state 'new' cannot be moved to state 'done' or 'delivered'
CREATE TRIGGER new_booking_restrictions
	BEFORE UPDATE
    ON booking FOR EACH ROW
		IF 	OLD.state = 'new' AND (NEW.state = 'done' OR NEW.state = 'delivered')
	    THEN
			SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'ERROR: Cannot move booking from state "new" to "done"/"delivered"';
        END IF;
$$

-- booking in state 'delivered' cannot be moved to state 'new'
CREATE TRIGGER delivered_booking_restrictions
	BEFORE UPDATE
    ON booking FOR EACH ROW
		IF 	OLD.state = 'delivered'
        AND NEW.state != 'done'
        THEN
			SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'ERROR: Cannot move booking from state "delivered" to any state except "done"';
        END IF;
$$

-- booking in state 'done/canceled' cannot be moved to any new state
CREATE TRIGGER closed_booking_restrictions
	BEFORE UPDATE
    ON booking FOR EACH ROW
		IF	(OLD.state = 'done' AND NEW.state != 'new')
        OR	(OLD.state = 'canceled' AND NEW.state != 'new')
        THEN
			SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'ERROR: Cannot move booking from final state (done/canceled) to any state except "new"';
        END IF;    
$$

-- if booking is finished (state = done or canceled), available book amount increased for each book in booking
CREATE TRIGGER finished_booking_updates_books_available_amount
	AFTER UPDATE
    ON booking FOR EACH ROW
		IF 		NEW.state = 'done' OR NEW.state = 'canceled'
        THEN	
				UPDATE book
				   SET available_amount = available_amount + 1
                 WHERE id 
                    IN (SELECT book_id 
                          FROM book_in_booking
						 WHERE booking_id = NEW.id
                       );
		END IF;
$$     

CREATE TRIGGER increase_was_booked_times_in_case_of_delivery
	AFTER UPDATE
    ON booking FOR EACH ROW
		IF 		NEW.state = 'delivered'
        THEN	
				UPDATE book
				   SET was_booked_times = was_booked_times + 1
                 WHERE id 
                    IN (SELECT book_id 
                          FROM book_in_booking
						 WHERE booking_id = NEW.id
                       );
		END IF;
$$ 
DELIMITER ;

-- ---------------------------------------------
-- FILL DB --
-- USERS --
INSERT INTO users VALUES (1, 'admin@gmail.com', 'qwerty1', '12345678', 'admin', DEFAULT, DEFAULT, 'admin', DEFAULT, NULL);
INSERT INTO users VALUES (2, 'librarian@gmail.com', 'qwerty1', '12345678', 'librarian', DEFAULT, DEFAULT, 'librarian', DEFAULT, NULL);
INSERT INTO users VALUES (3, 'user@gmail.com', 'qwerty1', '12345678', 'user', DEFAULT, DEFAULT, 'користувач', DEFAULT, NULL);
INSERT INTO users VALUES (DEFAULT, 'test@gmail.com', 'qwerty1', '12345678', DEFAULT, DEFAULT, DEFAULT, 'user test', DEFAULT, NULL);

-- VALID BOOKS --
INSERT INTO book VALUES (DEFAULT, 'Гарри Поттер. Полное собрание (комплект из 7 книг) (сборник)', '978-5-389-10668-0', '2016', 20, DEFAULT, DEFAULT, DEFAULT, NULL);
INSERT INTO book VALUES (DEFAULT, 'Зеленая миля', '978-5-17-118362-2', '2020', 5, DEFAULT, DEFAULT, DEFAULT, NULL);
INSERT INTO book VALUES (DEFAULT, 'Унесенные ветром', '978-5-389-17583-9', '2020', 5, DEFAULT, DEFAULT, DEFAULT, NULL);
INSERT INTO book VALUES (DEFAULT, 'Полное собрание произведений о Шерлоке Холмсе в одном томе (сборник)', '978-5-93556-958-7', '2015', 10, DEFAULT, DEFAULT, DEFAULT, NULL);
INSERT INTO book VALUES (DEFAULT, 'Прислуга', '978-5-86471-732-5', '2016', 2, DEFAULT, DEFAULT, DEFAULT, NULL);
INSERT INTO book VALUES (DEFAULT, 'Граф Монте-Кристо', '978-5-04-117008-0', '2021', 15, DEFAULT, DEFAULT, DEFAULT, NULL);
INSERT INTO book VALUES (DEFAULT, 'Властелин Колец: Возвращение короля', '978-5-17-133632-5', '2020', 20, DEFAULT, DEFAULT, DEFAULT, NULL);
INSERT INTO book VALUES (DEFAULT, 'Игра престолов', '978-5-17-114122-6', '2019', 20, DEFAULT, DEFAULT, DEFAULT, NULL);
INSERT INTO book VALUES (DEFAULT, 'Вторая жизнь Уве', '978-5-906837-24-0', '2016', 5, DEFAULT, DEFAULT, DEFAULT, NULL);
INSERT INTO book VALUES (DEFAULT, 'Крестный отец', '978-5-04-098842-6', '2021', 10, DEFAULT, DEFAULT, DEFAULT, NULL);
INSERT INTO book VALUES (DEFAULT, 'О всех созданиях – больших и малых', '978-5-389-17845-8', '2020', 3, DEFAULT, DEFAULT, DEFAULT, NULL);
INSERT INTO book VALUES (DEFAULT, 'Пустая могила', '978-5-04-090460-0', '2018', 1, DEFAULT, DEFAULT, DEFAULT, NULL);
INSERT INTO book VALUES (DEFAULT, 'Угрюм-река. Книга 1', '978-5-4444-5649-1', '2017', 1, DEFAULT, DEFAULT, DEFAULT, NULL);
INSERT INTO book VALUES (DEFAULT, 'Оправдание Острова', '978-5-17-134423-8', '2020', 3, DEFAULT, DEFAULT, DEFAULT, NULL);
INSERT INTO book VALUES (DEFAULT, 'Женщина, которая легла в кровать на год', '978-5-86471-687-8', '2014', 10, DEFAULT, DEFAULT, DEFAULT, NULL);
INSERT INTO book VALUES (DEFAULT, 'Дураки умирают', '978-5-699-46418-0', '2010', 4, DEFAULT, DEFAULT, DEFAULT, NULL);
INSERT INTO book VALUES (DEFAULT, 'Седьмая чаша', '978-5-389-18129-8', '2021', 5, DEFAULT, DEFAULT, DEFAULT, NULL);
INSERT INTO book VALUES (DEFAULT, 'Попугай Флобера', '978-5-389-11682-5', '2017', 9, DEFAULT, DEFAULT, DEFAULT, NULL);
INSERT INTO book VALUES (DEFAULT, 'Троя. Величайшее предание в пересказе', '978-5-86471-869-8', '2020', 13, DEFAULT, DEFAULT, DEFAULT, NULL);
INSERT INTO book VALUES (DEFAULT, 'Жнец-3. Итоги', '978-5-17-122856-9', '2021', 2, DEFAULT, DEFAULT, DEFAULT, NULL);
INSERT INTO book VALUES (DEFAULT, 'Last Word: Media Coverage of the Supreme Court of Canada', 0774812435, 2005, 5, DEFAULT, DEFAULT, DEFAULT, NULL);
INSERT INTO book VALUES (DEFAULT, 'Murder on a Mystery Tour', 0802756689, 2000, 5, DEFAULT, DEFAULT, DEFAULT, NULL);
INSERT INTO book VALUES (DEFAULT, 'Reel Murder: A Mystery', 0816144923, 1988, 5, DEFAULT, DEFAULT, DEFAULT, NULL);
INSERT INTO book VALUES (DEFAULT, 'Principles of Bloodstain Pattern Analysis: Theory and Practice', 0849320143, 2005, 5, DEFAULT, DEFAULT, DEFAULT, NULL);
INSERT INTO book VALUES (DEFAULT, 'The Encyclopedia of Crime Scene Investigation', 0816068151, 2007, 5, DEFAULT, DEFAULT, DEFAULT, NULL);
INSERT INTO book VALUES (DEFAULT, 'Software Forensics: Collecting Evidence from the Scene of a Digital Crime', 0071428046, 2004, 5, DEFAULT, DEFAULT, DEFAULT, NULL);
INSERT INTO book VALUES (DEFAULT, 'Fair Maiden', 0684192136, 1990, 5, DEFAULT, DEFAULT, DEFAULT, NULL);
INSERT INTO book VALUES (DEFAULT, 'The Rise and Decline of the Medici Bank: 1397-1494', 1893122328, 1999, 5, DEFAULT, DEFAULT, DEFAULT, NULL);
INSERT INTO book VALUES (DEFAULT, 'Architektur Denken', 3764374969, 2006, 5, DEFAULT, DEFAULT, DEFAULT, NULL);
INSERT INTO book VALUES (DEFAULT, 'The Last Valley: Dien Bien Phu and the French Defeat in Vietnam', 0306813866, 2004, 5, DEFAULT, DEFAULT, DEFAULT, NULL);
INSERT INTO book VALUES (DEFAULT, 'Calciumcarbonat: Von Der Kreidezeit Ins 21. Jahrhundert', 3764364246, 2000, 5, DEFAULT, DEFAULT, DEFAULT, NULL);
INSERT INTO book VALUES (DEFAULT, 'Office Mayhem: A Handbook to Practical Anarchy', 0810993872, 2008, 5, DEFAULT, DEFAULT, DEFAULT, NULL);
INSERT INTO book VALUES (DEFAULT, 'Selected Poems', 0140079858, 1985, 5, DEFAULT, DEFAULT, DEFAULT, NULL);
INSERT INTO book VALUES (DEFAULT, 'The Power Broker: Robert Moses and the Fall of New York', 0394480767, 1974, 5, DEFAULT, DEFAULT, DEFAULT, NULL);
INSERT INTO book VALUES (DEFAULT, 'The Holocaust: The Destruction of European Jewry 1933-1945', 0805203761, 1973, 5, DEFAULT, DEFAULT, DEFAULT, NULL);
INSERT INTO book VALUES (DEFAULT, 'London: the Biography', 1856197166, 2000, 5, DEFAULT, DEFAULT, DEFAULT, NULL);
INSERT INTO book VALUES (DEFAULT, 'Passages: Photographs in Africa', 0810929481, 2000, 5, DEFAULT, DEFAULT, DEFAULT, NULL);
INSERT INTO book VALUES (DEFAULT, 'The  Cowgirls', 0929398157, 1990, 5, DEFAULT, DEFAULT, DEFAULT, NULL);
INSERT INTO book VALUES (DEFAULT, 'Communicating the Infinite: The Emergence of the Habad School', 0226490459, 1990, 5, DEFAULT, DEFAULT, DEFAULT, NULL);
INSERT INTO book VALUES (DEFAULT, 'The Battle for New York: The City at the Heart of the American Revolution', 0802713742, 2002, 5, DEFAULT, DEFAULT, DEFAULT, NULL);

-- AUTHORS --
INSERT INTO author VALUES(DEFAULT, 'Джоан Кэтлин Роулинг', DEFAULT, NULL);
INSERT INTO author VALUES(DEFAULT, 'Стивен Кинг', DEFAULT, NULL);
INSERT INTO author VALUES(DEFAULT, 'Маргарет Митчелл', DEFAULT, NULL);
INSERT INTO author VALUES(DEFAULT, 'Артур Конан Дойл', DEFAULT, NULL);
INSERT INTO author VALUES(DEFAULT, 'Кэтрин Стокетт', DEFAULT, NULL);
INSERT INTO author VALUES(DEFAULT, 'Александр Дюма', DEFAULT, NULL);
INSERT INTO author VALUES(DEFAULT, 'Джон Р. Р. Толкин', DEFAULT, NULL);
INSERT INTO author VALUES(DEFAULT, 'Джордж Мартин', DEFAULT, NULL);
INSERT INTO author VALUES(DEFAULT, 'Фредрик Бакман', DEFAULT, NULL);
INSERT INTO author VALUES(DEFAULT, 'Марио Пьюзо', DEFAULT, NULL);
INSERT INTO author VALUES(DEFAULT, 'Джеймс Хэрриот', DEFAULT, NULL);
INSERT INTO author VALUES(DEFAULT, 'Джонатан Страуд', DEFAULT, NULL);
INSERT INTO author VALUES(DEFAULT, 'Вячеслав Шишков', DEFAULT, NULL);
INSERT INTO author VALUES(DEFAULT, 'Евгений Водолазкин', DEFAULT, NULL);
INSERT INTO author VALUES(DEFAULT, 'Сью Таунсенд', DEFAULT, NULL);
INSERT INTO author VALUES(DEFAULT, 'К. Дж. Сэнсом', DEFAULT, NULL);
INSERT INTO author VALUES(DEFAULT, 'Джулиан Барнс', DEFAULT, NULL);
INSERT INTO author VALUES(DEFAULT, 'Стивен Фрай', DEFAULT, NULL);
INSERT INTO author VALUES(DEFAULT, 'Нил Шустерман', DEFAULT, NULL);
INSERT INTO author VALUES (DEFAULT, 'Florian Sauvageau', DEFAULT, NULL);
INSERT INTO author VALUES (DEFAULT, 'Marian Babson', DEFAULT, NULL);


INSERT INTO author VALUES (DEFAULT, 'Stuart H. James', DEFAULT, NULL);
INSERT INTO author VALUES (DEFAULT, 'Michael Newton', DEFAULT, NULL);
INSERT INTO author VALUES (DEFAULT, 'Robert Slade', DEFAULT, NULL);
INSERT INTO author VALUES (DEFAULT, 'Lynn Hall', DEFAULT, NULL);
INSERT INTO author VALUES (DEFAULT, 'Raymond de Roover', DEFAULT, NULL);
INSERT INTO author VALUES (DEFAULT, 'Peter Zumthor', DEFAULT, NULL);
INSERT INTO author VALUES (DEFAULT, 'Martin Windrow', DEFAULT, NULL);
INSERT INTO author VALUES (DEFAULT, 'Wolfgang F. Tegethoff', DEFAULT, NULL);
INSERT INTO author VALUES (DEFAULT, 'Juliette Cezzar', DEFAULT, NULL);
INSERT INTO author VALUES (DEFAULT, 'Rabindranath Tagore', DEFAULT, NULL);
INSERT INTO author VALUES (DEFAULT, 'Robert A. Caro', DEFAULT, NULL);
INSERT INTO author VALUES (DEFAULT, 'Nora Levin', DEFAULT, NULL);
INSERT INTO author VALUES (DEFAULT, 'Peter Ackroyd', DEFAULT, NULL);
INSERT INTO author VALUES (DEFAULT, 'Carol Beckwith', DEFAULT, NULL);
INSERT INTO author VALUES (DEFAULT, 'Joyce Gibson Roach', DEFAULT, NULL);
INSERT INTO author VALUES (DEFAULT, 'Naftali Loewenthal', DEFAULT, NULL);
INSERT INTO author VALUES (DEFAULT, 'Barnet Schecter', DEFAULT, NULL);

-- BOOKS AUTHORS --
INSERT INTO book_author VALUES (1, 1);
INSERT INTO book_author VALUES (2, 2);
INSERT INTO book_author VALUES (3, 3);
INSERT INTO book_author VALUES (4, 4);
INSERT INTO book_author VALUES (5, 5);
INSERT INTO book_author VALUES (6, 6);
INSERT INTO book_author VALUES (7, 7);
INSERT INTO book_author VALUES (8, 8);
INSERT INTO book_author VALUES (9, 9);
INSERT INTO book_author VALUES (10, 10);
INSERT INTO book_author VALUES (11, 11);
INSERT INTO book_author VALUES (12, 12);
INSERT INTO book_author VALUES (13, 13);
INSERT INTO book_author VALUES (14, 14);
INSERT INTO book_author VALUES (15, 15);
INSERT INTO book_author VALUES (16, 10);
INSERT INTO book_author VALUES (17, 16);
INSERT INTO book_author VALUES (18, 17);
INSERT INTO book_author VALUES (19, 18);
INSERT INTO book_author VALUES (20, 19);
INSERT INTO book_author VALUES (21, 20);
INSERT INTO book_author VALUES (22, 21);
INSERT INTO book_author VALUES (23, 21);
INSERT INTO book_author VALUES (24, 22);
INSERT INTO book_author VALUES (25, 23);
INSERT INTO book_author VALUES (26, 24);
INSERT INTO book_author VALUES (27, 25);
INSERT INTO book_author VALUES (28, 26);
INSERT INTO book_author VALUES (29, 27);
INSERT INTO book_author VALUES (30, 28);
INSERT INTO book_author VALUES (31, 29);
INSERT INTO book_author VALUES (32, 30);
INSERT INTO book_author VALUES (33, 31);
INSERT INTO book_author VALUES (34, 32);
INSERT INTO book_author VALUES (35, 33);
INSERT INTO book_author VALUES (36, 34);
INSERT INTO book_author VALUES (37, 35);
INSERT INTO book_author VALUES (38, 36);
INSERT INTO book_author VALUES (39, 37);
INSERT INTO book_author VALUES (40, 38);