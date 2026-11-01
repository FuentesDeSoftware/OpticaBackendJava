-- =========================================================
--  DNI hondureño: 13 dígitos (antes 8)
--  NOT VALID: la regla aplica a altas y cambios, sin revalidar las filas existentes
-- =========================================================
ALTER TABLE clientes DROP CONSTRAINT IF EXISTS clientes_dni_check;
ALTER TABLE clientes ALTER COLUMN dni TYPE VARCHAR(13);
ALTER TABLE clientes ADD CONSTRAINT clientes_dni_check CHECK (dni ~ '^[0-9]{13}$') NOT VALID;
