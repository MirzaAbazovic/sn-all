 -- Neues Rufnummern-Mapping Taifun - Hurrican für doppelten Upstream ANF-725
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
CALL create_lb2produkt(17,92358,3401,'Ü/SFF 300/50 inkl. Komf-Ansch. (08/16)');
CALL create_lb2produkt(17,92359,3401,'Ü/SFF 300/50 Regio inkl. Komf-An (08/16)');
CALL create_lb2produkt(17,92360,3401,'Ü/Komfort-Anschluss (SFF300) (08/16)');

-- Standard-Anschluss --> LB 16
CALL create_lb2produkt(16,92350,3401,'Ü/Surf+Fon-Flat 25/5 (08/16)');
CALL create_lb2produkt(16,92351,3401,'Ü/Surf+Fon-Flat 25/5 Regio (08/16)');
CALL create_lb2produkt(16,92352,3401,'Ü/Surf+Fon-Flat 50/10 (08/16)');
CALL create_lb2produkt(16,92353,3401,'Ü/Surf+Fon-Flat 50/10 Regio (08/16)');
CALL create_lb2produkt(16,92354,3401,'Ü/Surf+Fon-Flat 100/40 (08/16)');
CALL create_lb2produkt(16,92355,3401,'Ü/Surf+Fon-Flat 100/40 Regio (08/16)');
CALL create_lb2produkt(16,92356,3401,'Ü/SFF 100 inkl. Speed-Up. 150/50 (08/16)');

DROP PROCEDURE create_lb2produkt;
