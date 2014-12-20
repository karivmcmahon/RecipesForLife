CREATE TABLE Images (updateTime datetime NOT NULL, image image NOT NULL, imageid int NOT NULL, PRIMARY KEY (imageid));
ALTER TABLE Images ADD FOREIGN KEY (imageid) REFERENCES Users (id);
