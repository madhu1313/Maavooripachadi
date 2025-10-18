
ALTER TABLE orders ADD COLUMN ship_address_json JSON NULL;
ALTER TABLE orders ADD COLUMN address_validation_status VARCHAR(16) DEFAULT 'UNKNOWN';
ALTER TABLE orders ADD COLUMN address_validation_score INT DEFAULT 0;