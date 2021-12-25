-- name: users-by-country
-- Counts the users in a given country.
SELECT *
FROM users
WHERE country_code = :country_code

-- name: user-count
-- Counts all the users.
SELECT count(*) AS count
FROM users