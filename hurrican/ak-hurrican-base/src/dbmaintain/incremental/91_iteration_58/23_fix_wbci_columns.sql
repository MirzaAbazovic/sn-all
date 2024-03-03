alter table T_WBCI_GESCHAEFTSFALL modify VORABSTIMMUNGSID VARCHAR2(21) not null;
alter table T_WBCI_GESCHAEFTSFALL modify AUFNEHMENDEREKP VARCHAR2(10) not null;
alter table T_WBCI_GESCHAEFTSFALL modify ABGEBENDEREKP VARCHAR2(10) not null;
alter table T_WBCI_GESCHAEFTSFALL modify ABSENDER VARCHAR2(10) not null;

alter table T_WBCI_GESCHAEFTSFALL modify VERSION not null;
alter table T_WBCI_GESCHAEFTSFALL modify ENDKUNDE_ID not null;
alter table T_WBCI_GESCHAEFTSFALL modify KUNDENWUNSCHTERMIN not null;

alter table T_WBCI_MELDUNG modify VORABSTIMMUNGSID not null;
alter table T_WBCI_MELDUNG modify TYP not null;
alter table T_WBCI_MELDUNG modify GESCHAEFTSFALLTYP not null;
alter table T_WBCI_MELDUNG modify AUFNEHMENDEREKP not null;
alter table T_WBCI_MELDUNG modify ABGEBENDEREKP not null;
alter table T_WBCI_MELDUNG modify ABSENDER not null;
alter table T_WBCI_MELDUNG modify VERSION not null;

alter table T_WBCI_MELDUNG_POSITION modify VERSION not null;
alter table T_WBCI_MELDUNG_POSITION modify POSITION_TYP not null;
alter table T_WBCI_MELDUNG_POSITION modify MELDUNG_CODE not null;
alter table T_WBCI_MELDUNG_POSITION modify MELDUNG_TEXT not null;

alter table T_WBCI_MPOS_ABBM_RUFNUMMER modify VERSION not null;
alter table T_WBCI_MPOS_ABBM_RUFNUMMER modify MPOS_ABBM_ID not null;
alter table T_WBCI_MPOS_ABBM_RUFNUMMER modify RUFNUMMER not null;

alter table T_WBCI_PERSON_ODER_FIRMA modify VERSION not null;
alter table T_WBCI_PROJEKT modify VERSION not null;
alter table T_WBCI_RUFNUMMER_ONKZ modify VERSION not null;
alter table T_WBCI_RUFNUMMERNBLOCK modify VERSION not null;
alter table T_WBCI_RUFNUMMERPORTIERUNG modify VERSION not null;
alter table T_WBCI_TECHNISCHE_RESSOURCE modify VERSION not null;

