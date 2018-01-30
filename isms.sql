create schema isms_schema;

CREATE TABLE IF NOT EXISTS `isms_schema`.`ISMS_STANDARD`  (
  `STANDARD_ID` BIGINT NOT NULL,
  `STANDARD_TYPE` VARCHAR(64) NULL,
  `IS_EVALUATION` INT NULL DEFAULT 0,
  `NAME` TEXT NULL,
  PRIMARY KEY (`STANDARD_ID`));

CREATE TABLE IF NOT EXISTS `isms_schema`.`ISMS_STANDARD_NODE` (
  `NODE_ID` BIGINT NOT NULL,
  `STANDARD_ID` BIGINT NULL,
  `NODE_TYPE` VARCHAR(64) NULL,
  `NAME` TEXT NULL,
  `NODE_POSITION` INT NULL,
  `PARENT_NODE_ID` BIGINT NULL,
  PRIMARY KEY (`NODE_ID`));
  
CREATE TABLE IF NOT EXISTS `isms_schema`.`ISMS_BOOLEAN_PROPERTY` (
  `PROPERTY_ID` BIGINT NOT NULL,
  `NODE_ID` BIGINT NULL,
  `NAME` VARCHAR(64) NULL,
  `READONLY` INT NULL DEFAULT 0,
  `VALUE` INT NULL DEFAULT 0,
  PRIMARY KEY (`PROPERTY_ID`));
  
CREATE TABLE IF NOT EXISTS `isms_schema`.`ISMS_ENUM_PROPERTY` (
  `PROPERTY_ID` BIGINT NOT NULL,
  `NODE_ID` BIGINT NULL,
  `NAME` VARCHAR(64) NULL,
  `READONLY` INT NULL DEFAULT 0,
  `VALUE` VARCHAR(64) NULL,
  `ENUM_TYPE` VARCHAR(64) NULL,
  PRIMARY KEY (`PROPERTY_ID`));
  
CREATE TABLE IF NOT EXISTS `isms_schema`.`ISMS_FLOAT_PROPERTY` (
  `PROPERTY_ID` BIGINT NOT NULL,
  `NODE_ID` BIGINT NULL,
  `NAME` VARCHAR(64) NULL,
  `READONLY` INT NULL DEFAULT 0,
  `VALUE` DOUBLE NULL DEFAULT 0,
  PRIMARY KEY (`PROPERTY_ID`));
  
CREATE TABLE IF NOT EXISTS `isms_schema`.`ISMS_STRING_PROPERTY` (
  `PROPERTY_ID` BIGINT NOT NULL,
  `NODE_ID` BIGINT NULL,
  `NAME` VARCHAR(64) NULL,
  `READONLY` INT NULL DEFAULT 0,
  `VALUE` TEXT NULL,
  PRIMARY KEY (`PROPERTY_ID`));
  
CREATE TABLE IF NOT EXISTS `isms_schema`.`ISMS_EVIDENCE_PROPERTY` (
  `PROPERTY_ID` BIGINT NOT NULL,
  `NODE_ID` BIGINT NULL,
  `NAME` VARCHAR(64) NULL,
  `READONLY` INT NULL DEFAULT 0,
  `EVIDENCE_ID` BIGINT NULL,
  PRIMARY KEY (`PROPERTY_ID`));
  
CREATE TABLE IF NOT EXISTS `isms_schema`.`ISMS_EVIDENCE` (
  `EVIDENCE_ID` BIGINT NOT NULL,
  `NAME` TEXT NULL,
  `DESCRIPTION` TEXT NULL,
  PRIMARY KEY (`EVIDENCE_ID`));