CREATE TABLE AltererationRecipes (Recipeid int NOT NULL, altererationID int NOT NULL, updateTime datetime NOT NULL);
ALTER TABLE AltererationRecipes ADD FOREIGN KEY (Recipeid) REFERENCES Recipe (id);
ALTER TABLE AltererationRecipes ADD FOREIGN KEY (altererationID) REFERENCES Alterations (id);
