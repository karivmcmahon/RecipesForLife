CREATE TABLE IngredToIngredDetails ([Ingredient detailsid] int NOT NULL, ingredientid int NOT NULL, updateTime datetime NOT NULL);
ALTER TABLE IngredToIngredDetails ADD FOREIGN KEY ([Ingredient detailsid]) REFERENCES [Ingredient details] (id);
ALTER TABLE IngredToIngredDetails ADD FOREIGN KEY (ingredientid) REFERENCES Ingredient (id);
