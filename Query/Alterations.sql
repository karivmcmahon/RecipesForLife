CREATE TABLE Alterations (id int IDENTITY NOT NULL, changeType varchar(255) NOT NULL, [table] varchar(255) NULL, [column] varchar(255) NULL, rowid int NULL, madeBy int NOT NULL, updateTime datetime NOT NULL, progress varchar(255) NOT NULL, recipeID int NOT NULL, cookbookID int NOT NULL, PRIMARY KEY (id));
ALTER TABLE Alterations ADD FOREIGN KEY (madeBy) REFERENCES Users (id);
