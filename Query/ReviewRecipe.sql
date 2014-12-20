CREATE TABLE ReviewRecipe (updateTime datetime NOT NULL, ReviewId int NOT NULL, Recipeid int NOT NULL);
ALTER TABLE ReviewRecipe ADD FOREIGN KEY (Recipeid) REFERENCES Recipe (id);
ALTER TABLE ReviewRecipe ADD FOREIGN KEY (ReviewId) REFERENCES Review (reviewId);
