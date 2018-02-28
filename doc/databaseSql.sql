-- auto-generated definition
CREATE TABLE ISMS_DATA_CLASS
(
    CLASS_ID bigint PRIMARY KEY NOT NULL,
    CLASS_TYPE varchar(64) NOT NULL,
    PARENT_ID bigint DEFAULT 0 NOT NULL,
    CLASS_NAME varchar(64)
)

