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
    term INT NOT NULL,
    email VARCHAR(150) NOT NULL,
    state_id UUID NOT NULL,
    loan_type_id UUID NOT NULL,
    CONSTRAINT fk_application_state FOREIGN KEY (state_id) REFERENCES state(state_id),
    CONSTRAINT fk_application_loantype FOREIGN KEY (loan_type_id) REFERENCES loan_type(loan_type_id)
);

INSERT INTO state (name, description)
VALUES ('PENDIENTE', 'Estado inicial de la solicitud de préstamo');

-- Insert en loan_type de ejemplo
INSERT INTO loan_type (name, minimum_amount, maximum_amount, interest_rate, automatic_validation)
VALUES ('Préstamo Personal', 1000.00, 50000.00, 12.50, FALSE);
