DROP TABLE IF EXISTS crashes;
--SPLIT
CREATE TABLE IF NOT EXISTS crashes (
     id INTEGER PRIMARY KEY AUTOINCREMENT,
     item TEXT);
--SPLIT
DROP TABLE IF EXISTS favourites;
--SPLIT
CREATE TABLE IF NOT EXISTS favourites (
     id INTEGER PRIMARY KEY AUTOINCREMENT,
     start_lat REAL,
     start_lng REAL,
     end_lat REAL,
     end_lng REAL,
     filters TEXT);
--SPLIT
DROP TABLE IF EXISTS users;
--SPLIT
CREATE TABLE IF NOT EXISTS users (
     id INTEGER PRIMARY KEY AUTOINCREMENT,
     username TEXT,
     password TEXT);
--SPLIT
INSERT INTO users (username, password) VALUES ('admin', '12345');