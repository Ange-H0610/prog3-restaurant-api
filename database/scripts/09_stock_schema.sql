-- Ajout de la gestion des stocks

\c mini_dish_db;

-- Création du type movement_type
CREATE TYPE movement_type_enum AS ENUM ('IN', 'OUT');

-- Création de la table StockMovement
CREATE TABLE IF NOT EXISTS stock_movement (
    id SERIAL PRIMARY KEY,
    id_ingredient INTEGER NOT NULL,
    quantity NUMERIC(10, 3) NOT NULL,
    type movement_type_enum NOT NULL,
    unit unit_type NOT NULL,
    creation_datetime TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_stock_movement_ingredient 
        FOREIGN KEY (id_ingredient) 
        REFERENCES ingredient(id) 
        ON DELETE CASCADE
);

-- Index
CREATE INDEX IF NOT EXISTS idx_stock_movement_ingredient ON stock_movement(id_ingredient);
CREATE INDEX IF NOT EXISTS idx_stock_movement_datetime ON stock_movement(creation_datetime);