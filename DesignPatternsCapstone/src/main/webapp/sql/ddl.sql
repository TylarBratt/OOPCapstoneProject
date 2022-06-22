CREATE DATABASE IF NOT EXISTS oop_capstone;
USE oop_capstone;
DROP TABLE IF EXISTS `user`;
DROP TABLE IF EXISTS product;
DROP TABLE IF EXISTS bid;
DROP TABLE IF EXISTS auction;

CREATE TABLE `user` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(60) NOT NULL,
  `password` varchar(45) NOT NULL,
  `credits` int NOT NULL,
  `role` varchar(10) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  UNIQUE KEY `username_UNIQUE` (`username`)
);
CREATE TABLE `product` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `owner_id` int NOT NULL,
  `image_path` varchar(45) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
);
CREATE TABLE `bid` (
  `id` int NOT NULL AUTO_INCREMENT,
  auction_id int NOT NULL,
  `ammount` int NOT NULL,
  `user_id` int NOT NULL,
  `date` datetime NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
);

CREATE TABLE `auction` (
  `id` int NOT NULL AUTO_INCREMENT,
  `product_id` int NOT NULL,
  `start_date` datetime NOT NULL,
  `duration_mins` int NOT NULL,
  `start_price` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `is_UNIQUE` (`id`)
);

insert into `user` (username, password, credits, role) values ("root", "root", "10001", "ADMIN");






DROP PROCEDURE IF EXISTS insert_product;
DELIMITER // 
CREATE PROCEDURE `insert_product` (in p_name varchar(45), in p_owner_id int, in p_image varchar(45))
BEGIN
insert into product (name, owner_id, image_path) values (p_name, p_owner_id, p_image);
select * from product where id = last_insert_id();
END //
DELIMITER ;

DROP PROCEDURE IF EXISTS insert_user;
DELIMITER // 
CREATE PROCEDURE `insert_user`(in p_name varchar(60), in p_password varchar(45), in p_credits int, in p_role varchar(10))
BEGIN
insert into user (username, password, credits, role) values (p_name, p_password, p_credits, p_role);
select * from user where id = last_insert_id();
END //
DELIMITER ;

DROP PROCEDURE IF EXISTS make_auction;
DELIMITER // 
CREATE PROCEDURE `make_auction` (in p_product_id int, in p_duration int, in p_start_price int)
BEGIN
insert into auction (product_id, start_date, duration_mins, start_price) values (p_product_id, NOW(), p_duration, p_start_price);
select * from auction
JOIN product on auction.product_id = product.id 
JOIN user ON product.owner_id = user.id 
WHERE auction.id = last_insert_id();
END //
DELIMITER ;

