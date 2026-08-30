ALTER TABLE receipt 
    ADD COLUMN merchant_name VARCHAR(255),
    ADD COLUMN receipt_number VARCHAR(255),
    ADD COLUMN purchase_date DATE,
    ADD COLUMN total_amount DECIMAL(12, 2);