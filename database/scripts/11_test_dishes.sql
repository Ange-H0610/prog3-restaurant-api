INSERT INTO dish (name, dish_type, selling_price) VALUES
('Pizza Margherita', 'MAIN', 12000.00),
('Pizza Quattro Stagioni', 'MAIN', 15000.00),
('Tiramisu', 'DESSERT', 8000.00),
('Panna Cotta', 'DESSERT', 7000.00),
('Salade César', 'START', 6500.00)
ON CONFLICT (name) DO NOTHING;