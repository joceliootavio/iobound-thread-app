-- Criação do banco de dados
--CREATE DATABASE performance_test;

-- Conecte-se ao banco de dados criado
\c performance_test;

CREATE TABLE customer (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    phone VARCHAR(20),
    birth_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) CHECK (status IN ('ACTIVE', 'INACTIVE')),
    address TEXT,
    city VARCHAR(50)
);

INSERT INTO public.customer
(id, "name", email, phone, birth_date, created_at, updated_at, status, address, city)
VALUES('e07d7927-6659-4668-9b95-54f7ef91334b'::uuid, 'Customer 1', 'jocelio.otavio@gmail.com', '+55 9798245544', '2010-08-10', '2025-03-16 22:18:41.516', '2025-03-16 22:18:41.516', 'INACTIVE', 'Rua 1, Bairro XYZ', 'Fortaleza');

INSERT INTO customer (id, name, email, phone, birth_date, created_at, updated_at, status, address, city)
SELECT
    gen_random_uuid(),
    'Customer ' || i,
    'customer' || i || '@example.com',
    CASE WHEN random() > 0.2 THEN '+55 9' || floor(random() * 900000000 + 100000000)::TEXT ELSE NULL END,
    NOW() - INTERVAL '20 years' * random(),
    NOW(),
    NOW(),
    CASE WHEN random() > 0.5 THEN 'ACTIVE' ELSE 'INACTIVE' END,
    'Rua ' || i || ', Bairro XYZ',
    (ARRAY['São Paulo', 'Rio de Janeiro', 'Belo Horizonte', 'Salvador', 'Fortaleza'])[floor(random() * 5 + 1)]
FROM generate_series(1, 10000) AS i;
