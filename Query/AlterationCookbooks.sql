CREATE TABLE AlterationCookbooks (altererationID int NOT NULL, cookbookID int NOT NULL, updateTime datetime NOT NULL);
ALTER TABLE AlterationCookbooks ADD FOREIGN KEY (altererationID) REFERENCES Alterations (id);
ALTER TABLE AlterationCookbooks ADD FOREIGN KEY (cookbookID) REFERENCES Cookbook (id);
