-- added new columns to WBCI_REQUEST, which were required for persisting STORNO Requests

alter table T_WBCI_REQUEST add AENDERUNGS_ID varchar2(21);
alter table T_WBCI_REQUEST add STORNO_GRUND varchar2(255 char);
alter table T_WBCI_REQUEST add ENDKUNDE_ID number(19,0);
alter table T_WBCI_REQUEST add STANDORT_ID number(19,0);
alter table T_WBCI_REQUEST add constraint FKCF2142AC5C16E2B foreign key (ENDKUNDE_ID) references T_WBCI_PERSON_ODER_FIRMA;
alter table T_WBCI_REQUEST add constraint FKCF2142AC57F9FC2B foreign key (STANDORT_ID) references T_WBCI_STANDORT;
