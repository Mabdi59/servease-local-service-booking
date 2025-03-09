BEGIN TRANSACTION;

INSERT INTO users (email, password_hash, role) VALUES ('user@servease.com','$2a$08$UkVvwpULis18S19S5pZFn.YHPZt3oaqHZnDwqbCW9pft6uFtkXKDC','CUSTOMER');
INSERT INTO users (email, password_hash, role) VALUES ('admin@servease.com','$2a$08$UkVvwpULis18S19S5pZFn.YHPZt3oaqHZnDwqbCW9pft6uFtkXKDC','ADMIN');

COMMIT TRANSACTION;

