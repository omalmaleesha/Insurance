DROP TABLE IF EXISTS users;

CREATE TABLE users (
    etf_no VARCHAR(20) PRIMARY KEY,

    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,

    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,

    phone_number VARCHAR(20),
    nic VARCHAR(20) UNIQUE,

    gender ENUM('MALE','FEMALE','OTHER'),

    date_of_birth DATE,
    address TEXT,

    designation VARCHAR(100) NOT NULL,

    employee_type ENUM('PERMANENT','CONTRACT','INTERN')
        DEFAULT 'PERMANENT',

    specialization VARCHAR(100),

    underwriting_limit DECIMAL(15,2)
        DEFAULT 0.00,

    approval_level INT
        DEFAULT 1,

    branch_code CHAR(3) NOT NULL,

    department VARCHAR(100) NOT NULL,

    status ENUM('ACTIVE','INACTIVE','SUSPENDED')
        DEFAULT 'ACTIVE',

    joined_date DATE,

    last_login DATETIME,

    created_at TIMESTAMP
        DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP
        DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP
);