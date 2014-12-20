CREATE TABLE RecipeIngredient (Recipeid int NOT NULL, ingredientDetailsId int NOT NULL, updateTime datetime NULL);
ALTER TABLE RecipeIngredient ADD FOREIGN KEY (ingredientDetailsId) REFERENCES [Ingredient details] (id);
ALTER TABLE RecipeIngredient ADD FOREIGN KEY (Recipeid) REFERENCES Recipe (id);
