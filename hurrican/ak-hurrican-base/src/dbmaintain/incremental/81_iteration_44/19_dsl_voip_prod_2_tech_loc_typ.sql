-- Produkt_2_Tech_Location_Typ (analog zu Produkt 421)
CREATE OR REPLACE PROCEDURE create_p2tecloctype_dslvoip
IS
   BEGIN
    FOR p2tl IN (select p.TECH_LOCATION_TYPE_REF_ID, p.PRIORITY from T_PRODUKT_2_TECH_LOCATION_TYPE p where p.PROD_ID = 421) LOOP
      INSERT INTO T_PRODUKT_2_TECH_LOCATION_TYPE 
       (ID, PROD_ID, TECH_LOCATION_TYPE_REF_ID, PRIORITY, USERW, DATEW, VERSION)
       VALUES
	   (S_T_PRODUKT_2_TECH_LOC_TYPE_0.nextVal, 480, p2tl.TECH_LOCATION_TYPE_REF_ID, p2tl.PRIORITY, 'IMPORT', SYSDATE, 0)
	  ;
    END LOOP;
   END;
/  

call create_p2tecloctype_dslvoip();

DROP PROCEDURE create_p2tecloctype_dslvoip;
