-- flache Struktur durch hierarchisches Modell ersetzen

ALTER TABLE T_GEO_ID_CACHE DROP COLUMN STREET;
ALTER TABLE T_GEO_ID_CACHE DROP COLUMN STREETSECTION_MODIFIED;
ALTER TABLE T_GEO_ID_CACHE DROP COLUMN ZIPCODE;
ALTER TABLE T_GEO_ID_CACHE DROP COLUMN ZIPCODE_ID;
ALTER TABLE T_GEO_ID_CACHE DROP COLUMN ZIPCODE_MODIFIED;
ALTER TABLE T_GEO_ID_CACHE DROP COLUMN CITY;
ALTER TABLE T_GEO_ID_CACHE DROP COLUMN CITY_ID;
ALTER TABLE T_GEO_ID_CACHE DROP COLUMN CITY_MODIFIED;
ALTER TABLE T_GEO_ID_CACHE DROP COLUMN COUNTRY;
ALTER TABLE T_GEO_ID_CACHE DROP COLUMN COUNTRY_ID;
ALTER TABLE T_GEO_ID_CACHE DROP COLUMN COUNTRY_MODIFIED;
ALTER TABLE T_GEO_ID_CACHE DROP COLUMN DISTRICT_ID;
ALTER TABLE T_GEO_ID_CACHE DROP COLUMN DISTRICT_MODIFIED;
ALTER TABLE T_GEO_ID_CACHE DROP COLUMN DISTRICT;

ALTER TABLE T_GEO_ID_CACHE ADD SERVICEABLE CHAR(1) DEFAULT '0' NOT NULL;
ALTER TABLE T_GEO_ID_CACHE ADD DISTANCE VARCHAR2(255);
UPDATE T_GEO_ID_CACHE
SET modified = timestamp'2000-01-01 00:00:00' WHERE modified IS NULL;
ALTER TABLE T_GEO_ID_CACHE MODIFY MODIFIED TIMESTAMP DEFAULT timestamp'2000-01-01 00:00:00' NOT NULL;
ALTER TABLE T_GEO_ID_CACHE ADD REPLACED_BY_ID NUMBER(19, 0);

CREATE TABLE T_GEO_ID_STREET_SECTION (
  ID             NUMBER(19, 0) PRIMARY KEY NOT NULL,
  REPLACED_BY_ID NUMBER(19, 0),
  VERSION        NUMBER(19, 0) DEFAULT 0   NOT NULL,
  TECHNICAL_ID   VARCHAR2(255),
  MODIFIED       TIMESTAMP                 NOT NULL,
  SERVICEABLE    CHAR(1) DEFAULT '0' NOT NULL,

  NAME           VARCHAR2(255)             NOT NULL,

  ZIP_CODE_ID    NUMBER(19, 0)             NOT NULL,
  DISTRICT_ID    NUMBER(19, 0)
);

CREATE TABLE T_GEO_ID_DISTRICT (
  ID             NUMBER(19, 0) PRIMARY KEY NOT NULL,
  REPLACED_BY_ID NUMBER(19, 0),
  VERSION        NUMBER(19, 0) DEFAULT 0   NOT NULL,
  TECHNICAL_ID   VARCHAR2(255),
  MODIFIED       TIMESTAMP                 NOT NULL,
  SERVICEABLE    CHAR(1) DEFAULT '0' NOT NULL,

  NAME           VARCHAR2(255)             NOT NULL
);

CREATE TABLE T_GEO_ID_ZIPCODE (
  ID             NUMBER(19, 0) PRIMARY KEY NOT NULL,
  REPLACED_BY_ID NUMBER(19, 0),
  VERSION        NUMBER(19, 0) DEFAULT 0   NOT NULL,
  TECHNICAL_ID   VARCHAR2(255),
  MODIFIED       TIMESTAMP                 NOT NULL,
  SERVICEABLE    CHAR(1) DEFAULT '0' NOT NULL,

  ZIP_CODE       VARCHAR2(255)             NOT NULL,

  CITY_ID        NUMBER(19, 0)             NOT NULL
);

