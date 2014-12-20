CREATE TABLE Suggestion (suggestionID int IDENTITY NOT NULL, suggestion varchar(255) NOT NULL, userID int NOT NULL, updateTime datetime NOT NULL, PRIMARY KEY (suggestionID));
ALTER TABLE Suggestion ADD FOREIGN KEY (userID) REFERENCES Users (id);
