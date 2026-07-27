DELETE FROM film_likes;
DELETE FROM friends_user;
DELETE FROM film_multi_genre;
DELETE FROM films;
DELETE FROM users;
DELETE FROM status;
DELETE FROM genre;
DELETE FROM rating_film;

ALTER TABLE rating_film ALTER COLUMN rating_film_id RESTART WITH 1;

ALTER TABLE genre ALTER COLUMN genre_id RESTART WITH 1;

ALTER TABLE status ALTER COLUMN status_id RESTART WITH 1;

ALTER TABLE films ALTER COLUMN film_id RESTART WITH 1;

ALTER TABLE film_multi_genre ALTER COLUMN film_multi_genre_id RESTART WITH 1;

ALTER TABLE users ALTER COLUMN user_id RESTART WITH 1;

ALTER TABLE friends_user ALTER COLUMN friends_user_id RESTART WITH 1;

ALTER TABLE film_likes ALTER COLUMN film_likes_id RESTART WITH 1;

INSERT INTO rating_film (name, description) VALUES
('G', 'Для всех возрастов'),
('PG', 'Рекомендуется присутствие родителей'),
('PG-13', 'Детям до 13 лет не рекомендуется'),
('R', 'До 17 лет только в сопровождении взрослых'),
('NC-17', 'Только для взрослых');

INSERT INTO genre (genre_id, name) VALUES
(1, 'Комедия'),
(2, 'Драма'),
(3, 'Мультфильм'),
(4, 'Триллер'),
(5, 'Документальный'),
(6, 'Боевик');

INSERT INTO status (status_id, name) VALUES
(1, 'ожидание'),
(2, 'подтверждён');

INSERT INTO films (name, description, releaseDate, duration, rating_id) VALUES
('Побег из Шоушенка', 'Два заключённых сближаются за 20 лет в тюрьме', '1994-09-23 00:00:00', 142, 1),
('Крёстный отец', 'История семьи Корлеоне', '1972-03-24 00:00:00', 175, 2),
('Тёмный рыцарь', 'Бэтмен против Джокера', '2008-07-18 00:00:00', 152, 3),
('Криминальное чтиво', 'Переплетающиеся истории криминального мира', '1994-10-14 00:00:00', 154, 4),
('Властелин колец: Возвращение короля', 'Финальная битва за Средиземье', '2003-12-17 00:00:00', 201, 3),
('Форрест Гамп', 'История простого человека в сложное время', '1994-07-06 00:00:00', 142, 3),
('Начало', 'Вор проникает в сны людей', '2010-07-16 00:00:00', 148, 3),
('Зелёная миля', 'История тюремного надзирателя и заключённого', '1999-12-10 00:00:00', 189, 4),
('Матрица', 'Хакер узнаёт правду о реальности', '1999-03-31 00:00:00', 136, 4),
('Интерстеллар', 'Команда исследователей путешествует через червоточину', '2014-11-07 00:00:00', 169, 3);

INSERT INTO film_multi_genre (film_id, genre_id) VALUES
(1, 2),
(2, 2),
(3, 3),
(4, 2),
(5, 4),
(6, 2),
(7, 4),
(8, 2),
(9, 4),
(9, 3),
(10, 4);

INSERT INTO users (email, login, name, birthday) VALUES
('alexey.smirnov@email.com', 'alexeys', 'Алексей Смирнов', '1990-05-15 00:00:00'),
('maria.ivanova@email.com', 'mariai', 'Мария Иванова', '1988-08-20 00:00:00'),
('ivan.petrov@email.com', 'ivanp', 'Иван Петров', '1992-11-30 00:00:00'),
('elena.sidorova@email.com', 'elenas', 'Елена Сидорова', '1995-03-10 00:00:00'),
('dmitry.kozloff@email.com', 'dmitryk', 'Дмитрий Козлов', '1985-07-25 00:00:00'),
('olga.novikova@email.com', 'olgan', 'Ольга Новикова', '1993-09-05 00:00:00'),
('sergey.morozov@email.com', 'sergeym', 'Сергей Морозов', '1991-12-12 00:00:00'),
('anna.volkova@email.com', 'annav', 'Анна Волкова', '1994-04-18 00:00:00'),
('andrey.fedorov@email.com', 'andreyf', 'Андрей Фёдоров', '1987-06-22 00:00:00'),
('tatiana.pavlova@email.com', 'tatianap', 'Татьяна Павлова', '1996-01-14 00:00:00');

INSERT INTO friends_user (user_id, friend_user_id, status_id) VALUES
(1, 2, 2),
(1, 3, 2),
(1, 4, 1),
(2, 1, 2),
(2, 5, 2),
(2, 6, 1),
(3, 1, 2),
(3, 7, 2),
(3, 8, 1),
(4, 5, 2),
(4, 6, 2),
(5, 2, 2),
(5, 4, 2),
(5, 7, 1),
(6, 7, 2),
(6, 8, 2),
(7, 3, 2),
(7, 6, 2),
(8, 1, 2),
(8, 5, 1),
(9, 1, 2),
(9, 3, 2),
(9, 10, 1),
(10, 2, 2),
(10, 9, 1);

INSERT INTO film_likes (film_id, user_id) VALUES
(1, 1),
(1, 2),
(1, 3),
(1, 4),
(1, 5),
(2, 1),
(2, 3),
(2, 5),
(2, 7),
(3, 2),
(3, 4),
(3, 6),
(3, 7),
(3, 8),
(3, 1),
(4, 2),
(4, 5),
(4, 8),
(5, 1),
(5, 3),
(5, 4),
(5, 6),
(5, 7),
(5, 8),
(5, 2),
(6, 1),
(6, 3),
(6, 5),
(6, 8),
(7, 2),
(7, 4),
(7, 6),
(7, 7),
(7, 8),
(8, 1),
(8, 3),
(8, 5),
(9, 2),
(9, 4),
(9, 6),
(9, 7),
(10, 1),
(10, 3),
(10, 5),
(10, 8);