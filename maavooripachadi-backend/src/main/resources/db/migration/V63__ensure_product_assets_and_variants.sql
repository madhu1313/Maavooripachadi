-- Update product hero images to use local frontend assets and seed additional catalog records.

-- Veg pickles
UPDATE product SET hero_image_url = '/assets/images/avakaya_mango_670x.jpg' WHERE slug = 'mango-avakai';
UPDATE product SET hero_image_url = '/assets/images/gongura_pandumirchi_670x.jpg' WHERE slug = 'gongura-pandumirchi';
UPDATE product SET hero_image_url = '/assets/images/ash_gourd_670x.jpg' WHERE slug = 'budida-gummadikaya';
UPDATE product SET hero_image_url = '/assets/images/kothimeera_670x.jpg' WHERE slug = 'kothimeera-pickle';

-- Non-veg pickles
INSERT INTO product (slug, title, description_html, hero_image_url, price_paise, mrp_paise, category_slug, tags, search_text, in_stock, badge)
VALUES
  (
    'natu-kodi',
    'Natu Kodi Pickle',
    '<p>Farm-raised country chicken pickle simmered in sesame oil with pepper, jeera and cloves.</p>',
    '/assets/images/chicken.jpg',
    52900,
    56900,
    'non-veg-pickles',
    'pickle,non-veg,chicken,natu-kodi,spicy',
    'Natu kodi country chicken pickle bold spices slow simmered sesame oil Andhra style',
    1,
    NULL
  ),
  (
    'prawns',
    'Royal Prawns Pickle',
    '<p>Succulent prawns coated in chilli-garlic masala and sun-cured for a coastal umami kick.</p>',
    '/assets/images/prawns.jpg',
    57900,
    61900,
    'non-veg-pickles',
    'pickle,non-veg,seafood,prawns,spicy',
    'Prawns pickle coastal favourite chilli garlic masala rich umami seafood kick',
    1,
    'Limited batch'
  ),
  (
    'crab-pickle',
    'Chicken Boneless Pickle',
    '<p>Juicy boneless chicken pieces slow-cooked in gingelly oil with hand-pounded spices.</p>',
    '/assets/images/chicken.jpg',
    58900,
    62900,
    'non-veg-pickles',
    'pickle,non-veg,chicken,boneless,spicy',
    'Chicken boneless pickle tender chicken chunks slow cooked gingelly oil aromatic spices',
    1,
    NULL
  )
ON DUPLICATE KEY UPDATE
  title = VALUES(title),
  description_html = VALUES(description_html),
  hero_image_url = VALUES(hero_image_url),
  price_paise = VALUES(price_paise),
  mrp_paise = VALUES(mrp_paise),
  category_slug = VALUES(category_slug),
  tags = VALUES(tags),
  search_text = VALUES(search_text),
  in_stock = VALUES(in_stock),
  badge = VALUES(badge);

UPDATE product SET hero_image_url = '/assets/images/mutton_gongura.jpg' WHERE slug = 'gongura-mutton';

-- Podis & mixes
INSERT INTO product (slug, title, description_html, hero_image_url, price_paise, mrp_paise, category_slug, tags, search_text, in_stock, badge)
VALUES
  (
    'karivepaku-podi',
    'Karivepaku Podi',
    '<p>Iron-rich curry leaf podi roasted with sesame, lentils and aromatic ghee tempering.</p>',
    '/assets/images/karivepaku_670x.jpg',
    19900,
    22900,
    'podi',
    'podi,karivepaku,curry-leaf,protein',
    'Karivepaku podi curry leaf blend protein rich roasted lentils iron boost',
    1,
    'Daily staple'
  ),
  (
    'sambar-mix',
    'Homestyle Sambar Mix',
    '<p>Classic sambar masala with roasted dal, coriander seeds and Byadgi chillies.</p>',
    '/assets/images/podi.jpg',
    17900,
    20900,
    'podi',
    'podi,sambar,mix,instant',
    'Homestyle sambar mix quick cooking masala roasted dal coriander seeds byadgi chilli',
    1,
    NULL
  ),
  (
    'flax-seed-podi',
    'Flax Seed Podi',
    '<p>Omega-rich flax seeds toasted with garlic, dried coconut and red chillies.</p>',
    '/assets/images/flax_670x.jpg',
    18900,
    21900,
    'podi',
    'podi,flax,healthy,omega',
    'Flax seed podi omega rich toasted seeds garlic coconut red chilli wholesome',
    1,
    'Healthy pick'
  )
