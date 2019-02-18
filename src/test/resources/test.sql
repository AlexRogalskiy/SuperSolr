-- table <orders>
INSERT INTO orders(created, createdBy, clientmobile, clientname, description, title) VALUES (CURRENT_TIMESTAMP, 'Test', '+7900 000 00 00', 'Client 01', 'Order description 01', 'Order title 01');
INSERT INTO orders(created, createdBy, clientmobile, clientname, description, title) VALUES (CURRENT_TIMESTAMP, 'Test', '+7900 000 00 00', 'Client 02', 'Order description 02', 'Order title 02');
INSERT INTO orders(created, createdBy, clientmobile, clientname, description, title) VALUES (CURRENT_TIMESTAMP, 'Test', '+7900 000 00 00', 'Client 03', 'Order description 03', 'Order title 03');
INSERT INTO orders(created, createdBy, clientmobile, clientname, description, title) VALUES (CURRENT_TIMESTAMP, 'Test', '+7900 000 00 00', 'Client 04', 'Order description 04', 'Order title 04');
INSERT INTO orders(created, createdBy, clientmobile, clientname, description, title) VALUES (CURRENT_TIMESTAMP, 'Test', '+7900 000 00 00', 'Client 05', 'Order description 05', 'Order title 05');
INSERT INTO orders(created, createdBy, clientmobile, clientname, description, title) VALUES (CURRENT_TIMESTAMP, 'Test', '+7900 000 00 00', 'Client 05', 'Order description 06', 'Order title 06');
INSERT INTO orders(created, createdBy, clientmobile, clientname, description, title) VALUES (CURRENT_TIMESTAMP, 'Test', '+7900 000 00 00', 'Client 05', 'Order description 07', 'Order title 07');
INSERT INTO orders(created, createdBy, clientmobile, clientname, description, title) VALUES (CURRENT_TIMESTAMP, 'Test', '+7900 000 00 00', 'Client 05', 'Order description 08', 'Order title 08');
INSERT INTO orders(created, createdBy, clientmobile, clientname, description, title) VALUES (CURRENT_TIMESTAMP, 'Test', '+7900 000 00 00', 'Client 06', 'Order description 09', 'Order title 09');
INSERT INTO orders(created, createdBy, clientmobile, clientname, description, title) VALUES (CURRENT_TIMESTAMP, 'Test', '+7900 000 00 00', 'Client 06', 'Order description 10', 'Order title 10');
INSERT INTO orders(created, createdBy, clientmobile, clientname, description, title) VALUES (CURRENT_TIMESTAMP, 'Test', '+7900 000 00 00', 'Client 06', 'Order description 11', 'Order title 11');

-- table <categories>
INSERT INTO categories(created, createdBy, description, "index", title) VALUES (CURRENT_TIMESTAMP, 'Test', 'Category description 01', 1, 'Category 01');
INSERT INTO categories(created, createdBy, description, "index", title) VALUES (CURRENT_TIMESTAMP, 'Test', 'Category description 02', 1, 'Category 02');
INSERT INTO categories(created, createdBy, description, "index", title) VALUES (CURRENT_TIMESTAMP, 'Test', 'Category description 03', 1, 'Category 03');
INSERT INTO categories(created, createdBy, description, "index", title) VALUES (CURRENT_TIMESTAMP, 'Test', 'Category description 04', 1, 'Category 04');
INSERT INTO categories(created, createdBy, description, "index", title) VALUES (CURRENT_TIMESTAMP, 'Test', 'Category description 05', 1, 'Category 05');
INSERT INTO categories(created, createdBy, description, "index", title) VALUES (CURRENT_TIMESTAMP, 'Test', 'Category description 06', 1, 'Category 06');
INSERT INTO categories(created, createdBy, description, "index", title) VALUES (CURRENT_TIMESTAMP, 'Test', 'Category description 07', 1, 'Category 07');
INSERT INTO categories(created, createdBy, description, "index", title) VALUES (CURRENT_TIMESTAMP, 'Test', 'Category description 08', 1, 'Category 08');
INSERT INTO categories(created, createdBy, description, "index", title) VALUES (CURRENT_TIMESTAMP, 'Test', 'Category description 09', 1, 'Category 09');
INSERT INTO categories(created, createdBy, description, "index", title) VALUES (CURRENT_TIMESTAMP, 'Test', 'Category description 10', 1, 'Category 10');
INSERT INTO categories(created, createdBy, description, "index", title) VALUES (CURRENT_TIMESTAMP, 'Test', 'Category description 11', 1, 'Category 11');

