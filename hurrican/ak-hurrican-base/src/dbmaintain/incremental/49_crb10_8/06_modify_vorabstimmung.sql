alter table T_VORABSTIMMUNG drop constraint FK_VORAB_2_ADDRESS;
alter table T_VORABSTIMMUNG drop column PREVIOUS_LOCATION_ADDRESS_ID;

alter table T_VORABSTIMMUNG add PREVIOUSLOCATIONADDRESS_ID NUMBER(10);
ALTER TABLE T_VORABSTIMMUNG ADD CONSTRAINT
    FK_VORAB_2_ADDRESS FOREIGN KEY (PREVIOUSLOCATIONADDRESS_ID)
    REFERENCES T_ADDRESS (ID);