ON DUPLICATE KEY UPDATE
  title = VALUES(title),
  description_html = VALUES(description_html),
  hero_image_url = VALUES(hero_image_url),
  price_paise = VALUES(price_paise),
  mrp_paise = VALUES(mrp_paise),
  category_slug = VALUES(category_slug),
  tags = VALUES(tags),
  search_text = VALUES(search_text),
  in_stock = VALUES(in_stock),
  badge = VALUES(badge);

-- Snacks & sweets
INSERT INTO product (slug, title, description_html, hero_image_url, price_paise, mrp_paise, category_slug, tags, search_text, in_stock, badge)
VALUES
  (
    'raagi-murukulu',
    'Raagi Murukulu',
    '<p>Millet murukulu fried crisp with gingelly oil, Byadgi chillies and curry leaf tempering.</p>',
    '/assets/images/raagi-murukulu.jpg',
    12500,
    14900,
    'snacks-sweets',
    'snack,millet,murukulu,raagi',
    'Raagi murukulu crispy millet snack gingelly oil byadgi chilli curry leaf',
    1,
    'Millet special'
  ),
  (
    'pudina-jonna-murukulu',
    'Pudina Jonna Murukulu',
    '<p>Jonna murukulu tossed with fresh mint, green chilli and ajwain for a refreshing crunch.</p>',
    '/assets/images/pudina-jonna-murukulu.jpg',
    12500,
    13900,
    'snacks-sweets',
    'snack,pudina,murukulu,jonna',
    'Pudina jonna murukulu corn flour snack mint green chilli ajwain crispy',
    1,
    NULL
  ),
  (
    'badam-halwa',
    'The Original Badam Halwa',
    '<p>Almond halwa slow-cooked with A2 ghee, saffron strands and freshly ground cardamom.</p>',
    '/assets/images/badam-halwa.jpg',
    50000,
    55900,
    'snacks-sweets',
    'sweet,badam,halwa,festive',
    'Badam halwa decadent almond dessert ghee slow cooked saffron festive favourite',
    1,
    'Bestseller'
  ),
  (
    'aloo-cashew-mixture',
    'Aloo Cashew Mixture',
    '<p>Spiced potato sev blended with roasted cashews, peanuts and curry leaves.</p>',
    '/assets/images/sweets-n-snacks.jpg',
    14000,
    15900,
    'snacks-sweets',
    'snack,mixture,cashew,aloo',
    'Aloo cashew mixture spicy sev roasted cashews peanuts curry leaf snack',
    1,
    NULL
  )
ON DUPLICATE KEY UPDATE
  title = VALUES(title),
  description_html = VALUES(description_html),
  hero_image_url = VALUES(hero_image_url),
  price_paise = VALUES(price_paise),
  mrp_paise = VALUES(mrp_paise),
  category_slug = VALUES(category_slug),
  tags = VALUES(tags),
  search_text = VALUES(search_text),
  in_stock = VALUES(in_stock),
  badge = VALUES(badge);

-- 250 g jars for pickles and podis (priced at half of the 500 g MRP).
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

-- Standard 500 g jars for all seeded catalog items.
INSERT INTO variant (product_id, sku, label, price_paise, in_stock)
SELECT p.id, CONCAT(p.slug, '-500g'), '500 g jar', p.price_paise, TRUE
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
  'raagi-murukulu',
  'pudina-jonna-murukulu',
  'aloo-cashew-mixture',
  'badam-halwa'
)
ON DUPLICATE KEY UPDATE
  label = VALUES(label),
  price_paise = VALUES(price_paise),
  in_stock = VALUES(in_stock);

-- 1 kg jars for pickles and podis (priced at double the 500 g MRP).
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
