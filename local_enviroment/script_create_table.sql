-- Habilitamos extensión para UUID aleatorio
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Tabla: state
CREATE TABLE state (
    state_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    description TEXT
);

-- Tabla: loan_type
CREATE TABLE loan_type (
    loan_type_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    minimum_amount NUMERIC(15,2) NOT NULL,
    maximum_amount NUMERIC(15,2) NOT NULL,
    interest_rate NUMERIC(5,2) NOT NULL,
    automatic_validation BOOLEAN NOT NULL DEFAULT FALSE
);

-- Tabla: application
CREATE TABLE application (
    application_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    amount NUMERIC(15,2) NOT NULL,
    base_salary NUMERIC(15,2) NOT NULL,
    term INT NOT NULL,
    email VARCHAR(150) NOT NULL,
    state_id UUID NOT NULL,
    loan_type_id UUID NOT NULL,
    CONSTRAINT fk_application_state FOREIGN KEY (state_id) REFERENCES state(state_id),
    CONSTRAINT fk_application_loantype FOREIGN KEY (loan_type_id) REFERENCES loan_type(loan_type_id)
);

CREATE OR REPLACE VIEW v_pending_decisions AS
SELECT a.application_id,
       a.amount,
       a.term,
       a.email,
       lt.name            AS loan_type_name,
       lt.interest_rate,
       s.name             AS state_name,
       a.base_salary               AS base_salary,
       COALESCE(SUM(CASE WHEN s2.name = 'APROBADO' THEN a2.amount ELSE 0 END), 0) AS monthly_amount
FROM application a
JOIN loan_type lt ON a.loan_type_id = lt.loan_type_id
JOIN state s      ON a.state_id     = s.state_id
LEFT JOIN application a2 ON a2.email = a.email
LEFT JOIN state s2       ON a2.state_id = s2.state_id
--where s.name = 'PENDIENTE'
GROUP BY
	a.application_id,
	a.amount,  -- ⚠️ falta aquí
    a.term,    -- ⚠️ falta aquí
    a.email,
    lt.name, lt.interest_rate,
    s.name, a.base_salary;

INSERT INTO public.loan_type (loan_type_id,"name",minimum_amount,maximum_amount,interest_rate,automatic_validation) VALUES
	 ('3ce5706d-d01e-44af-b0b4-73e5fec45efe'::uuid,'Préstamo Personal',1000.00,50000.00,12.50,true),
	 ('663bebe7-5013-4f55-b02c-4fbd08d8843e'::uuid,'PrestamoPersonal2',1000.00,50000.00,12.50,false),
	 ('72ea4243-5c70-4aae-a645-7203e3a3e423'::uuid,'Préstamo Personal',1000.00,20000.00,12.50,true),
	 ('cc92676e-4017-4f48-a105-5203db1afea5'::uuid,'Préstamo Hipotecario',20000.00,500000.00,8.25,false),
	 ('fa4bd2c3-3876-4a5b-94c0-3f2cb824d3da'::uuid,'Préstamo Vehicular',5000.00,80000.00,10.00,true),
	 ('09ad050d-690b-4018-a293-ae7de564ca7a'::uuid,'Préstamo Educativo',2000.00,60000.00,9.75,true),
	 ('11f49034-046a-4469-84cd-f88a196e8fea'::uuid,'Préstamo Empresarial',10000.00,1000000.00,14.00,false);


INSERT INTO application (application_id, amount, base_salary, term, email, state_id, loan_type_id) VALUES
-- Préstamo Personal
(gen_random_uuid(), 15000.00, 2500.00, 24, 'juan.perez@example.com', '6a9259ef-807c-46b7-935c-950f3f5b18ae', '3ce5706d-d01e-44af-b0b4-73e5fec45efe'),
(gen_random_uuid(), 22000.00, 3100.00, 36, 'maria.gomez@example.com', '032d8ef8-9536-4aeb-ab8f-abe139a05951', '3ce5706d-d01e-44af-b0b4-73e5fec45efe'),
(gen_random_uuid(), 18000.00, 2800.00, 12, 'carlos.ramos@example.com', '6a9259ef-807c-46b7-935c-950f3f5b18ae', '72ea4243-5c70-4aae-a645-7203e3a3e423'),
(gen_random_uuid(), 12000.00, 2200.00, 18, 'laura.mendoza@example.com', '032d8ef8-9536-4aeb-ab8f-abe139a05951', '72ea4243-5c70-4aae-a645-7203e3a3e423'),

-- PrestamoPersonal2
(gen_random_uuid(), 14000.00, 2100.00, 24, 'roberto.fernandez@example.com', '6a9259ef-807c-46b7-935c-950f3f5b18ae', '663bebe7-5013-4f55-b02c-4fbd08d8843e'),
(gen_random_uuid(), 25000.00, 3500.00, 48, 'carolina.diaz@example.com', '032d8ef8-9536-4aeb-ab8f-abe139a05951', '663bebe7-5013-4f55-b02c-4fbd08d8843e'),

-- Préstamo Hipotecario
(gen_random_uuid(), 150000.00, 8000.00, 120, 'alberto.silva@example.com', '6a9259ef-807c-46b7-935c-950f3f5b18ae', 'cc92676e-4017-4f48-a105-5203db1afea5'),
(gen_random_uuid(), 350000.00, 12000.00, 240, 'monica.castillo@example.com', '032d8ef8-9536-4aeb-ab8f-abe139a05951', 'cc92676e-4017-4f48-a105-5203db1afea5'),
(gen_random_uuid(), 250000.00, 9500.00, 180, 'francisco.lopez@example.com', '6a9259ef-807c-46b7-935c-950f3f5b18ae', 'cc92676e-4017-4f48-a105-5203db1afea5'),

