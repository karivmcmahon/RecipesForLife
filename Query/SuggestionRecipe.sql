CREATE TABLE SuggestionRecipe (SuggestionID int NOT NULL, recipeID int NOT NULL, updateTime datetime NOT NULL);
ALTER TABLE SuggestionRecipe ADD  FOREIGN KEY (SuggestionID) REFERENCES Suggestion (suggestionID);
ALTER TABLE SuggestionRecipe ADD FOREIGN KEY (recipeID) REFERENCES Recipe (id);
