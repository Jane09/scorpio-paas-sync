/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 80011
Source Host           : localhost:3306
Source Database       : scorpio-honest

Target Server Type    : MYSQL
Target Server Version : 80011
File Encoding         : 65001

Date: 2018-10-29 11:13:10
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for t_temp_customer
-- ----------------------------
DROP TABLE IF EXISTS `t_temp_customer`;
CREATE TABLE `t_temp_customer` (
  `res_id` varchar(32) COLLATE utf8mb4_general_ci NOT NULL,
  `market_project` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `source` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `batch` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `state` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `name` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `sex` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `mobile` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `phone` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `area` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `postcode` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `last_call_time` datetime DEFAULT NULL,
  `call_sum` int(11) DEFAULT NULL,
  `call_result` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `result_desc` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci,
  `call_desc` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `id_number` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `job` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `bank` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `company` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `bank_acct` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `address` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `postcode2` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `birthday` varchar(20) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `age` int(11) DEFAULT NULL,
  `staff_name` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `job_no` varchar(11) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`res_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
