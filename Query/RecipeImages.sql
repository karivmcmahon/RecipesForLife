CREATE TABLE RecipeImages (Recipeid int NOT NULL, imageid int NOT NULL, updateTime datetime NOT NULL);
ALTER TABLE RecipeImages ADD FOREIGN KEY (Recipeid) REFERENCES Recipe (id);
ALTER TABLE RecipeImages ADD FOREIGN KEY (imageid) REFERENCES Images (imageid);
