--
-- Table structure for table `customer`
--

DROP TABLE IF EXISTS `customer`;
CREATE TABLE `customer` (
  `customer_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(200) NOT NULL,
  PRIMARY KEY (`customer_id`)
);

--
-- Dumping data for table `customer`
--

INSERT INTO `customer` VALUES (1000,'Customer-1'),(1001,'Customer-2'),(1002,'Customer-3'), (1003,'Customer-4'),(1004,'Customer-5'),(1005,'Customer-6');

--
-- Table structure for table `account`
--

DROP TABLE IF EXISTS `account`;
CREATE TABLE `account` (
  `customer_id` bigint(20) NOT NULL,
  `account_id` bigint(20) NOT NULL,
  PRIMARY KEY (`account_id`),
  KEY `fk_customerid` (`customer_id`),
  CONSTRAINT `fk_customerid` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`customer_id`)
);

--
-- Dumping data for table `account`
--

INSERT INTO `account` VALUES (1000,5000),(1001,5001),(1002, 5002),(1003,5003),(1004,5004),(1005,5005);

--
-- Table structure for table `balance`
--

DROP TABLE IF EXISTS `balance`;
CREATE TABLE `balance` (
  `account_id` bigint(20) NOT NULL PRIMARY KEY,
  `amount` decimal(13,2) NOT NULL,
  `version` bigint(20) NOT NULL,
  KEY `fk_accountid` (`account_id`),
  CONSTRAINT `fk_accountid` FOREIGN KEY (`account_id`) REFERENCES `account` (`account_id`)
);

--
-- Dumping data for table `balance`
--

INSERT INTO `balance` VALUES (5000,89789680,0),(5001,67865788,0),(5002,2343466,0),(5003,987796.00,0),(5004,0.00,0),(5005,7865785678.00,0);