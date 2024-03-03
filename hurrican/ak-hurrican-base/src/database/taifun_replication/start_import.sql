delete from AREA;
delete from ACCOUNT;
delete from ADRESSE;
delete from ADDRESS_FORMAT;
delete from DN;
delete from DN_BLOCK;
delete from AUFTRAGPOS;
delete from KUNDE;
delete from AUFTRAG;
delete from CONTACT;
delete from PERSON;
delete from OE;
delete from OE_LANG;
delete from LEISTUNG;
delete from LEISTUNG_LANG;
delete from PERSON;

load data local infile "./MISTRAL_AREA.txt" into table AREA
FIELDS TERMINATED BY ';' ENCLOSED BY '' ESCAPED BY '\\'
LINES TERMINATED BY '\n';

load data local infile "./MISTRAL_ACCOUNT.txt" into table ACCOUNT
FIELDS TERMINATED BY ';' ENCLOSED BY '' ESCAPED BY '\\'
LINES TERMINATED BY '\n';

load data local infile "./MISTRAL_LEISTUNG.txt" into table LEISTUNG
FIELDS TERMINATED BY ';' ENCLOSED BY '' ESCAPED BY '\\'
LINES TERMINATED BY '\n';

load data local infile "./MISTRAL_LEISTUNG_LANG.txt" into table LEISTUNG_LANG
FIELDS TERMINATED BY ';' ENCLOSED BY '' ESCAPED BY '\\'
LINES TERMINATED BY '\n';

load data local infile "./MISTRAL_OE.txt" into table OE
FIELDS TERMINATED BY ';' ENCLOSED BY '' ESCAPED BY '\\'
LINES TERMINATED BY '\n';

load data local infile "./MISTRAL_OE_LANG.txt" into table OE_LANG
FIELDS TERMINATED BY ';' ENCLOSED BY '' ESCAPED BY '\\'
LINES TERMINATED BY '\n';

load data local infile "./MISTRAL_PERSON.txt" into table PERSON
FIELDS TERMINATED BY ';' ENCLOSED BY '' ESCAPED BY '\\'
LINES TERMINATED BY '\n';

load data local infile "./MISTRAL_CONTACT.txt" into table CONTACT
FIELDS TERMINATED BY ';' ENCLOSED BY '' ESCAPED BY '\\'
LINES TERMINATED BY '\n';

load data local infile "./MISTRAL_AUFTRAG.txt" into table AUFTRAG
FIELDS TERMINATED BY ';' ENCLOSED BY '' ESCAPED BY '\\'
LINES TERMINATED BY '\n';

load data local infile "./MISTRAL_KUNDE.txt" into table KUNDE
FIELDS TERMINATED BY ';' ENCLOSED BY '' ESCAPED BY '\\'
LINES TERMINATED BY '\n';

load data local infile "./MISTRAL_AUFTRAGPOS.txt" into table AUFTRAGPOS
FIELDS TERMINATED BY ';' ENCLOSED BY '' ESCAPED BY '\\'
LINES TERMINATED BY '\n';

load data local infile "./MISTRAL_DN.txt" into table DN
FIELDS TERMINATED BY ';' ENCLOSED BY '' ESCAPED BY '\\'
LINES TERMINATED BY '\n';

load data local infile "./MISTRAL_DN_BLOCK.txt" into table DN_BLOCK
FIELDS TERMINATED BY ';' ENCLOSED BY '' ESCAPED BY '\\'
LINES TERMINATED BY '\n';

load data local infile "./MISTRAL_ADDRESS_FORMAT.txt" into table ADDRESS_FORMAT
FIELDS TERMINATED BY ';' ENCLOSED BY '' ESCAPED BY '\\'
LINES TERMINATED BY '\n';

load data local infile "./MISTRAL_ADRESSE.txt" into table ADRESSE
FIELDS TERMINATED BY ';' ENCLOSED BY '' ESCAPED BY '\\'
LINES TERMINATED BY '\n';


update auftragpos set charge_to=null where charge_to='0000-00-00';
update auftragpos set charge_from=null where charge_from='0000-00-00';
update auftragpos set charged_until=null where charged_until='0000-00-00';
update auftrag set gueltig_von=null where gueltig_von='0000-00-00';
update auftrag set gueltig_bis=null where gueltig_bis='0000-00-00';
update auftrag set WUNSCH_TERMIN=null where WUNSCH_TERMIN='0000-00-00';
update auftrag set DATEW=null where DATEW='0000-00-00';
update auftrag set VERTRAGSDATUM=null where VERTRAGSDATUM='0000-00-00';
update account set DATEW=null where DATEW='0000-00-00';
update dn_block set VALID_FROM=null where VALID_FROM='0000-00-00';
update dn_block set VALID_TO=null where VALID_TO='0000-00-00';
update dn_block set PUBLISH_DATE=null where PUBLISH_DATE='0000-00-00';
update dn set VALID_FROM=null where VALID_FROM='0000-00-00';
update dn set VALID_TO=null where VALID_TO='0000-00-00';
update dn set FREE_DATE=null;
update dn set WISH_DATE=null;
update dn set WISH_TIME=null;
update dn set REAL_DATE=null;
update dn set REAL_TIME_FROM=null;
update dn set REAL_TIME_TO=null;
update dn set PORT_DOC_SENT=null;
update dn set PORT_DOC_RECEIVED=null;
update kunde set gueltig_von=null where gueltig_von='0000-00-00';
update kunde set gueltig_bis=null where gueltig_bis='0000-00-00';
update kunde set LISTENDATUM=null where LISTENDATUM='0000-00-00';
update kunde set DATEW=null where DATEW='0000-00-00';
update leistung set gueltig_von=null where gueltig_von='0000-00-00';
update leistung set gueltig_bis=null where gueltig_bis='0000-00-00';
update oe set gueltig_von=null where gueltig_von='0000-00-00';
update oe set gueltig_bis=null where gueltig_bis='0000-00-00';








