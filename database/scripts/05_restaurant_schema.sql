-- Connexion à la base de données
\c mini_dish_db;

-- Création des types ENUM
CREATE TYPE dish_type_enum AS ENUM ('START', 'MAIN', 'DESSERT');
CREATE TYPE ingredient_category_enum AS ENUM ('VEGETABLE', 'ANIMAL', 'MARINE', 'DAIRY', 'OTHER');

-- Création de la table Dish
CREATE TABLE dish (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    dish_type dish_type_enum NOT NULL
);

-- Création de la table Ingredient
CREATE TABLE ingredient (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price NUMERIC(10, 2) NOT NULL,
    category ingredient_category_enum NOT NULL,
    id_dish INTEGER,
    CONSTRAINT fk_ingredient_dish 
        FOREIGN KEY (id_dish) 
        REFERENCES dish(id) 
        ON DELETE SET NULL
);

-- Index
CREATE INDEX idx_ingredient_name ON ingredient(name);
CREATE INDEX idx_ingredient_category ON ingredient(category);
CREATE INDEX idx_ingredient_id_dish ON ingredient(id_dish);