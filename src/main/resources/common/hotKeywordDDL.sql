CREATE DATABASE `relation` /*!40100 DEFAULT CHARACTER SET utf8 */;

CREATE TABLE `relation`.`hotkeywords` (
  `id` INT unsigned NOT NULL AUTO_INCREMENT,
  `keyword` VARCHAR(255) NOT NULL,
  `image` TEXT NOT NULL,
  `google` TINYINT NOT NULL DEFAULT 0,
  `naver` TINYINT NOT NULL DEFAULT 0,
  `daum` TINYINT NOT NULL DEFAULT 0,
  `timestamp` DATETIME NOT NULL,
  PRIMARY KEY (`id`)
  )ENGINE=InnoDB DEFAULT CHARSET=utf8;
