CREATE TABLE IF NOT EXISTS blueprints (
    author VARCHAR(100) NOT NULL,
    name   VARCHAR(100) NOT NULL,
    PRIMARY KEY (author, name)
);

CREATE TABLE IF NOT EXISTS blueprint_points (
    id          BIGSERIAL PRIMARY KEY,
    author      VARCHAR(100) NOT NULL,
    name        VARCHAR(100) NOT NULL,
    x           INTEGER NOT NULL,
    y           INTEGER NOT NULL,
    point_order INTEGER NOT NULL,
    CONSTRAINT fk_blueprint
        FOREIGN KEY (author, name)
        REFERENCES blueprints(author, name)
        ON DELETE CASCADE
);