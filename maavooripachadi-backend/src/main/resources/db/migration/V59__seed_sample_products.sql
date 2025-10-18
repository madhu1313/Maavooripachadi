INSERT INTO product (
    slug,
    title,
    description_html,
    hero_image_url,
    price_paise,
    mrp_paise,
    category_slug,
    tags,
    search_text,
    in_stock,
    badge
) VALUES
  (
    'mango-avakai',
    'Signature Mango Avakai Pickle',
    '<p>Grandmother-style mango avakai slow cured in cold-pressed gingelly oil.</p>',
    'https://cdn.maavooripachadi.com/images/pickles/mango-maagai_670x.jpg',
    34900,
    39900,
    'veg-pickles',
    'pickle,mango,avakai,veg',
    'Signature mango avakai pickle cold pressed gingelly oil traditional Andhra favourite',
    1,
    'Bestseller'
  ),
  (
    'gongura-mutton',
    'Gongura Mutton Pickle',
    '<p>Andhra-style gongura simmered with tender boneless mutton for the perfect tangy hit.</p>',
    'https://cdn.maavooripachadi.com/images/pickles/mutton_gongura.jpg',
    49900,
    54900,
    'non-veg-pickles',
    'pickle,gongura,mutton,non-veg,spicy',
    'Gongura mutton pickle tangy spicy non veg favourite slow cooked tender meat',
    1,
    'Hot'
  ),
  (
    'millet-murukulu',
    'Millet Murukulu',
    '<p>Crisp, airy murukulu made with foxtail millet flour and roasted gram.</p>',
    'https://cdn.maavooripachadi.com/images/snacks/raagi-murukulu.jpg',
    21900,
    24900,
    'snacks-sweets',
    'snack,millet,murukulu,tea-time',
    'Millet murukulu crunchy tea time snack foxtail millet roasted gram handcrafted savoury',
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
