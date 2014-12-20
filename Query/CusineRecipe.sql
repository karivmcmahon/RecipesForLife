CREATE TABLE CusineRecipe (Recipeid int NOT NULL, cusineId int NOT NULL, updateTime datetime NOT NULL);
ALTER TABLE CusineRecipe ADD FOREIGN KEY (Recipeid) REFERENCES Recipe (id);
ALTER TABLE CusineRecipe ADD FOREIGN KEY (cusineId) REFERENCES Cuisine (cuisineId);
