CREATE TABLE Account (id int NOT NULL, email varchar(255) NOT NULL, password varchar(255) NOT NULL, updateTime datetime NOT NULL, PRIMARY KEY (id));
ALTER TABLE Account ADD FOREIGN KEY (id) REFERENCES Users (id);
