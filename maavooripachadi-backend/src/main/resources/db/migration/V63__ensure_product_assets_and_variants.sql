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
    'Spiced Crab Pickle',
    '<p>Handpicked crab meat cooked with stone-ground masalas and sealed with gingelly oil.</p>',
    '/assets/images/fish.jpg',
    58900,
    62900,
    'non-veg-pickles',
    'pickle,non-veg,seafood,crab,heritage',
    'Spiced crab pickle indulgent seafood delicacy slow cooked stone ground masala',
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
    'bellam-gavvalu',
    'Bellam Gavvalu',
    '<p>Crispy gavvalu coated in glossy jaggery caramel with hints of cardamom.</p>',
    '/assets/images/dry-fruit_670x.jpg',
    24900,
    27900,
    'snacks-sweets',
    'snack,sweet,gavvalu,jaggery',
    'Bellam gavvalu jaggery glazed crunchy shells festive sweet snack classic',
    1,
    'Festive special'
  ),
  (
    'ribbon-pakodi',
    'Ribbon Pakodi',
    '<p>Crunchy ribbon murukku made with rice flour, besan and aromatic chilli-garlic tempering.</p>',
    '/assets/images/raagi-murukulu.jpg',
    22900,
    25900,
    'snacks-sweets',
    'snack,savoury,ribbon,pakodi',
    'Ribbon pakodi crunchy savoury snack rice flour besan chilli garlic tempering',
    1,
    NULL
  ),
  (
    'badam-halwa',
    'Badam Halwa',
    '<p>Rich almond halwa slow-cooked in ghee with saffron, cardamom and condensed milk.</p>',
    '/assets/images/dry-fruit_670x.jpg',
    39900,
    44900,
    'snacks-sweets',
    'sweet,badam,halwa,festive',
    'Badam halwa decadent almond dessert ghee slow cooked saffron festive favourite',
    1,
    'Bestseller'
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

-- Seed basic variants so product pages can add items to cart.
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
  'bellam-gavvalu',
  'ribbon-pakodi',
  'badam-halwa'
)
ON DUPLICATE KEY UPDATE
  label = VALUES(label),
  price_paise = VALUES(price_paise),
  in_stock = VALUES(in_stock);