-- Préstamo Vehicular
(gen_random_uuid(), 35000.00, 4200.00, 48, 'patricia.reyes@example.com', '032d8ef8-9536-4aeb-ab8f-abe139a05951', 'fa4bd2c3-3876-4a5b-94c0-3f2cb824d3da'),
(gen_random_uuid(), 50000.00, 5200.00, 60, 'andres.castro@example.com', '6a9259ef-807c-46b7-935c-950f3f5b18ae', 'fa4bd2c3-3876-4a5b-94c0-3f2cb824d3da'),
(gen_random_uuid(), 28000.00, 3800.00, 36, 'valeria.ortiz@example.com', '032d8ef8-9536-4aeb-ab8f-abe139a05951', 'fa4bd2c3-3876-4a5b-94c0-3f2cb824d3da'),

-- Préstamo Educativo
(gen_random_uuid(), 15000.00, 1800.00, 36, 'sofia.torres@example.com', '6a9259ef-807c-46b7-935c-950f3f5b18ae', '09ad050d-690b-4018-a293-ae7de564ca7a'),
(gen_random_uuid(), 25000.00, 2200.00, 48, 'ricardo.martinez@example.com', '032d8ef8-9536-4aeb-ab8f-abe139a05951', '09ad050d-690b-4018-a293-ae7de564ca7a'),
(gen_random_uuid(), 18000.00, 2000.00, 24, 'camila.rojas@example.com', '6a9259ef-807c-46b7-935c-950f3f5b18ae', '09ad050d-690b-4018-a293-ae7de564ca7a'),

-- Préstamo Empresarial
(gen_random_uuid(), 50000.00, 7500.00, 24, 'gustavo.arias@example.com', '032d8ef8-9536-4aeb-ab8f-abe139a05951', '11f49034-046a-4469-84cd-f88a196e8fea'),
(gen_random_uuid(), 120000.00, 15000.00, 60, 'isabel.moreno@example.com', '6a9259ef-807c-46b7-935c-950f3f5b18ae', '11f49034-046a-4469-84cd-f88a196e8fea'),
(gen_random_uuid(), 250000.00, 20000.00, 120, 'felipe.santos@example.com', '032d8ef8-9536-4aeb-ab8f-abe139a05951', '11f49034-046a-4469-84cd-f88a196e8fea'),

-- Más préstamos variados
(gen_random_uuid(), 8000.00, 1800.00, 12, 'mateo.vargas@example.com', '6a9259ef-807c-46b7-935c-950f3f5b18ae', '3ce5706d-d01e-44af-b0b4-73e5fec45efe'),
(gen_random_uuid(), 16000.00, 2500.00, 24, 'ximena.luna@example.com', '032d8ef8-9536-4aeb-ab8f-abe139a05951', '72ea4243-5c70-4aae-a645-7203e3a3e423'),
(gen_random_uuid(), 45000.00, 4200.00, 48, 'sebastian.garcia@example.com', '032d8ef8-9536-4aeb-ab8f-abe139a05951', 'fa4bd2c3-3876-4a5b-94c0-3f2cb824d3da'),
(gen_random_uuid(), 300000.00, 12000.00, 240, 'fernanda.carrillo@example.com', '6a9259ef-807c-46b7-935c-950f3f5b18ae', 'cc92676e-4017-4f48-a105-5203db1afea5'),
(gen_random_uuid(), 10000.00, 2100.00, 18, 'oscar.benitez@example.com', '032d8ef8-9536-4aeb-ab8f-abe139a05951', '663bebe7-5013-4f55-b02c-4fbd08d8843e'),
(gen_random_uuid(), 22000.00, 3100.00, 36, 'alejandra.morales@example.com', '6a9259ef-807c-46b7-935c-950f3f5b18ae', '72ea4243-5c70-4aae-a645-7203e3a3e423'),
(gen_random_uuid(), 9000.00, 1700.00, 12, 'daniel.vera@example.com', '032d8ef8-9536-4aeb-ab8f-abe139a05951', '3ce5706d-d01e-44af-b0b4-73e5fec45efe'),
(gen_random_uuid(), 70000.00, 6000.00, 72, 'claudia.rivera@example.com', '032d8ef8-9536-4aeb-ab8f-abe139a05951', 'fa4bd2c3-3876-4a5b-94c0-3f2cb824d3da'),
(gen_random_uuid(), 450000.00, 18000.00, 180, 'marco.salas@example.com', '6a9259ef-807c-46b7-935c-950f3f5b18ae', 'cc92676e-4017-4f48-a105-5203db1afea5'),
(gen_random_uuid(), 15000.00, 2500.00, 24, 'angela.paredes@example.com', '032d8ef8-9536-4aeb-ab8f-abe139a05951', '09ad050d-690b-4018-a293-ae7de564ca7a');

INSERT INTO public.state (state_id,"name",description) VALUES
	 ('6a9259ef-807c-46b7-935c-950f3f5b18ae'::uuid,'PENDIENTE','Estado inicial de la solicitud de préstamo'),
	 ('032d8ef8-9536-4aeb-ab8f-abe139a05951'::uuid,'APROBADO','Solicitud Aprovada'),
	 ('fa4126de-6f35-46ec-9fcd-b5aaae99fdc8'::uuid,'RECHAZADO','Solicitud Rechazada');

-- Insert en loan_type de ejemplo
INSERT INTO loan_type (name, minimum_amount, maximum_amount, interest_rate, automatic_validation)
VALUES ('Préstamo Personal', 1000.00, 50000.00, 12.50, FALSE);
