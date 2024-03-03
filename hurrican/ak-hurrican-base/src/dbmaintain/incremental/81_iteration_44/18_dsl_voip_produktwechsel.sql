-- Produktwechsel

--src to dest
CREATE OR REPLACE PROCEDURE create_p2p_dslvoip_src_2_dest
IS
   BEGIN
    FOR d IN 
    (select p.PROD_DEST, p.PHYSIKAEND_TYP, p.CHAIN_ID, p.DESCRIPTION from T_PROD_2_PROD p where p.PROD_SRC = 421 and p.PROD_DEST >= 400) LOOP
      INSERT INTO T_PROD_2_PROD
   		(ID, PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID, DESCRIPTION, VERSION)
       VALUES
	    (S_T_PROD_2_PROD_0.nextVal, 480, d.PROD_DEST, d.PHYSIKAEND_TYP, d.CHAIN_ID, d.DESCRIPTION, 0)
	  ;
    END LOOP;
   END;
/  

-- dest to src
CREATE OR REPLACE PROCEDURE create_p2p_dslvoip_dest_to_src
IS
   BEGIN
    FOR d IN 
    (select p.PROD_SRC, p.PHYSIKAEND_TYP, p.CHAIN_ID, p.DESCRIPTION from T_PROD_2_PROD p where p.PROD_DEST = 421 and p.PROD_SRC >= 400) LOOP
      INSERT INTO T_PROD_2_PROD
   		(ID, PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID, DESCRIPTION, VERSION)
       VALUES
	    (S_T_PROD_2_PROD_0.nextVal, d.PROD_SRC, 480, d.PHYSIKAEND_TYP, d.CHAIN_ID, d.DESCRIPTION, 0)
	  ;
    END LOOP;
   END;
/  

call create_p2p_dslvoip_src_2_dest();
call create_p2p_dslvoip_dest_to_src();

-- wechsel zu/von Produkt 440 -> Uebernahme statt Kreuzung
UPDATE T_PROD_2_PROD set PHYSIKAEND_TYP = 5000 
	WHERE (PROD_SRC = 480 AND PROD_DEST = 440)
	OR (PROD_SRC = 440 AND PROD_DEST = 480)
;

DROP PROCEDURE create_p2p_dslvoip_src_2_dest;
DROP PROCEDURE create_p2p_dslvoip_dest_to_src;