-- table <products>
INSERT INTO PRODUCTS(created, createdBy, agerestriction, instock, catalognumber, locktype, longdescription, name, pagetitle, price, pricedescription, rating, recommendedprice, shortdescription) VALUES (CURRENT_TIMESTAMP, 'Test', 10, true, 25, 1, 'Long description', 'Product 01', 'Product title 01', 50.00, 'Not in sale', 10, 50.00, 'Short description');
INSERT INTO PRODUCTS(created, createdBy, agerestriction, instock, catalognumber, locktype, longdescription, name, pagetitle, price, pricedescription, rating, recommendedprice, shortdescription) VALUES (CURRENT_TIMESTAMP, 'Test', 15, true, 35, 1, 'Long description', 'Product 02', 'Product title 02', 150.00, 'Not in sale', 8, 150.00, 'Short description');
INSERT INTO PRODUCTS(created, createdBy, agerestriction, instock, catalognumber, locktype, longdescription, name, pagetitle, price, pricedescription, rating, recommendedprice, shortdescription) VALUES (CURRENT_TIMESTAMP, 'Test', 18, true, 15, 1, 'Long description', 'Product 03', 'Product title 03', 250.00, 'Not in sale', 4, 250.00, 'Short description');
INSERT INTO PRODUCTS(created, createdBy, agerestriction, instock, catalognumber, locktype, longdescription, name, pagetitle, price, pricedescription, rating, recommendedprice, shortdescription) VALUES (CURRENT_TIMESTAMP, 'Test', 1, true, 5, 1, 'Long description', 'Product 04', 'Product title 04', 350.00, 'Not in sale', 4, 350.00, 'Short description');
INSERT INTO PRODUCTS(created, createdBy, agerestriction, instock, catalognumber, locktype, longdescription, name, pagetitle, price, pricedescription, rating, recommendedprice, shortdescription) VALUES (CURRENT_TIMESTAMP, 'Test', 4, true, 45, 1, 'Long description', 'Product 05', 'Product title 05', 650.00, 'Not in sale', 4, 650.00, 'Short description');
INSERT INTO PRODUCTS(created, createdBy, agerestriction, instock, catalognumber, locktype, longdescription, name, pagetitle, price, pricedescription, rating, recommendedprice, shortdescription) VALUES (CURRENT_TIMESTAMP, 'Test', 18, true, 5, 1, 'Long description', 'Product 06', 'Product title 06', 750.00, 'Not in sale', 7, 750.00, 'Short description');
INSERT INTO PRODUCTS(created, createdBy, agerestriction, instock, catalognumber, locktype, longdescription, name, pagetitle, price, pricedescription, rating, recommendedprice, shortdescription) VALUES (CURRENT_TIMESTAMP, 'Test', 18, true, 5, 1, 'Long description', 'Product 07', 'Product title 07', 10.00, 'Not in sale', 7, 10.00, 'Short description');
INSERT INTO PRODUCTS(created, createdBy, agerestriction, instock, catalognumber, locktype, longdescription, name, pagetitle, price, pricedescription, rating, recommendedprice, shortdescription) VALUES (CURRENT_TIMESTAMP, 'Test', 8, true, 15, 1, 'Long description', 'Product 08', 'Product title 08', 50.00, 'Not in sale', 7, 50.00, 'Short description');
INSERT INTO PRODUCTS(created, createdBy, agerestriction, instock, catalognumber, locktype, longdescription, name, pagetitle, price, pricedescription, rating, recommendedprice, shortdescription) VALUES (CURRENT_TIMESTAMP, 'Test', 8, true, 5, 1, 'Long description', 'Product 09', 'Product title 09', 40.00, 'Not in sale', 7, 7.00, 'Short description');
INSERT INTO PRODUCTS(created, createdBy, agerestriction, instock, catalognumber, locktype, longdescription, name, pagetitle, price, pricedescription, rating, recommendedprice, shortdescription) VALUES (CURRENT_TIMESTAMP, 'Test', 6, true, 13, 1, 'Long description', 'Product 10', 'Product title 10', 780.00, 'Not in sale', 7, 213.00, 'Short description');
INSERT INTO PRODUCTS(created, createdBy, agerestriction, instock, catalognumber, locktype, longdescription, name, pagetitle, price, pricedescription, rating, recommendedprice, shortdescription) VALUES (CURRENT_TIMESTAMP, 'Test', 18, true, 5, 1, 'Long description', 'Product 11', 'Product title 11', 70.00, 'Not in sale', 7, 18.00, 'Short description');
INSERT INTO PRODUCTS(created, createdBy, agerestriction, instock, catalognumber, locktype, longdescription, name, pagetitle, price, pricedescription, rating, recommendedprice, shortdescription) VALUES (CURRENT_TIMESTAMP, 'Test', 2, true, 11, 1, 'Long description', 'Product 12', 'Product title 12', 710.00, 'Not in sale', 7, 130.00, 'Short description');
INSERT INTO PRODUCTS(created, createdBy, agerestriction, instock, catalognumber, locktype, longdescription, name, pagetitle, price, pricedescription, rating, recommendedprice, shortdescription) VALUES (CURRENT_TIMESTAMP, 'Test', 11, true, 5, 1, 'Long description', 'Product 13', 'Product title 13', 70.00, 'Not in sale', 7, 70.00, 'Short description');
INSERT INTO PRODUCTS(created, createdBy, agerestriction, instock, catalognumber, locktype, longdescription, name, pagetitle, price, pricedescription, rating, recommendedprice, shortdescription) VALUES (CURRENT_TIMESTAMP, 'Test', 8, true, 12, 1, 'Long description', 'Product 14', 'Product title 14', 50.00, 'Not in sale', 7, 10.00, 'Short description');
INSERT INTO PRODUCTS(created, createdBy, agerestriction, instock, catalognumber, locktype, longdescription, name, pagetitle, price, pricedescription, rating, recommendedprice, shortdescription) VALUES (CURRENT_TIMESTAMP, 'Test', 9, true, 5, 1, 'Long description', 'Product 15', 'Product title 15', 40.00, 'Not in sale', 7, 35.00, 'Short description');
INSERT INTO PRODUCTS(created, createdBy, agerestriction, instock, catalognumber, locktype, longdescription, name, pagetitle, price, pricedescription, rating, recommendedprice, shortdescription) VALUES (CURRENT_TIMESTAMP, 'Test', 19, true, 10, 1, 'Long description', 'Product 16', 'Product title 16', 150.00, 'Not in sale', 7, 110.00, 'Short description');
INSERT INTO PRODUCTS(created, createdBy, agerestriction, instock, catalognumber, locktype, longdescription, name, pagetitle, price, pricedescription, rating, recommendedprice, shortdescription) VALUES (CURRENT_TIMESTAMP, 'Test', 4, true, 0, 1, 'Long description', 'Product 17', 'Product title 17', 250.00, 'Not in sale', 7, 90.00, 'Short description');

