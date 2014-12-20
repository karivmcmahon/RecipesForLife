CREATE TABLE Review (reviewId int IDENTITY NOT NULL, review varchar(255) NOT NULL, userid int NOT NULL, updateTime datetime NOT NULL, PRIMARY KEY (reviewId));
ALTER TABLE Review ADD FOREIGN KEY (userid) REFERENCES Users (id);
