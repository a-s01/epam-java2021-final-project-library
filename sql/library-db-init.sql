-- COMMENTS --
-- mysql 8.0 supports CHECK CONSTRAINS and enforces them by default (https://dev.mysql.com/doc/refman/8.0/en/create-table-check-constraints.html)
-- they are used here, so make sure mysql version is >= 8.0
-- For ids INT UNSIGNED is used, should be represented as long in java code

SET @DEFAULT_KEEP_PERIOD = 14;

-- ---------------------------------------------
-- SCHEMA --
-- DROP SCHEMA IF EXISTS `library-app`;
CREATE SCHEMA IF NOT EXISTS `library-app` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE `library-app`;

-- ---------------------------------------------
-- DROP ALL TABLES --
-- Triggers for a table are also dropped if you drop the table.
SET FOREIGN_KEY_CHECKS=0; -- for being able to delete cross-referenced tables
DROP TABLE IF EXISTS book_in_booking;
DROP TABLE IF EXISTS booking;
DROP TABLE IF EXISTS book_author;
DROP TABLE IF EXISTS book;
DROP TABLE IF EXISTS book_stat;
DROP TABLE IF EXISTS author_name_i18n;
DROP TABLE IF EXISTS author;
DROP TABLE IF EXISTS user;
DROP TABLE IF EXISTS lang;
SET FOREIGN_KEY_CHECKS=1;

-- ---------------------------------------------
-- CREATE ALL TABLES
-- ---------------------------------------------
-- TABLE language --
CREATE TABLE lang (
	PRIMARY KEY (id),
    id			INT UNSIGNED	NOT NULL	AUTO_INCREMENT, -- default app lang should be 1
    code	 	VARCHAR(2)		NOT NULL,	-- ISO 639-1 code
				UNIQUE INDEX (code)
);

-- TABLE user --
-- instead of role table ENUM inside user table is used to better aligns with java code. 
-- Role must be not null and '' means UNKNOWN role.
-- User with fine > 0 cannot create a new booking
-- User with fine > 0 cannot be deleted
-- User holding book (their booking state = 'delivered') cannot be deleted
-- If user is deleted, all their booking in state 'new' are canceled 
CREATE TABLE user (
	PRIMARY KEY (id),
	id					INT	UNSIGNED						NOT NULL	AUTO_INCREMENT,
    email				VARCHAR(50) 						NOT NULL,
						UNIQUE INDEX (email),
						CONSTRAINT valid_user_email
							CHECK (email RLIKE '^[[:alnum:]]+@[[:alnum:]\\.]+\\.[[:alpha:]]{2,6}$'), -- TODO should be checked and updated (add utf8 support)
	password			CHAR(128)							NOT NULL,
    salt				CHAR(50)							NOT NULL,
    role				ENUM('USER', 'LIBRARIAN', 'ADMIN')	NOT NULL,	-- by default mysql behaviour first enum value will be default value 
																	-- for column. DON'T put 'admin' first!
    state				ENUM('VALID', 'BLOCKED', 'DELETED')	NOT NULL,
    fine				DECIMAL(9,2)						NOT NULL	DEFAULT (0),
						CONSTRAINT
							CHECK (fine >= 0),
    name				VARCHAR(50),
    preferred_lang_id	INT UNSIGNED						NOT NULL,
						FOREIGN KEY (preferred_lang_id)
							REFERENCES lang (id)
							ON DELETE RESTRICT
							ON UPDATE CASCADE,
    modified			DATETIME							NOT NULL	DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	fine_last_checked	DATETIME							NOT NULL	DEFAULT CURRENT_TIMESTAMP
)
AUTO_INCREMENT = 100; -- reserved space for system user

-- TABLE author --
CREATE TABLE author (
	PRIMARY KEY (id),
	id				INT UNSIGNED			NOT NULL	AUTO_INCREMENT,
    name			VARCHAR(50)				NOT NULL,	-- fallback name in case no lang was found
					UNIQUE INDEX (name),
    modified		DATETIME				NOT NULL	DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- TABLE author_names --
CREATE TABLE author_name_i18n (
	PRIMARY KEY (lang_id, author_id),
    lang_id		INT UNSIGNED	NOT NULL,
				FOREIGN KEY (lang_id)
					REFERENCES lang (id)
					ON DELETE CASCADE
					ON UPDATE CASCADE,
	author_id	INT UNSIGNED	NOT NULL,
				FOREIGN KEY (author_id)
                    REFERENCES author (id)
                    ON DELETE CASCADE
                    ON UPDATE CASCADE,
	name		VARCHAR(50)		NOT NULL
);

-- TABLE book --
-- If book is added to booking, in_stock decreased by 1
-- If book was in booking, and its booking state marked as delivered was_booked_times increases by 1
CREATE TABLE book (
	PRIMARY KEY (id),
    id					INT UNSIGNED			NOT NULL	AUTO_INCREMENT,
    title				VARCHAR(256)			NOT NULL,					
    isbn				VARCHAR(17)				NOT NULL, 	-- 13 digits + 4 '-'
						UNIQUE INDEX (isbn),
    year				YEAR					NOT NULL,
    lang_code			VARCHAR(2)				NOT NULL, 	-- valid ISO 639-1 code, any, NOT only from the app supported lang
	keep_period			INT UNSIGNED			NOT NULL,	-- period of time in days book is allowed to be kept by user                        
    modified			DATETIME				NOT NULL	DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE book_stat (
	PRIMARY KEY (book_id),
	book_id				INT UNSIGNED			NOT NULL,
						FOREIGN KEY (book_id)
                            REFERENCES book (id)
                            ON DELETE CASCADE
                            ON UPDATE CASCADE,
	total				INT UNSIGNED			NOT NULL,	-- how many books library has in total, including available and not available for booking
						INDEX (total),
                        CONSTRAINT
							CHECK (total >= 0),
	in_stock			INT UNSIGNED			NOT NULL	DEFAULT (total), 	-- how many book are in the library
						CONSTRAINT
							CHECK (in_stock <= total),
	reserved			INT UNSIGNED			NOT NULL 	DEFAULT (0),				-- how many books are reserved by user (in state booked)
						CONSTRAINT
							CHECK (reserved <= in_stock),
    times_was_booked	INT UNSIGNED			NOT NULL	DEFAULT (0)  				-- how many times book was booked and this booking was in state 'delivered'
);    
    
-- TABLE book_author --
-- allows multiple author for book
CREATE TABLE book_author (
	PRIMARY KEY (book_id, author_id),
	book_id		INT UNSIGNED				NOT NULL,
				FOREIGN KEY (book_id)
					REFERENCES book (id)
					ON DELETE CASCADE
					ON UPDATE CASCADE,
    author_id	INT UNSIGNED				NOT NULL,
				FOREIGN KEY (author_id)
					REFERENCES author (id)
					ON DELETE RESTRICT
					ON UPDATE CASCADE
);

-- TABLE booking --
-- booking made by user (should not be available for admins or librarian -- business logic ensures).
-- user deleting cause deleting of the booking. Prevent deleting user holding a book
-- If user is deleted, all their booking in state 'new' are canceled
-- User with fine > 0 cannot create a new booking
-- booking in state 'new' cannot be moved to state 'done'
-- booking in state 'delivered' cannot be moved to state 'new'
-- booking in state 'done/canceled' cannot be moved to any new state
CREATE TABLE booking (
	PRIMARY KEY (id),
	id				INT UNSIGNED		NOT NULL	AUTO_INCREMENT,
    user_id 		INT UNSIGNED		NOT NULL,
					FOREIGN KEY (user_id)
						REFERENCES user (id)
						ON DELETE CASCADE
						ON UPDATE CASCADE,
    state			ENUM('NEW', 'BOOKED', 'DELIVERED','DONE','CANCELED') NOT NULL,
    located			ENUM('LIBRARY', 'USER')					  NOT NULL,			
	modified		DATETIME			NOT NULL	DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- TABLE book_in_booking --
-- let's assume one user can take only one same book (can barely imagine other case)
-- adding book here means available book amount is decreased by one
CREATE TABLE book_in_booking (
	PRIMARY KEY (booking_id, book_id),
    booking_id	INT UNSIGNED				NOT NULL,
				FOREIGN KEY (booking_id)
					REFERENCES booking (id)
					ON DELETE CASCADE
					ON UPDATE CASCADE,
    book_id		INT UNSIGNED				NOT NULL,
				FOREIGN KEY (book_id)
					REFERENCES book (id)
					ON DELETE CASCADE
					ON UPDATE CASCADE
);

-- DEFAULT VALUES --
-- LANG --
INSERT INTO lang VALUES (DEFAULT, 'en');
INSERT INTO lang VALUES (DEFAULT, 'ru');