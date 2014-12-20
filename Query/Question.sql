CREATE TABLE Question (questionId int IDENTITY NOT NULL, question varchar(255) NOT NULL, userid int NOT NULL, updateTime datetime NOT NULL, PRIMARY KEY (questionId));
ALTER TABLE Question ADD FOREIGN KEY (userid) REFERENCES Users (id);
