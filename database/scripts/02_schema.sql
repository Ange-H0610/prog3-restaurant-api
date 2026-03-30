-- Création de la table Product
CREATE TABLE product (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price NUMERIC(10, 2) NOT NULL,
    creation_datetime TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Création de la table Product_category
CREATE TABLE product_category (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    product_id INTEGER NOT NULL,
    CONSTRAINT fk_product_category_product 
        FOREIGN KEY (product_id) 
        REFERENCES product(id) 
        ON DELETE CASCADE
);

-- Index pour améliorer les performances
CREATE INDEX idx_product_creation_datetime ON product(creation_datetime);
CREATE INDEX idx_product_category_name ON product_category(name);
CREATE INDEX idx_product_category_product_id ON product_category(product_id);