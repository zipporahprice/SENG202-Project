INSERT INTO rtree_index(id, minX, maxX, minY, maxY) SELECT object_id, longitude, longitude, latitude, latitude FROM crashes;
--SPLIT
CREATE TRIGGER insert_crash AFTER INSERT ON crashes BEGIN
    INSERT INTO rtree_index(id, minX, maxX, minY, maxY)
    VALUES (NEW.object_id, NEW.longitude, NEW.longitude, NEW.latitude, NEW.latitude);
END;