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
  `ammount` int NOT NULL,
  `user_id` int NOT NULL,
  `date` datetime NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  auction_id int NOT NULL,
  UNIQUE KEY `auction_bid_UNIQUE` (auction_id,ammount)
);

CREATE TABLE `auction` (
  `id` int NOT NULL AUTO_INCREMENT,
  `product_id` int NOT NULL,
  `start_date` datetime NOT NULL,
  `duration_mins` int NOT NULL,
  `start_price` int NOT NULL,
  `is_active` int NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `is_UNIQUE` (`id`)
);

insert into `user` (username, password, credits, role) values ("root", "root", "10001", "ADMIN");

DROP USER IF EXISTS '22S_CST8288_450_GROUP_2';
CREATE USER '22S_CST8288_450_GROUP_2'@'%' IDENTIFIED BY 'CST8288';
GRANT ALL PRIVILEGES ON oop_capstone.* TO '22S_CST8288_450_GROUP_2'@'%';

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

-- Make Bid
DROP PROCEDURE IF EXISTS make_bid;
DELIMITER // 
CREATE PROCEDURE `make_bid` (in p_auction_id int, in p_user_id int, in p_ammount int)
BEGIN
insert into bid (ammount, user_id, `date`, auction_id) values (p_ammount, p_user_id, NOW(), p_auction_id);
END //
DELIMITER ;

-- PROCESS FINISHED AUCTIONS AND TRANSFER OWNERSHIP
DROP PROCEDURE IF EXISTS `process_finished_auctions`;
DELIMITER // 
CREATE PROCEDURE `process_finished_auctions` ()
BEGIN

CREATE TEMPORARY TABLE IF NOT EXISTS finished_auctions
SELECT 
	auction.id AS auction_id,
    TIMESTAMPDIFF(SECOND,NOW(),DATE_ADD(start_date,interval duration_mins minute)) AS seconds_remaining,
    bid.ammount AS top_bid,
	bid.user_id AS top_bidder,
	owner.id AS owner_id,
    product.id AS product_id
FROM auction 
	
	LEFT JOIN product ON auction.product_id = product.id
    LEFT JOIN user AS owner ON product.owner_id = owner.id
    LEFT JOIN bid ON bid.auction_id = auction.id
	LEFT JOIN bid other ON other.auction_id = bid.auction_id AND bid.ammount < other.ammount 
    WHERE other.id IS NULL
AND auction.is_active = 1
HAVING seconds_remaining < 0;

-- Subtract the bid price from the high bidder's credit count.
UPDATE user JOIN finished_auctions ON user.id = top_bidder
SET user.credits = credits - top_bid;
    
-- Add the bid price to the owner's credits
UPDATE user JOIN finished_auctions ON user.id = owner_id AND top_bidder IS NOT NULL
SET user.credits = credits + top_bid;
    
-- Set the product's owner ID to that of the high bidder.
UPDATE product JOIN finished_auctions ON product.id = product_id AND top_bidder IS NOT NULL
SET product.owner_id = top_bidder;

-- Flag the finished auctions as complete.
UPDATE auction 
SET auction.is_active = 0
WHERE auction.id IN (
	SELECT auction_id FROM finished_auctions
);

DROP TABLE finished_auctions;
END //
DELIMITER ;



-- Get active auctions
DROP PROCEDURE IF EXISTS get_active_auctions;
DELIMITER // 
CREATE PROCEDURE `get_active_auctions` ()
BEGIN
	SELECT 
		auction.*,
		TIMESTAMPDIFF(SECOND,NOW(),DATE_ADD(start_date,interval duration_mins minute)) AS seconds_remaining,
		bid.ammount AS top_bid,
		bid.user_id AS top_bidder,
		owner.id,
		product.*
	FROM auction 
		
		LEFT JOIN product ON auction.product_id = product.id
		LEFT JOIN user AS owner ON product.owner_id = owner.id
		LEFT JOIN bid ON bid.auction_id = auction.id
		LEFT JOIN bid other ON other.auction_id = bid.auction_id AND bid.ammount < other.ammount 
		WHERE other.id IS NULL
	AND auction.is_active = 1;
END //
DELIMITER ;

