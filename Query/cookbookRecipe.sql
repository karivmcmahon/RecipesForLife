CREATE TABLE CookbookRecipe (Recipeid int NOT NULL, Cookbookid int NOT NULL, updateTime datetime NULL);
ALTER TABLE CookbookRecipe ADD  FOREIGN KEY (Recipeid) REFERENCES Recipe (id);
ALTER TABLE CookbookRecipe ADD  FOREIGN KEY (Cookbookid) REFERENCES Cookbook (id);
