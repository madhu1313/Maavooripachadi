-- Align existing crab pickle record with the renamed Chicken Boneless Pickle metadata.

UPDATE product
SET
  title = 'Chicken Boneless Pickle',
  description_html = '<p>Juicy boneless chicken pieces slow-cooked in gingelly oil with hand-pounded spices.</p>',
  hero_image_url = '/assets/images/chicken.jpg',
  tags = 'pickle,non-veg,chicken,boneless,spicy',
  search_text = 'Chicken boneless pickle tender chicken chunks slow cooked gingelly oil aromatic spices'
WHERE slug = 'crab-pickle';
