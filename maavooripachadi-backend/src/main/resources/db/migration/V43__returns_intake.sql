CREATE TABLE IF NOT EXISTS return_intake (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  return_id BIGINT,
  warehouse_id BIGINT,
  variant_id BIGINT,
  `condition` VARCHAR(16),
  restockable BOOLEAN DEFAULT FALSE,
  qty INT,
  processed_by VARCHAR(128),
  note VARCHAR(255),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
