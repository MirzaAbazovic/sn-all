alter table T_GEO_ID_CACHE add TECHNICAL_ID varchar2(255);
alter table T_GEO_ID_CACHE add MODIFIED timestamp;

alter table T_GEO_ID_CACHE add STREETSECTION_ID number(19,0);
alter table T_GEO_ID_CACHE add STREETSECTION_MODIFIED timestamp;

alter table T_GEO_ID_CACHE add ZIPCODE_ID number(19,0);
alter table T_GEO_ID_CACHE add ZIPCODE_MODIFIED timestamp;

alter table T_GEO_ID_CACHE add CITY_ID number(19,0);
alter table T_GEO_ID_CACHE add CITY_MODIFIED timestamp;

alter table T_GEO_ID_CACHE add COUNTRY_ID number(19,0);
alter table T_GEO_ID_CACHE add COUNTRY_MODIFIED timestamp;

alter table T_GEO_ID_CACHE add DISTRICT_ID number(19,0);
alter table T_GEO_ID_CACHE add DISTRICT_MODIFIED timestamp;
alter table T_GEO_ID_CACHE add DISTRICT varchar2(255);

alter table T_GEO_ID_CACHE drop column OLD_STREET_ID;
alter table T_GEO_ID_CACHE drop column DEACTIVATED;

create table T_GEO_ID_CARRIER_ADDRESS (
    ID number(19,0) PRIMARY KEY not null,
    GEO_ID number(19,0) not null,
    CARRIER varchar2(255) NOT NULL,
    STREET varchar2(255) NOT NULL,
    HOUSENUM varchar2(255) NOT NULL,
    HOUSENUMEXTENSION varchar2(255),
    ZIPCODE varchar2(255) NOT NULL,
    DISTRICT varchar2(255),
    CITY varchar2(255) NOT NULL,
    VERSION number(19,0) DEFAULT 0 NOT NULL
);

ALTER TABLE T_GEO_ID_CARRIER_ADDRESS ADD CONSTRAINT FK_GEO_ID_CARRIER_ADR_2_GEOID FOREIGN KEY (GEO_ID) REFERENCES T_GEO_ID_CACHE(ID);
alter table T_GEO_ID_CARRIER_ADDRESS add constraint UQ_GEO_ID_CARRIER_ADDRESS unique (GEO_ID, CARRIER);

create sequence S_T_GEO_ID_CARRIER_ADDRESS_0;

GRANT SELECT, INSERT, UPDATE ON T_GEO_ID_CARRIER_ADDRESS TO R_HURRICAN_USER;
GRANT SELECT ON T_GEO_ID_CARRIER_ADDRESS TO R_HURRICAN_READ_ONLY;
GRANT SELECT ON S_T_GEO_ID_CARRIER_ADDRESS_0 TO PUBLIC;