-- table <attributes>
INSERT INTO ATTRIBUTES(created, createdBy, descriptiontext, keywords, name, "synonym") VALUES (CURRENT_TIMESTAMP, 'Test', 'Description size', 'Test pilot', 'size', 'dimension');
INSERT INTO ATTRIBUTES(created, createdBy, descriptiontext, keywords, name, "synonym") VALUES (CURRENT_TIMESTAMP, 'Test', 'Description color', 'Test pilot', 'color', 'colour');
INSERT INTO ATTRIBUTES(created, createdBy, descriptiontext, keywords, name, "synonym") VALUES (CURRENT_TIMESTAMP, 'Test', 'Description material', 'Test pilot', 'material', 'source');

-- table <product_attribute>
INSERT INTO product_attribute(productid, attributeid) VALUES (1, 1);
INSERT INTO product_attribute(productid, attributeid) VALUES (1, 2);
INSERT INTO product_attribute(productid, attributeid) VALUES (1, 3);
INSERT INTO product_attribute(productid, attributeid) VALUES (2, 1);
INSERT INTO product_attribute(productid, attributeid) VALUES (2, 2);
INSERT INTO product_attribute(productid, attributeid) VALUES (3, 1);
INSERT INTO product_attribute(productid, attributeid) VALUES (3, 3);
INSERT INTO product_attribute(productid, attributeid) VALUES (4, 3);
INSERT INTO product_attribute(productid, attributeid) VALUES (5, 1);

-- table <product_category>
--INSERT INTO product_category(productid, categoryid) VALUES (1, 1);
--INSERT INTO product_category(productid, categoryid) VALUES (2, 4);
--INSERT INTO product_category(productid, categoryid) VALUES (2, 5);
--INSERT INTO product_category(productid, categoryid) VALUES (4, 1);
--INSERT INTO product_category(productid, categoryid) VALUES (5, 1);
--INSERT INTO product_category(productid, categoryid) VALUES (6, 1);
--INSERT INTO product_category(productid, categoryid) VALUES (7, 2);
--INSERT INTO product_category(productid, categoryid) VALUES (8, 2);
--INSERT INTO product_category(productid, categoryid) VALUES (8, 3);
--INSERT INTO product_category(productid, categoryid) VALUES (8, 4);
--INSERT INTO product_category(productid, categoryid) VALUES (8, 5);
--INSERT INTO product_category(productid, categoryid) VALUES (8, 6);
--INSERT INTO product_category(productid, categoryid) VALUES (8, 7);
--INSERT INTO product_category(productid, categoryid) VALUES (8, 8);

-- table <product_main_category>
--INSERT INTO product_main_category(productid, categoryid) VALUES (1, 1);
--INSERT INTO product_main_category(productid, categoryid) VALUES (2, 4);
--INSERT INTO product_main_category(productid, categoryid) VALUES (2, 5);
--INSERT INTO product_main_category(productid, categoryid) VALUES (4, 1);
--INSERT INTO product_main_category(productid, categoryid) VALUES (8, 7);
--INSERT INTO product_main_category(productid, categoryid) VALUES (8, 8);

-- table <product_order>
--INSERT INTO product_order(productid, orderid) VALUES (1, 1);
--INSERT INTO product_order(productid, orderid) VALUES (2, 1);
--INSERT INTO product_order(productid, orderid) VALUES (3, 1);
--INSERT INTO product_order(productid, orderid) VALUES (1, 2);
--INSERT INTO product_order(productid, orderid) VALUES (2, 2);
--INSERT INTO product_order(productid, orderid) VALUES (1, 3);