CREATE TABLE T_GEO_ID_CITY (
  ID             NUMBER(19, 0) PRIMARY KEY NOT NULL,
  REPLACED_BY_ID NUMBER(19, 0),
  VERSION        NUMBER(19, 0) DEFAULT 0   NOT NULL,
  TECHNICAL_ID   VARCHAR2(255),
  MODIFIED       TIMESTAMP                 NOT NULL,
  SERVICEABLE    CHAR(1) DEFAULT '0' NOT NULL,

  NAME           VARCHAR2(255)             NOT NULL,

  COUNTRY_ID     NUMBER(19, 0)             NOT NULL
);

CREATE TABLE T_GEO_ID_COUNTRY (
  ID             NUMBER(19, 0) PRIMARY KEY NOT NULL,
  REPLACED_BY_ID NUMBER(19, 0),
  VERSION        NUMBER(19, 0) DEFAULT 0   NOT NULL,
  TECHNICAL_ID   VARCHAR2(255),
  MODIFIED       TIMESTAMP                 NOT NULL,
  SERVICEABLE    CHAR(1) DEFAULT '0' NOT NULL,

  NAME           VARCHAR2(255)             NOT NULL
);

UPDATE T_GEO_ID_CACHE SET streetsection_id = null;
ALTER TABLE T_GEO_ID_CACHE ADD CONSTRAINT FK_GEO_ID_BUILDING FOREIGN KEY (STREETSECTION_ID) REFERENCES T_GEO_ID_STREET_SECTION (ID);
ALTER TABLE T_GEO_ID_STREET_SECTION ADD CONSTRAINT FK_GEO_ID_STREET_SECTION_1 FOREIGN KEY (ZIP_CODE_ID) REFERENCES T_GEO_ID_ZIPCODE (ID);
ALTER TABLE T_GEO_ID_STREET_SECTION ADD CONSTRAINT FK_GEO_ID_STREET_SECTION_2 FOREIGN KEY (DISTRICT_ID) REFERENCES T_GEO_ID_DISTRICT (ID);
ALTER TABLE T_GEO_ID_ZIPCODE ADD CONSTRAINT FK_GEO_ID_ZIP_CODE FOREIGN KEY (CITY_ID) REFERENCES T_GEO_ID_CITY (ID);
ALTER TABLE T_GEO_ID_CITY ADD CONSTRAINT FK_GEO_ID_CITY FOREIGN KEY (COUNTRY_ID) REFERENCES T_GEO_ID_COUNTRY (ID);

GRANT SELECT, INSERT, UPDATE ON T_GEO_ID_STREET_SECTION TO R_HURRICAN_USER;
GRANT SELECT ON T_GEO_ID_STREET_SECTION TO R_HURRICAN_READ_ONLY;

GRANT SELECT, INSERT, UPDATE ON T_GEO_ID_DISTRICT TO R_HURRICAN_USER;
GRANT SELECT ON T_GEO_ID_DISTRICT TO R_HURRICAN_READ_ONLY;

GRANT SELECT, INSERT, UPDATE ON T_GEO_ID_ZIPCODE TO R_HURRICAN_USER;
GRANT SELECT ON T_GEO_ID_ZIPCODE TO R_HURRICAN_READ_ONLY;

GRANT SELECT, INSERT, UPDATE ON T_GEO_ID_CITY TO R_HURRICAN_USER;
GRANT SELECT ON T_GEO_ID_CITY TO R_HURRICAN_READ_ONLY;

GRANT SELECT, INSERT, UPDATE ON T_GEO_ID_COUNTRY TO R_HURRICAN_USER;
GRANT SELECT ON T_GEO_ID_COUNTRY TO R_HURRICAN_READ_ONLY;