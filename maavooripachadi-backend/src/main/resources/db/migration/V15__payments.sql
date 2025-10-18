CREATE TABLE payment_attempt(
id BIGINT PRIMARY KEY AUTO_INCREMENT, order_no VARCHAR(64),
gateway VARCHAR(32), gateway_order_id VARCHAR(128), gateway_payment_id VARCHAR(128),
amount INT, status VARCHAR(32));