DROP TABLE IF EXISTS news;
CREATE TABLE news (
    id int8 NOT NULL,
    published BOOLEAN,
    source VARCHAR(255),
    title VARCHAR(255),
    topic VARCHAR(255),
    PRIMARY KEY (id));
