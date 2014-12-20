CREATE TABLE ImageRecipe (imageID int NOT NULL, madeBy int NOT NULL, recipeID int NOT NULL, updateTime datetime NOT NULL);
ALTER TABLE ImageRecipe ADD FOREIGN KEY (recipeID) REFERENCES Recipe (id);
ALTER TABLE ImageRecipe ADD FOREIGN KEY (imageID) REFERENCES Images (imageid);
ALTER TABLE ImageRecipe ADD FOREIGN KEY (madeBy) REFERENCES Users (id);