-- Get active auctions
DROP PROCEDURE IF EXISTS get_auctions;
DELIMITER // 
CREATE PROCEDURE `get_auctions` ()
BEGIN
	SELECT 
		auction.*,
		TIMESTAMPDIFF(SECOND,NOW(),DATE_ADD(start_date,interval duration_mins minute)) AS seconds_remaining,
		bid.ammount AS top_bid,
		bid.user_id AS top_bidder,
		owner.id,
		product.*
	FROM auction 
		
		LEFT JOIN product ON auction.product_id = product.id
		LEFT JOIN user AS owner ON product.owner_id = owner.id
		LEFT JOIN bid ON bid.auction_id = auction.id
		LEFT JOIN bid other ON other.auction_id = bid.auction_id AND bid.ammount < other.ammount 
		WHERE other.id IS NULL;
END //
DELIMITER ;

-- Get user with ID
DROP PROCEDURE IF EXISTS get_user_with_id;
DELIMITER // 
CREATE PROCEDURE `get_user_with_id` (IN userID INT)
BEGIN
	SELECT * FROM user WHERE user.id = userID;
END //
DELIMITER ;

-- Get available credits for a user to bid on an auction.
DROP PROCEDURE IF EXISTS get_user_available_credits;
DELIMITER // 
CREATE PROCEDURE `get_user_available_credits` (
	IN userID INT,
    IN excluded_auction_id INT,
    OUT total INT
)
BEGIN
	DECLARE creditsInUse INT;
    
	SELECT 
		IFNULL(SUM(bid.ammount),0)
	FROM auction 
		
		JOIN product ON auction.product_id = product.id
		JOIN `user` AS `owner` ON product.owner_id = owner.id
		JOIN bid ON bid.auction_id = auction.id
		LEFT JOIN bid higher_bid ON higher_bid.auction_id = bid.auction_id AND bid.ammount < higher_bid.ammount 
        JOIN `user` AS top_bidder ON bid.user_id = top_bidder.id
		WHERE higher_bid.id IS NULL
	AND top_bidder.id = userID
	AND auction.is_active = 1
    AND (excluded_auction_id IS NULL OR auction.id != excluded_auction_id)
    INTO creditsInUse;
    
    SELECT (credits-creditsInUse) FROM `user` WHERE `user`.id = userID INTO total;
    
END //
DELIMITER ;

-- Get participating auctions.
DROP PROCEDURE IF EXISTS get_participating_auctions;
DELIMITER // 

CREATE PROCEDURE `get_participating_auctions`(
	IN user_id INT
)
BEGIN
	
	SELECT 
		auction.*,
		TIMESTAMPDIFF(SECOND,NOW(),DATE_ADD(start_date,interval duration_mins minute)) AS seconds_remaining,
		bid.ammount AS top_bid,
		bid.user_id AS top_bidder,
		owner.id,
		product.*
	FROM (SELECT DISTINCT auction.* FROM bid 
		  JOIN auction ON auction.id = bid.auction_id AND bid.user_id = user_id) AS auction
		LEFT JOIN product ON auction.product_id = product.id
		LEFT JOIN user AS owner ON product.owner_id = owner.id
		LEFT JOIN bid ON bid.auction_id = auction.id
		LEFT JOIN bid other ON other.auction_id = bid.auction_id AND bid.ammount < other.ammount 
		WHERE other.id IS NULL
        ORDER BY bid.date DESC;
END //
DELIMITER ;


-- Get participating auctions.
DROP PROCEDURE IF EXISTS get_started_auctions;
DELIMITER // 

CREATE PROCEDURE `get_started_auctions`(
	IN user_id INT
)
BEGIN
	
	SELECT 
		auction.*,
		TIMESTAMPDIFF(SECOND,NOW(),DATE_ADD(start_date,interval duration_mins minute)) AS seconds_remaining,
		bid.ammount AS top_bid,
		bid.user_id AS top_bidder,
		owner.id,
		product.*
	FROM auction
		LEFT JOIN product ON auction.product_id = product.id
		LEFT JOIN user AS `owner` ON product.owner_id = `owner`.id 
		LEFT JOIN bid ON bid.auction_id = auction.id
		LEFT JOIN bid other ON other.auction_id = bid.auction_id AND bid.ammount < other.ammount 
		WHERE other.id IS NULL AND
        `owner`.id = user_id
        ORDER BY auction.start_date DESC;
END //
DELIMITER ;


