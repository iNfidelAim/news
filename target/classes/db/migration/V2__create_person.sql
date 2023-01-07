DROP TABLE IF EXISTS person;
CREATE TABLE person (
    id  SERIAL NOT NULL,
    password VARCHAR(255),
    role VARCHAR(255),
    username VARCHAR(255),
    PRIMARY KEY (id));