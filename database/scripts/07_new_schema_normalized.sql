-- Connexion à la base de données
\c mini_dish_db;

-- Supprimer les anciennes tables (attention: perte de données!)
DROP TABLE IF EXISTS dish_ingredient CASCADE;
DROP TABLE IF EXISTS ingredient CASCADE;
DROP TABLE IF EXISTS dish CASCADE;

-- Recréer les types ENUM
CREATE TYPE dish_type_enum AS ENUM ('START', 'MAIN', 'DESSERT');
CREATE TYPE ingredient_category_enum AS ENUM ('VEGETABLE', 'ANIMAL', 'MARINE', 'DAIRY', 'OTHER');
CREATE TYPE unit_type AS ENUM ('KG', 'L', 'PCS');

-- Création de la table Dish (avec prix de vente)
CREATE TABLE dish (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    dish_type dish_type_enum NOT NULL,
    selling_price NUMERIC(10, 2)
);

-- Création de la table Ingredient (sans référence à dish)
CREATE TABLE ingredient (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    price NUMERIC(10, 2) NOT NULL,
    category ingredient_category_enum NOT NULL
);

-- Création de la table de jointure DishIngredient
CREATE TABLE dish_ingredient (
    id SERIAL PRIMARY KEY,
    id_dish INTEGER NOT NULL,
    id_ingredient INTEGER NOT NULL,
    quantity_required NUMERIC(10, 3) NOT NULL,
    unit unit_type NOT NULL,
    CONSTRAINT fk_dish_ingredient_dish 
        FOREIGN KEY (id_dish) 
        REFERENCES dish(id) 
        ON DELETE CASCADE,
    CONSTRAINT fk_dish_ingredient_ingredient 
        FOREIGN KEY (id_ingredient) 
        REFERENCES ingredient(id) 
        ON DELETE CASCADE,
    CONSTRAINT unique_dish_ingredient UNIQUE (id_dish, id_ingredient)
);

-- Index
CREATE INDEX idx_dish_ingredient_dish ON dish_ingredient(id_dish);
CREATE INDEX idx_dish_ingredient_ingredient ON dish_ingredient(id_ingredient);
CREATE INDEX idx_ingredient_name ON ingredient(name);