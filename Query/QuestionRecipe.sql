CREATE TABLE QuestionRecipe (Recipeid int NOT NULL, questionId int NOT NULL, updateTime datetime NOT NULL);
ALTER TABLE QuestionRecipe ADD FOREIGN KEY (Recipeid) REFERENCES Recipe (id);
ALTER TABLE QuestionRecipe ADD FOREIGN KEY (questionId) REFERENCES Question (questionId);
