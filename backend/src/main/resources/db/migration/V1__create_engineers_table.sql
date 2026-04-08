CREATE TABLE engineers (
    id              UUID         NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    name            VARCHAR(255) NOT NULL,
    level           VARCHAR(50)  NOT NULL,
    squad           VARCHAR(50)  NOT NULL,
    hire_date       DATE,
    employment_type VARCHAR(50)  NOT NULL,
    hourly_rate     NUMERIC(10, 2),
    days_at_office  INTEGER
);
