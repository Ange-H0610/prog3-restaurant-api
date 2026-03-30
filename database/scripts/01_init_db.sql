-- Créer l'utilisateur
CREATE USER product_manager_user WITH PASSWORD 'ange';

-- Créer la base de données
CREATE DATABASE product_management_db OWNER product_manager_user;

-- Accorder les privilèges
GRANT ALL PRIVILEGES ON DATABASE product_management_db TO product_manager_user;