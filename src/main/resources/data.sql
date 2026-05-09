
INSERT INTO genre(name)
SELECT'Комедия'
WHERE NOT EXISTS (
    SELECT name FROM genre WHERE name = 'Комедия'
);

INSERT INTO genre(name)
SELECT'Драма'
WHERE NOT EXISTS (
    SELECT name FROM genre WHERE name = 'Драма'
);

INSERT INTO genre(name)
SELECT'Мультфильм'
WHERE NOT EXISTS (
    SELECT name FROM genre WHERE name = 'Мультфильм'
);

INSERT INTO genre(name)
SELECT'Триллер'
WHERE NOT EXISTS (
    SELECT name FROM genre WHERE name = 'Триллер'
);

INSERT INTO genre(name)
SELECT'Документальный'
WHERE NOT EXISTS (
    SELECT name FROM genre WHERE name = 'Документальный'
);

INSERT INTO genre(name)
SELECT'Боевик'
WHERE NOT EXISTS (
    SELECT name FROM genre WHERE name = 'Боевик'
);

INSERT INTO rating(name)
SELECT'G'
WHERE NOT EXISTS (
    SELECT name FROM rating WHERE name = 'G'
);

INSERT INTO rating(name)
SELECT'PG'
WHERE NOT EXISTS (
    SELECT name FROM rating WHERE name = 'PG'
);

INSERT INTO rating(name)
SELECT'PG-13'
WHERE NOT EXISTS (
    SELECT name FROM rating WHERE name = 'PG-13'
);

INSERT INTO rating(name)
SELECT'R'
WHERE NOT EXISTS (
    SELECT name FROM rating WHERE name = 'R'
);

INSERT INTO rating(name)
SELECT'NC-17'
WHERE NOT EXISTS (
    SELECT name FROM rating WHERE name = 'NG-17'
);

INSERT INTO users(name, login, email, birthday)
SELECT'Hota', 'Samka', 'Eror404@ya.ru', '2009-11-03'
WHERE NOT EXISTS (
    SELECT name, login, email, birthday
    FROM users
    WHERE name = 'Hota' and login = 'Samka' and email = 'Eror404@ya.ru' and birthday = '2009-11-03'
);

INSERT INTO users(name, login, email, birthday)
SELECT'Nano', 'Rozetka', 'cocojambo@ya.ru', '2001-10-21'
WHERE NOT EXISTS (
    SELECT name, login, email, birthday
    FROM users
    WHERE name = 'Nano' and login = 'Rozetka' and email = 'cocojambo@ya.ru' and birthday = '2001-10-21'
);

INSERT INTO users(name, login, email, birthday)
SELECT'Roza', 'Moni', 'ninro@ya.ru', '2007-10-21'
WHERE NOT EXISTS (
    SELECT name, login, email, birthday
    FROM users
    WHERE name = 'Roza' and login = 'Moni' and email = 'ninro@ya.ru' and birthday = '2007-10-21'
);

INSERT INTO films(name, description, release_date, duration, rating_id)
SELECT'Holly', 'good films', '2007-10-21','120', '2'
WHERE NOT EXISTS (
    SELECT name, description, release_date, duration, rating_id
    FROM films
    WHERE name = 'Holly' and
          description = 'good films' and
          release_date = '2007-10-21' and
          duration = '120' and
          rating_id = '2'
);

INSERT INTO films(name, description, release_date, duration,  rating_id)
SELECT'Vilar', 'standard film', '2002-10-21','120', '2'
WHERE NOT EXISTS (
    SELECT name, description, release_date, duration, rating_id
    FROM films
    WHERE name = 'Vilar' and
        description = 'standard film' and
        release_date = '2002-10-21' and
        duration = '120' and
        rating_id = '2'
);
