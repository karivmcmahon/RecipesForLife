CREATE TABLE Contributers (Cookbookid int NOT NULL, usersId int NOT NULL, updateTime datetime NOT NULL, progress varchar(255) NOT NULL);
ALTER TABLE Contributers ADD FOREIGN KEY (Cookbookid) REFERENCES Cookbook (id);
ALTER TABLE Contributers ADD FOREIGN KEY (usersId) REFERENCES Users (id);
