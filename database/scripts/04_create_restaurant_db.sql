-- Créer l'utilisateur
CREATE USER mini_dish_db_manager WITH PASSWORD 'ange';

-- Créer la base de données
CREATE DATABASE mini_dish_db OWNER mini_dish_db_manager;

-- Accorder les privilèges
GRANT ALL PRIVILEGES ON DATABASE mini_dish_db TO mini_dish_db_manager;