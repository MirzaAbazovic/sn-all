-- Reports
CREATE OR REPLACE PROCEDURE create_prod_2_report_dsl_voip
IS
  id NUMBER(19,0);
  BEGIN
    FOR r_2_p IN (SELECT r2p.ID, r2p.REP_ID FROM T_REPORT_2_PROD r2p WHERE r2p.PROD_ID = 421) LOOP

      SELECT S_T_REPORT_2_PROD_0.nextVal INTO id FROM DUAL;
      INSERT INTO T_REPORT_2_PROD 
        (ID, REP_ID, PROD_ID, VERSION)
         VALUES 
         (id, r_2_p.REP_ID, 480, 0)
      ;
      FOR r2p_s IN (SELECT STATUS_ID FROM T_REP2PROD_STATI WHERE REP2PROD_ID = r_2_p.ID) LOOP
        INSERT INTO T_REP2PROD_STATI 
          (ID, REP2PROD_ID, STATUS_ID, VERSION)
           VALUES
             (S_T_REP2PROD_STATI_0.nextVal, id, r2p_s.STATUS_ID, 0)
           ;
      END LOOP;
    END LOOP;
  END;
/

call create_prod_2_report_dsl_voip();

DROP PROCEDURE create_prod_2_report_dsl_voip;