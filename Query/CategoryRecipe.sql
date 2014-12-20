CREATE TABLE CatgoryRecipe (Recipeid int NOT NULL, categoryId int NOT NULL, updateTime datetime NOT NULL);
ALTER TABLE CatgoryRecipe ADD FOREIGN KEY (Recipeid) REFERENCES Recipe (id);
ALTER TABLE CatgoryRecipe ADD FOREIGN KEY (categoryId) REFERENCES Categories (categoryId);
