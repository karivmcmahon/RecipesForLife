CREATE TABLE PrepRecipe (recipeId int NOT NULL, Preperationid int NOT NULL, updateTime datetime NOT NULL);
ALTER TABLE PrepRecipe ADD FOREIGN KEY (recipeId) REFERENCES Recipe (id);
ALTER TABLE PrepRecipe ADD FOREIGN KEY (Preperationid) REFERENCES Preperation (id);
