TRUNCATE blueprint_points;
DELETE FROM blueprints;

INSERT INTO blueprints (author, name) VALUES
    ('john', 'house'),
    ('john', 'garage'),
    ('jane', 'garden')
ON CONFLICT DO NOTHING;

INSERT INTO blueprint_points (author, name, x, y, point_order) VALUES
    ('john', 'house',  0,  0, 0),
    ('john', 'house', 10,  0, 1),
    ('john', 'house', 10, 10, 2),
    ('john', 'house',  0, 10, 3),
    ('john', 'garage',  5,  5, 0),
    ('john', 'garage', 15,  5, 1),
    ('john', 'garage', 15, 15, 2),
    ('jane', 'garden', 2, 2, 0),
    ('jane', 'garden', 3, 4, 1),
    ('jane', 'garden', 6, 7, 2)
ON CONFLICT DO NOTHING;