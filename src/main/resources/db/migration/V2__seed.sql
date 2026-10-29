-- =========================================================
--  Datos de demostración (contraseña de ambos usuarios: 123456)
-- =========================================================
CREATE EXTENSION IF NOT EXISTS pgcrypto;

INSERT INTO usuarios (email, password_hash, rol) VALUES
    ('vendedor@optica.com', crypt('123456', gen_salt('bf', 10)), 'VENDEDOR'),
    ('cliente@optica.com',  crypt('123456', gen_salt('bf', 10)), 'CLIENTE');

INSERT INTO vendedores (usuario_id, nombres, apellidos)
SELECT id, 'Carlos', 'Ramírez' FROM usuarios WHERE email = 'vendedor@optica.com';

INSERT INTO clientes (usuario_id, nombres, apellidos, dni, telefono)
SELECT id, 'María', 'Torres', '71234567', '987654321' FROM usuarios WHERE email = 'cliente@optica.com';

INSERT INTO mediciones (cliente_id, vendedor_id, fecha, od_esfera, od_cilindro, od_eje, od_adicion,
                        oi_esfera, oi_cilindro, oi_eje, oi_adicion, dip, observaciones)
VALUES
    ((SELECT id FROM clientes WHERE dni = '71234567'), (SELECT MIN(id) FROM vendedores), DATE '2025-03-12',
     -1.25, -0.50, 180, 0.00, -1.00, -0.25, 170, 0.00, 62.0, 'Primera evaluación. Miopía leve.'),
    ((SELECT id FROM clientes WHERE dni = '71234567'), (SELECT MIN(id) FROM vendedores), DATE '2026-04-08',
     -1.75, -0.75, 175, 0.00, -1.50, -0.50, 165, 0.00, 62.0, 'Control anual. Aumento de graduación.');

INSERT INTO aros (codigo, marca, modelo, material, forma, color, precio, stock, imagen_url) VALUES
    ('RB-3025',  'Ray-Ban',  'Aviator Classic', 'Metal',     'Aviador',     'Dorado',     520.00, 12, NULL),
    ('RB-5154',  'Ray-Ban',  'Clubmaster',      'Acetato',   'Browline',    'Negro',      480.00,  3, NULL),
    ('RB-7047',  'Ray-Ban',  'RX7047',          'Acetato',   'Rectangular', 'Carey',      390.00,  8, NULL),
    ('OK-8156',  'Oakley',   'Pitchman R',      'O Matter',  'Redondo',     'Gris',       610.00,  4, NULL),
    ('OK-3233',  'Oakley',   'Socket 5.0',      'Titanio',   'Rectangular', 'Negro mate', 720.00,  6, NULL),
    ('VG-5250',  'Vogue',    'VO5250',          'Acetato',   'Cat Eye',     'Rosado',     310.00, 10, NULL),
    ('VG-4134',  'Vogue',    'VO4134',          'Metal',     'Redondo',     'Plateado',   280.00,  2, NULL),
    ('PR-16MV',  'Prada',    'PR 16MV',         'Acetato',   'Cuadrado',    'Negro',      890.00,  5, NULL),
    ('GU-2847',  'Guess',    'GU2847',          'Metal',     'Ovalado',     'Bronce',     260.00, 15, NULL),
    ('TH-1689',  'Tommy Hilfiger', 'TH 1689',   'TR90',      'Rectangular', 'Azul',       340.00,  1, NULL),
    ('SL-M110',  'Silhouette', 'Momentum',      'Titanio',   'Al aire',     'Plateado',   950.00,  7, NULL),
    ('GN-0001',  'Genérico', 'Basic Kids',      'TR90',      'Redondo',     'Rojo',        90.00, 20, NULL);

INSERT INTO lunas (codigo, tipo, material, indice, tratamiento, precio, stock) VALUES
    ('LN-MON-156', 'MONOFOCAL',  'Resina',        1.56, 'Antirreflejo',                 120.00, 40),
    ('LN-MON-160', 'MONOFOCAL',  'Policarbonato', 1.59, 'Antirreflejo + Filtro azul',   180.00, 25),
    ('LN-MON-167', 'MONOFOCAL',  'Alto índice',   1.67, 'Antirreflejo + Fotocromático', 320.00,  4),
    ('LN-BIF-156', 'BIFOCAL',    'Resina',        1.56, 'Antirreflejo',                 210.00, 12),
    ('LN-PRO-160', 'PROGRESIVO', 'Policarbonato', 1.59, 'Antirreflejo + Filtro azul',   450.00, 10);

INSERT INTO accesorios (codigo, nombre, descripcion, precio, stock) VALUES
    ('AC-EST-01', 'Estuche rígido',     'Estuche rígido con cierre magnético', 35.00, 30),
    ('AC-PAN-01', 'Paño de microfibra', 'Paño de limpieza 15x15 cm',            8.00, 100),
    ('AC-LIQ-01', 'Líquido limpiador',  'Spray limpiador antiempañante 60 ml',  18.00,  3),
    ('AC-COR-01', 'Cordón sujetador',   'Cordón de silicona ajustable',         15.00, 25);
