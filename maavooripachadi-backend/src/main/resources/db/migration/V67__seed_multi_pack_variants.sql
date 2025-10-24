-- Create multi-pack variants for pickles and podis so storefronts can offer multiple jar sizes.

-- Ensure 250 g jar variants exist (half of 500 g pricing).
INSERT INTO variant (product_id, sku, label, price_paise, in_stock)
SELECT
  p.id,
  CONCAT(p.slug, '-250g'),
  '250 g jar',
  CAST(ROUND(p.price_paise / 2) AS SIGNED),
  TRUE
FROM product p
WHERE p.category_slug IN ('veg-pickles', 'non-veg-pickles', 'podi')
ON DUPLICATE KEY UPDATE
  label = VALUES(label),
  price_paise = VALUES(price_paise),
  in_stock = VALUES(in_stock);

-- Refresh the standard 500 g jar pricing to align with product list price.
INSERT INTO variant (product_id, sku, label, price_paise, in_stock)
SELECT
  p.id,
  CONCAT(p.slug, '-500g'),
  '500 g jar',
  p.price_paise,
  TRUE
FROM product p
WHERE p.slug IN (
  'mango-avakai',
  'gongura-pandumirchi',
  'budida-gummadikaya',
  'kothimeera-pickle',
  'gongura-mutton',
  'natu-kodi',
  'prawns',
  'crab-pickle',
  'karivepaku-podi',
  'sambar-mix',
  'flax-seed-podi',
  'bellam-gavvalu',
  'ribbon-pakodi',
  'badam-halwa'
)
ON DUPLICATE KEY UPDATE
  label = VALUES(label),
  price_paise = VALUES(price_paise),
  in_stock = VALUES(in_stock);

-- Ensure 1 kg jar variants exist (double the 500 g pricing).
INSERT INTO variant (product_id, sku, label, price_paise, in_stock)
SELECT
  p.id,
  CONCAT(p.slug, '-1000g'),
  '1 kg jar',
  p.price_paise * 2,
  TRUE
FROM product p
WHERE p.category_slug IN ('veg-pickles', 'non-veg-pickles', 'podi')
ON DUPLICATE KEY UPDATE
  label = VALUES(label),
  price_paise = VALUES(price_paise),
  in_stock = VALUES(in_stock);
