CREATE TABLE notes IF NOT EXISTS
(
  id SERIAL NOT NULL,
  created_at timestamp default current_timestamp,
  body varchar NOT NULL
);
INSERT INTO notes(body) VALUES ('Today I planted some Strawberries.');
INSERT INTO notes(body) VALUES ('Today I harvested two Strawberries.');