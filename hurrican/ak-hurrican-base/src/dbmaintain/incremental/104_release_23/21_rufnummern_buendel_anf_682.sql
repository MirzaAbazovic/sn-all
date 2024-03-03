 -- Neues Rufnummern-Mapping Taifun - Hurrican für Endgerätewahlfreiheit ANF-682
CREATE OR REPLACE PROCEDURE create_lb2produkt(lbID IN NUMBER, leistungNO IN NUMBER, produktNO IN NUMBER,
                                                beschreibung IN VARCHAR2)
IS
  BEGIN
    Insert into T_LB_2_PRODUKT (LB_ID, LEISTUNG__NO, PRODUCT_OE__NO, DESCRIPTION, VERSION)
     select lbID, leistungNO, produktNO, beschreibung, 0
     from dual where not exists (select * from T_LB_2_PRODUKT where LB_ID = lbID and LEISTUNG__NO = leistungNO and PRODUCT_OE__NO = produktNO);
  END;
/

-- Komfort-Anschluss --> LB 17
CALL create_lb2produkt(17,92742,3401,unistr('\00DC') || '/SFF Komfort-Anschluss (08/16)');
CALL create_lb2produkt(17,92745,3404,unistr('\00DC') || '/SFF Sondertarif Komfort-Anschluss (08/16)');

DROP PROCEDURE create_lb2produkt;

-- Umlaute in Rufnummernleistungen aus SQL-Skript:  ...104_release_23/14_rufnummer_buendel_anf_725.sql korrigieren

UPDATE T_LB_2_PRODUKT SET DESCRIPTION =  unistr('\00DC') || '/SFF 300/50 inkl. Komf-Ansch. (08/16)' where  LB_ID = 17  and LEISTUNG__NO = 92358 and PRODUCT_OE__NO = 3401;
UPDATE T_LB_2_PRODUKT SET DESCRIPTION =  unistr('\00DC') || '/SFF 300/50 Regio inkl. Komf-An (08/16)' where  LB_ID = 17  and LEISTUNG__NO = 92359 and PRODUCT_OE__NO = 3401;
UPDATE T_LB_2_PRODUKT SET DESCRIPTION =  unistr('\00DC') || '/Komfort-Anschluss (SFF300) (08/16)' where  LB_ID = 17  and LEISTUNG__NO = 92360 and PRODUCT_OE__NO = 3401;

UPDATE T_LB_2_PRODUKT SET DESCRIPTION =  unistr('\00DC') || '/Surf+Fon-Flat 25/5 (08/16)' where  LB_ID = 16  and LEISTUNG__NO = 92350 and PRODUCT_OE__NO = 3401;
UPDATE T_LB_2_PRODUKT SET DESCRIPTION =  unistr('\00DC') || '/Surf+Fon-Flat 25/5 Regio (08/16)' where  LB_ID = 16  and LEISTUNG__NO = 92351 and PRODUCT_OE__NO = 3401;
UPDATE T_LB_2_PRODUKT SET DESCRIPTION =  unistr('\00DC') || '/Surf+Fon-Flat 50/10 (08/16)' where  LB_ID = 16  and LEISTUNG__NO = 92352 and PRODUCT_OE__NO = 3401;
UPDATE T_LB_2_PRODUKT SET DESCRIPTION =  unistr('\00DC') || '/Surf+Fon-Flat 50/10 Regio (08/16)' where  LB_ID = 16  and LEISTUNG__NO = 92353 and PRODUCT_OE__NO = 3401;
UPDATE T_LB_2_PRODUKT SET DESCRIPTION =  unistr('\00DC') || '/Surf+Fon-Flat 100/40 (08/16)' where  LB_ID = 16  and LEISTUNG__NO = 92354 and PRODUCT_OE__NO = 3401;
UPDATE T_LB_2_PRODUKT SET DESCRIPTION =  unistr('\00DC') || '/Surf+Fon-Flat 100/40 Regio (08/16)' where  LB_ID = 16  and LEISTUNG__NO = 92355 and PRODUCT_OE__NO = 3401;
UPDATE T_LB_2_PRODUKT SET DESCRIPTION =  unistr('\00DC') || '/SFF 100 inkl. Speed-Up. 150/50 (08/16)' where  LB_ID = 16  and LEISTUNG__NO = 92356 and PRODUCT_OE__NO = 3401;
