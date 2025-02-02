-- Criação do banco de dados
--CREATE DATABASE performance_test;

-- Conecte-se ao banco de dados criado
\c performance_test;

-- Criação da tabela com 5 colunas
--DROP TABLE public.test_table;
CREATE TABLE public.test_table (
    id SERIAL PRIMARY KEY,
    column1 VARCHAR(255) NOT NULL,
    column2 TEXT,
    column3 INTEGER,
    column4 TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    column5 BOOLEAN DEFAULT FALSE
);
--
---- Inserção de dados fake para teste de performance
INSERT INTO public.test_table (column1, column2, column3, column5)
SELECT
    md5(random()::text), -- Gera strings aleatórias
    md5(random()::text),
    trunc(random() * 1000)::int, -- Gera números inteiros aleatórios
    random() > 0.5 -- Gera valores booleanos aleatórios
FROM generate_series(1, 10000); -- Insere 10.000 registros