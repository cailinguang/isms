SET SCHEMA APP;

CREATE TABLE APP.ISMS_STANDARD  (
  STANDARD_ID BIGINT NOT NULL,
  STANDARD_TYPE VARCHAR(64),
  IS_EVALUATION INT DEFAULT 0,
  NAME LONG VARCHAR,
  DESCRIPTION LONG VARCHAR,
  ARCHIVED INT DEFAULT 0,
  PRIMARY KEY (STANDARD_ID));

CREATE TABLE APP.ISMS_STANDARD_NODE (
  NODE_ID BIGINT NOT NULL,
  STANDARD_ID BIGINT,
  NODE_TYPE VARCHAR(64),
  NAME LONG VARCHAR,
  NODE_POSITION INT,
  PARENT_NODE_ID BIGINT,
  PRIMARY KEY (NODE_ID));
  
CREATE TABLE APP.ISMS_BOOLEAN_PROPERTY (
  PROPERTY_ID BIGINT NOT NULL,
  NODE_ID BIGINT,
  NAME VARCHAR(64),
  READONLY INT DEFAULT 0,
  VALUE INT DEFAULT 0,
  STANDARD_ID BIGINT,
  PRIMARY KEY (PROPERTY_ID));
  
CREATE TABLE APP.ISMS_ENUM_PROPERTY (
  PROPERTY_ID BIGINT NOT NULL,
  NODE_ID BIGINT,
  NAME VARCHAR(64),
  READONLY INT DEFAULT 0,
  VALUE VARCHAR(64),
  ENUM_TYPE VARCHAR(64),
  STANDARD_ID BIGINT,
  PRIMARY KEY (PROPERTY_ID));
  
CREATE TABLE APP.ISMS_FLOAT_PROPERTY (
  PROPERTY_ID BIGINT NOT NULL,
  NODE_ID BIGINT,
  NAME VARCHAR(64),
  READONLY INT DEFAULT 0,
  VALUE DOUBLE DEFAULT 0,
  STANDARD_ID BIGINT,
  PRIMARY KEY (PROPERTY_ID));
  
CREATE TABLE APP.ISMS_STRING_PROPERTY (
  PROPERTY_ID BIGINT NOT NULL,
  NODE_ID BIGINT,
  NAME VARCHAR(64),
  READONLY INT DEFAULT 0,
  VALUE LONG VARCHAR,
  STANDARD_ID BIGINT,
  PRIMARY KEY (PROPERTY_ID));
  
CREATE TABLE APP.ISMS_EVIDENCE_PROPERTY (
  PROPERTY_ID BIGINT NOT NULL,
  NODE_ID BIGINT,
  NAME VARCHAR(64),
  READONLY INT DEFAULT 0,
  EVIDENCE_ID BIGINT,
  EVIDENCE_NAME LONG VARCHAR,
  EVIDENCE_DESCRIPTION LONG VARCHAR,
  EVIDENCE_PATH VARCHAR(256),
  EVIDENCE_CONTENT_TYPE VARCHAR(256),
  STANDARD_ID BIGINT,
  PRIMARY KEY (PROPERTY_ID));
    
CREATE TABLE APP.ISMS_EVIDENCE (
  EVIDENCE_ID BIGINT NOT NULL,
  NAME VARCHAR(256),
  DESCRIPTION VARCHAR(256),
  PATH VARCHAR(256),
  CONTENT_TYPE VARCHAR(256),
  ARCHIVED INT DEFAULT 0,
  PRIMARY KEY (EVIDENCE_ID));
  
 CREATE TABLE APP.ISMS_USERS (
 	USERNAME VARCHAR(64) NOT NULL,
 	PASSWORD VARCHAR(64) NOT NULL, 
 	ROLE VARCHAR(64) NOT NULL,
 	PRIMARY KEY (USERNAME));