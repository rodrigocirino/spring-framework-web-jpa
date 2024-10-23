CREATE TABLE Logins (
    id SERIAL PRIMARY KEY,
    login VARCHAR(255) NOT NULL,
    pass VARCHAR(255) NOT NULL
);

INSERT INTO Logins (login, pass) VALUES ('admin', 'admin');

