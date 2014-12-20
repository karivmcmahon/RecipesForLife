CREATE TABLE DietaryRecipe (updateTime datetime NOT NULL, recipeId int NOT NULL, dietaryId int NOT NULL);
ALTER TABLE DietaryRecipe ADD FOREIGN KEY (recipeId) REFERENCES Recipe (id);
ALTER TABLE DietaryRecipe ADD FOREIGN KEY (dietaryId) REFERENCES Dietary (dietaryId);
