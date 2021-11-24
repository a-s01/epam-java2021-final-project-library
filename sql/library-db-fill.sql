-- Fill DB with test values --
-- USE `library-app-testdb`;
USE `library-app`;

-- user --
INSERT INTO user VALUES (1, 'admin@gmail.com', '���
(�"�M��έ�����=�_��Z~�vk��ߪ�aX��c���_��%;[��+�/�', 'ҽ˲�<K���~�7P&đv[', 'admin', DEFAULT, DEFAULT, 'admin', 1, DEFAULT);
INSERT INTO user VALUES (2, 'librarian@gmail.com', '���
(�"�M��έ�����=�_��Z~�vk��ߪ�aX��c���_��%;[��+�/�', 'ҽ˲�<K���~�7P&đv[', 'librarian', DEFAULT, DEFAULT, 'librarian', 1, DEFAULT);
INSERT INTO user VALUES (3, 'user@gmail.com', '���
(�"�M��έ�����=�_��Z~�vk��ߪ�aX��c���_��%;[��+�/�', 'ҽ˲�<K���~�7P&đv[', 'user', DEFAULT, DEFAULT, 'користувач', 2, DEFAULT);
INSERT INTO user VALUES (DEFAULT, 'test@gmail.com', '���
(�"�M��έ�����=�_��Z~�vk��ߪ�aX��c���_��%;[��+�/�', 'ҽ˲�<K���~�7P&đv[', DEFAULT, DEFAULT, DEFAULT, 'user test', 1, DEFAULT);

-- BOOK & BOOK_STAT --
INSERT INTO book VALUES (1, 'Гарри Поттер. Полное собрание (комплект из 7 книг) (сборник)', '978-5-389-10668-0', '2016', 'ru', @DEFAULT_KEEP_PERIOD, DEFAULT);
INSERT INTO book_stat VALUES (1, 20, DEFAULT, DEFAULT, DEFAULT);
INSERT INTO book VALUES (2, 'Зеленая миля', '978-5-17-118362-2', '2020', 'ru', @DEFAULT_KEEP_PERIOD, DEFAULT);
INSERT INTO book_stat VALUES (2, 20, DEFAULT, DEFAULT, DEFAULT);
INSERT INTO book VALUES (3, 'Унесенные ветром', '978-5-389-17583-9', '2020', 'ru', @DEFAULT_KEEP_PERIOD, DEFAULT);
INSERT INTO book_stat VALUES (3, 20, DEFAULT, DEFAULT, DEFAULT);
INSERT INTO book VALUES (4, 'Полное собрание произведений о Шерлоке Холмсе в одном томе (сборник)', '978-5-93556-958-7', '2015', 'ru', @DEFAULT_KEEP_PERIOD, DEFAULT);
INSERT INTO book_stat VALUES (4, 20, DEFAULT, DEFAULT, DEFAULT);
INSERT INTO book VALUES (5, 'Прислуга', '978-5-86471-732-5', '2016', 'ru', @DEFAULT_KEEP_PERIOD, DEFAULT);
INSERT INTO book_stat VALUES (5, 20, DEFAULT, DEFAULT, DEFAULT);
INSERT INTO book VALUES (6, 'Граф Монте-Кристо', '978-5-04-117008-0', '2021', 'ru', @DEFAULT_KEEP_PERIOD, DEFAULT);
INSERT INTO book_stat VALUES (6, 20, DEFAULT, DEFAULT, DEFAULT);
INSERT INTO book VALUES (7, 'Властелин Колец: Возвращение короля', '978-5-17-133632-5', '2020', 'ru', @DEFAULT_KEEP_PERIOD, DEFAULT);
INSERT INTO book_stat VALUES (7, 20, DEFAULT, DEFAULT, DEFAULT);
INSERT INTO book VALUES (8, 'Игра престолов', '978-5-17-114122-6', '2019', 'ru', @DEFAULT_KEEP_PERIOD, DEFAULT);
INSERT INTO book_stat VALUES (8, 20, DEFAULT, DEFAULT, DEFAULT);
INSERT INTO book VALUES (9, 'Вторая жизнь Уве', '978-5-906837-24-0', '2016', 'ru', @DEFAULT_KEEP_PERIOD, DEFAULT);
INSERT INTO book_stat VALUES (9, 20, DEFAULT, DEFAULT, DEFAULT);
INSERT INTO book VALUES (10, 'Крестный отец', '978-5-04-098842-6', '2021', 'ru', @DEFAULT_KEEP_PERIOD, DEFAULT);
INSERT INTO book_stat VALUES (10, 20, DEFAULT, DEFAULT, DEFAULT);
INSERT INTO book VALUES (11, 'О всех созданиях – больших и малых', '978-5-389-17845-8', '2020', 'ru', @DEFAULT_KEEP_PERIOD, DEFAULT);
INSERT INTO book_stat VALUES (11, 5, DEFAULT, DEFAULT, DEFAULT);
INSERT INTO book VALUES (12, 'Пустая могила', '978-5-04-090460-0', '2018', 'ru', @DEFAULT_KEEP_PERIOD, DEFAULT);
INSERT INTO book_stat VALUES (12, 5, DEFAULT, DEFAULT, DEFAULT);
INSERT INTO book VALUES (13, 'Угрюм-река. Книга 1', '978-5-4444-5649-1', '2017', 'ru', @DEFAULT_KEEP_PERIOD, DEFAULT);
INSERT INTO book_stat VALUES (13, 5, DEFAULT, DEFAULT, DEFAULT);
INSERT INTO book VALUES (14, 'Оправдание Острова', '978-5-17-134423-8', '2020', 'ru', @DEFAULT_KEEP_PERIOD, DEFAULT);
INSERT INTO book_stat VALUES (14, 5, DEFAULT, DEFAULT, DEFAULT);
INSERT INTO book VALUES (15, 'Женщина, которая легла в кровать на год', '978-5-86471-687-8', '2014', 'ru', @DEFAULT_KEEP_PERIOD, DEFAULT);
INSERT INTO book_stat VALUES (15, 20, DEFAULT, DEFAULT, DEFAULT);
INSERT INTO book VALUES (16, 'Дураки умирают', '978-5-699-46418-0', '2010', 'ru', @DEFAULT_KEEP_PERIOD, DEFAULT);
INSERT INTO book_stat VALUES (16, 20, DEFAULT, DEFAULT, DEFAULT);
INSERT INTO book VALUES (17, 'Седьмая чаша', '978-5-389-18129-8', '2021', 'ru', @DEFAULT_KEEP_PERIOD, DEFAULT);
INSERT INTO book_stat VALUES (17, 20, DEFAULT, DEFAULT, DEFAULT);
INSERT INTO book VALUES (18, 'Попугай Флобера', '978-5-389-11682-5', '2017', 'ru', @DEFAULT_KEEP_PERIOD, DEFAULT);
INSERT INTO book_stat VALUES (18, 20, DEFAULT, DEFAULT, DEFAULT);
INSERT INTO book VALUES (19, 'Троя. Величайшее предание в пересказе', '978-5-86471-869-8', '2020', 'ru', @DEFAULT_KEEP_PERIOD, DEFAULT);
INSERT INTO book_stat VALUES (19, 3, DEFAULT, DEFAULT, DEFAULT);
INSERT INTO book VALUES (20, 'Жнец-3. Итоги', '978-5-17-122856-9', '2021', 'ru', @DEFAULT_KEEP_PERIOD, DEFAULT);
INSERT INTO book_stat VALUES (20, 1, DEFAULT, DEFAULT, DEFAULT);
INSERT INTO book VALUES (21, 'Last Word: Media Coverage of the Supreme Court of Canada', 0774812435, 2005, 'en', @DEFAULT_KEEP_PERIOD, DEFAULT);
INSERT INTO book_stat VALUES (21, 20, DEFAULT, DEFAULT, DEFAULT);
INSERT INTO book VALUES (22, 'Murder on a Mystery Tour', 0802756689, 2000, 1, @DEFAULT_KEEP_PERIOD, DEFAULT);
INSERT INTO book_stat VALUES (22, 20, DEFAULT, DEFAULT, DEFAULT);
INSERT INTO book VALUES (23, 'Reel Murder: A Mystery', 0816144923, 1988, 1, @DEFAULT_KEEP_PERIOD, DEFAULT);
INSERT INTO book_stat VALUES (23, 20, DEFAULT, DEFAULT, DEFAULT);
INSERT INTO book VALUES (24, 'Principles of Bloodstain Pattern Analysis: Theory and Practice', 0849320143, 2005, 1, @DEFAULT_KEEP_PERIOD, DEFAULT);
INSERT INTO book_stat VALUES (24, 20, DEFAULT, DEFAULT, DEFAULT);
INSERT INTO book VALUES (25, 'The Encyclopedia of Crime Scene Investigation', 0816068151, 2007, 1, @DEFAULT_KEEP_PERIOD, DEFAULT);
INSERT INTO book_stat VALUES (25, 20, DEFAULT, DEFAULT, DEFAULT);
INSERT INTO book VALUES (26, 'Software Forensics: Collecting Evidence from the Scene of a Digital Crime', 0071428046, 2004, 1, @DEFAULT_KEEP_PERIOD, DEFAULT);
INSERT INTO book_stat VALUES (26, 20, DEFAULT, DEFAULT, DEFAULT);
INSERT INTO book VALUES (27, 'Fair Maiden', 0684192136, 1990, 1, @DEFAULT_KEEP_PERIOD, DEFAULT);
INSERT INTO book_stat VALUES (27, 20, DEFAULT, DEFAULT, DEFAULT);
INSERT INTO book VALUES (28, 'The Rise and Decline of the Medici Bank: 1397-1494', 1893122328, 1999, 1, @DEFAULT_KEEP_PERIOD, DEFAULT);
INSERT INTO book_stat VALUES (28, 20, DEFAULT, DEFAULT, DEFAULT);
INSERT INTO book VALUES (29, 'Architektur Denken', 3764374969, 2006, 1, @DEFAULT_KEEP_PERIOD, DEFAULT);
INSERT INTO book_stat VALUES (29, 20, DEFAULT, DEFAULT, DEFAULT);
INSERT INTO book VALUES (30, 'The Last Valley: Dien Bien Phu and the French Defeat in Vietnam', 0306813866, 2004, 1, @DEFAULT_KEEP_PERIOD, DEFAULT);
INSERT INTO book_stat VALUES (30, 20, DEFAULT, DEFAULT, DEFAULT);
INSERT INTO book VALUES (31, 'Calciumcarbonat: Von Der Kreidezeit Ins 21. Jahrhundert', 3764364246, 2000, 1, @DEFAULT_KEEP_PERIOD, DEFAULT);
INSERT INTO book_stat VALUES (31, 20, DEFAULT, DEFAULT, DEFAULT);
INSERT INTO book VALUES (32, 'Office Mayhem: A Handbook to Practical Anarchy', 0810993872, 2008, 1, @DEFAULT_KEEP_PERIOD, DEFAULT);
INSERT INTO book_stat VALUES (32, 20, DEFAULT, DEFAULT, DEFAULT);
INSERT INTO book VALUES (33, 'Selected Poems', 0140079858, 1985, 1, @DEFAULT_KEEP_PERIOD, DEFAULT);
INSERT INTO book_stat VALUES (33, 20, DEFAULT, DEFAULT, DEFAULT);
INSERT INTO book VALUES (34, 'The Power Broker: Robert Moses and the Fall of New York', 0394480767, 1974, 1, @DEFAULT_KEEP_PERIOD, DEFAULT);
INSERT INTO book_stat VALUES (34, 20, DEFAULT, DEFAULT, DEFAULT);
INSERT INTO book VALUES (35, 'The Holocaust: The Destruction of European Jewry 1933-1945', 0805203761, 1973, 1, @DEFAULT_KEEP_PERIOD, DEFAULT);
INSERT INTO book_stat VALUES (35, 20, DEFAULT, DEFAULT, DEFAULT);
INSERT INTO book VALUES (36, 'London: the Biography', 1856197166, 2000, 1, @DEFAULT_KEEP_PERIOD, DEFAULT);
INSERT INTO book_stat VALUES (36, 20, DEFAULT, DEFAULT, DEFAULT);
INSERT INTO book VALUES (37, 'Passages: Photographs in Africa', 0810929481, 2000, 1, @DEFAULT_KEEP_PERIOD, DEFAULT);
INSERT INTO book_stat VALUES (37, 20, DEFAULT, DEFAULT, DEFAULT);
INSERT INTO book VALUES (38, 'The Cowgirls', 0929398157, 1990, 1, @DEFAULT_KEEP_PERIOD, DEFAULT);
INSERT INTO book_stat VALUES (38, 20, DEFAULT, DEFAULT, DEFAULT);
INSERT INTO book VALUES (39, 'Communicating the Infinite: The Emergence of the Habad School', 0226490459, 1990, 1, @DEFAULT_KEEP_PERIOD, DEFAULT);
INSERT INTO book_stat VALUES (39, 20, DEFAULT, DEFAULT, DEFAULT);
INSERT INTO book VALUES (40, 'The Battle for New York: The City at the Heart of the American Revolution', 0802713742, 2002, 1, @DEFAULT_KEEP_PERIOD, DEFAULT);
INSERT INTO book_stat VALUES (40, 20, DEFAULT, DEFAULT, DEFAULT);

-- AUTHORS --
INSERT INTO author VALUES (DEFAULT, 'Джоан Кэтлин Роулинг', DEFAULT);
INSERT INTO author VALUES (DEFAULT, 'Стивен Кинг', DEFAULT);
INSERT INTO author VALUES (DEFAULT, 'Маргарет Митчелл', DEFAULT);
INSERT INTO author VALUES (DEFAULT, 'Артур Конан Дойл', DEFAULT);
INSERT INTO author VALUES (DEFAULT, 'Кэтрин Стокетт', DEFAULT);
INSERT INTO author VALUES (DEFAULT, 'Александр Дюма', DEFAULT);
INSERT INTO author VALUES (DEFAULT, 'Джон Р. Р. Толкин', DEFAULT);
INSERT INTO author VALUES (DEFAULT, 'Джордж Мартин', DEFAULT);
INSERT INTO author VALUES (DEFAULT, 'Фредрик Бакман', DEFAULT);
INSERT INTO author VALUES (DEFAULT, 'Марио Пьюзо', DEFAULT);
INSERT INTO author VALUES (DEFAULT, 'Джеймс Хэрриот', DEFAULT);
INSERT INTO author VALUES (DEFAULT, 'Джонатан Страуд', DEFAULT);
INSERT INTO author VALUES (DEFAULT, 'Вячеслав Шишков', DEFAULT);
INSERT INTO author VALUES (DEFAULT, 'Евгений Водолазкин', DEFAULT);
INSERT INTO author VALUES (DEFAULT, 'Сью Таунсенд', DEFAULT);
INSERT INTO author VALUES (DEFAULT, 'К. Дж. Сэнсом', DEFAULT);
INSERT INTO author VALUES (DEFAULT, 'Джулиан Барнс', DEFAULT);
INSERT INTO author VALUES (DEFAULT, 'Стивен Фрай', DEFAULT);
INSERT INTO author VALUES (DEFAULT, 'Нил Шустерман', DEFAULT);
INSERT INTO author VALUES (DEFAULT, 'Florian Sauvageau', DEFAULT);
INSERT INTO author VALUES (DEFAULT, 'Marian Babson', DEFAULT);
INSERT INTO author VALUES (DEFAULT, 'Stuart H. James', DEFAULT);
INSERT INTO author VALUES (DEFAULT, 'Michael Newton', DEFAULT);
INSERT INTO author VALUES (DEFAULT, 'Robert Slade', DEFAULT);
INSERT INTO author VALUES (DEFAULT, 'Lynn Hall', DEFAULT);
INSERT INTO author VALUES (DEFAULT, 'Raymond de Roover', DEFAULT);
INSERT INTO author VALUES (DEFAULT, 'Peter Zumthor', DEFAULT);
INSERT INTO author VALUES (DEFAULT, 'Martin Windrow', DEFAULT);
INSERT INTO author VALUES (DEFAULT, 'Wolfgang F. Tegethoff', DEFAULT);
INSERT INTO author VALUES (DEFAULT, 'Juliette Cezzar', DEFAULT);
INSERT INTO author VALUES (DEFAULT, 'Rabindranath Tagore', DEFAULT);
INSERT INTO author VALUES (DEFAULT, 'Robert A. Caro', DEFAULT);
INSERT INTO author VALUES (DEFAULT, 'Nora Levin', DEFAULT);
INSERT INTO author VALUES (DEFAULT, 'Peter Ackroyd', DEFAULT);
INSERT INTO author VALUES (DEFAULT, 'Carol Beckwith', DEFAULT);
INSERT INTO author VALUES (DEFAULT, 'Joyce Gibson Roach', DEFAULT);
INSERT INTO author VALUES (DEFAULT, 'Naftali Loewenthal', DEFAULT);
INSERT INTO author VALUES (DEFAULT, 'Barnet Schecter', DEFAULT);

-- AUTHOR NAMES --
INSERT INTO author_name_i18n VALUES (2, 1, 'Джоан Кэтлин Роулинг');
INSERT INTO author_name_i18n VALUES (1, 1, 'Joanne Kathleen Rowling');
INSERT INTO author_name_i18n VALUES (2, 2, 'Стивен Кинг');
INSERT INTO author_name_i18n VALUES (2, 3, 'Маргарет Митчелл');
INSERT INTO author_name_i18n VALUES (2, 4, 'Артур Конан Дойл');
INSERT INTO author_name_i18n VALUES (2, 5, 'Кэтрин Стокетт');
INSERT INTO author_name_i18n VALUES (2, 6, 'Александр Дюма');
INSERT INTO author_name_i18n VALUES (2, 7, 'Джон Р. Р. Толкин');
INSERT INTO author_name_i18n VALUES (2, 8, 'Джордж Мартин');
INSERT INTO author_name_i18n VALUES (2, 9, 'Фредрик Бакман');
INSERT INTO author_name_i18n VALUES (2, 10, 'Марио Пьюзо');
INSERT INTO author_name_i18n VALUES (2, 11, 'Джеймс Хэрриот');
INSERT INTO author_name_i18n VALUES (2, 12, 'Джонатан Страуд');
INSERT INTO author_name_i18n VALUES (2, 13, 'Вячеслав Шишков');
INSERT INTO author_name_i18n VALUES (2, 14, 'Евгений Водолазкин');
INSERT INTO author_name_i18n VALUES (2, 15, 'Сью Таунсенд');
INSERT INTO author_name_i18n VALUES (2, 16, 'К. Дж. Сэнсом');
INSERT INTO author_name_i18n VALUES (2, 17, 'Джулиан Барнс');
INSERT INTO author_name_i18n VALUES (2, 18, 'Стивен Фрай');
INSERT INTO author_name_i18n VALUES (2, 19, 'Нил Шустерман');
INSERT INTO author_name_i18n VALUES (1, 20, 'Florian Sauvageau');
INSERT INTO author_name_i18n VALUES (1, 21, 'Marian Babson');
INSERT INTO author_name_i18n VALUES (1, 22, 'Stuart H. James');
INSERT INTO author_name_i18n VALUES (1, 23, 'Michael Newton');
INSERT INTO author_name_i18n VALUES (1, 24, 'Robert Slade');
INSERT INTO author_name_i18n VALUES (1, 25, 'Lynn Hall');
INSERT INTO author_name_i18n VALUES (1, 26, 'Raymond de Roover');
INSERT INTO author_name_i18n VALUES (1, 27, 'Peter Zumthor');
INSERT INTO author_name_i18n VALUES (1, 28, 'Martin Windrow');
INSERT INTO author_name_i18n VALUES (1, 29, 'Wolfgang F. Tegethoff');
INSERT INTO author_name_i18n VALUES (1, 30, 'Juliette Cezzar');
INSERT INTO author_name_i18n VALUES (1, 31, 'Rabindranath Tagore');
INSERT INTO author_name_i18n VALUES (1, 32, 'Robert A. Caro');
INSERT INTO author_name_i18n VALUES (1, 33, 'Nora Levin');
INSERT INTO author_name_i18n VALUES (1, 34, 'Peter Ackroyd');
INSERT INTO author_name_i18n VALUES (1, 35, 'Carol Beckwith');
INSERT INTO author_name_i18n VALUES (1, 36, 'Joyce Gibson Roach');
INSERT INTO author_name_i18n VALUES (1, 37, 'Naftali Loewenthal');
INSERT INTO author_name_i18n VALUES (1, 38, 'Barnet Schecter');

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
