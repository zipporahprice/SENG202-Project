DROP TABLE IF EXISTS crashes;
--SPLIT
CREATE TABLE IF NOT EXISTS crashes (
     object_id INTEGER PRIMARY KEY AUTOINCREMENT,
     speed_limit INTEGER,
     crash_year INTEGER,
     crash_location1 TEXT,
     crash_location2 TEXT,
     severity INT,
     region TEXT,
     weather TEXT,
     longitude REAL,
     latitude REAL,
     bicycle_involved BOOLEAN,
     bus_involved BOOLEAN,
     car_involved BOOLEAN,
     holiday BOOLEAN,
     moped_involved BOOLEAN,
     motorcycle_involved BOOLEAN,
     parked_vehicle_involved BOOLEAN,
     pedestrian_involved BOOLEAN,
     school_bus_involved BOOLEAN,
     train_involved BOOLEAN,
     truck_involved BOOLEAN);
--SPLIT
DROP TABLE IF EXISTS rtree_index;
--SPLIT
CREATE VIRTUAL TABLE rtree_index USING rtree(
    id,
    minX, maxX,
    minY, maxY
);
--SPLIT
DROP TABLE IF EXISTS favourites;
--SPLIT
CREATE TABLE IF NOT EXISTS favourites (
     id INTEGER PRIMARY KEY AUTOINCREMENT,
     start_address TEXT,
     end_address TEXT,
     start_lat DOUBLE,
     start_lng DOUBLE,
     end_lat DOUBLE,
     end_lng DOUBLE,
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
--SPLIT
CREATE TRIGGER insert_crash AFTER INSERT ON crashes
BEGIN
    INSERT INTO rtree_index(id, minX, maxX, minY, maxY)
    VALUES (NEW.object_id, NEW.longitude, NEW.longitude, NEW.latitude, NEW.latitude);
END;