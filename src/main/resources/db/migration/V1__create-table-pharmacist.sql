-- All will be created using LOWERCASE!!!
-- Comando para criar a tabela Pharmacist
CREATE TABLE Pharmacist (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    license_Work VARCHAR(20) NOT NULL,
    type_Medication VARCHAR(20) NOT NULL, -- Alterado para VARCHAR, pois ENUM não é diretamente suportado no PostgreSQL
    street VARCHAR(255) NOT NULL,
    num  INT NOT NULL,
    apt VARCHAR(10) NULL,
    city VARCHAR(100) NOT NULL
    CONSTRAINT type_constraint CHECK (type_Medication IN ('SIMPLE', 'PRESCRIPTION', 'OVERTHECOUNTER')) -- Restrição de tipo
);

-- Comando para inserir os dados na tabela Pharmacist
INSERT INTO Pharmacist (name, license_Work, type_Medication, street, num, apt, city)
VALUES ('Nome do Farmacêutico', 123456, 'SIMPLE', 'Rua Exemplo', '123', '402', 'Cidade Exemplo');

