-- Baugruppe anlegen
Insert into T_HW_BAUGRUPPEN_TYP (ID, NAME, PORT_COUNT, DESCRIPTION, IS_ACTIVE, HW_SCHNITTSTELLE_NAME, HW_TYPE_NAME, HVT_TECHNIK_ID, VERSION, MAX_BANDWIDTH_DOWNSTREAM, MAX_BANDWIDTH_UPSTREAM, BONDING_CAPABLE) Values
   (S_T_HW_BAUGRUPPEN_TYP_0.nextval, 'H83DFDSH', 24, '24-Port G.fast-Karte für Huawei DPU MA5818', '1', 'GFAST', 'DPU', 2, 0, 150000, 100000, '0');


-- DSLAM Profile anlegen
CREATE OR REPLACE PROCEDURE clone_dslam_profile
IS
  id_src NUMBER(19,0);
  id_dst NUMBER(19,0);
   BEGIN
    SELECT hw.id INTO id_dst FROM T_HW_BAUGRUPPEN_TYP hw WHERE hw.NAME = 'H83DFDSH';
    SELECT hw.id INTO id_src FROM T_HW_BAUGRUPPEN_TYP hw WHERE hw.NAME = 'MA5811S-DE16';

    FOR p IN (SELECT PARAMETER_NAME, PARAMETER_VALUE, IS_DEFAULT FROM T_PROFILE_PARAMETER WHERE hw_baugruppen_typ_id = id_src) LOOP

      Insert into T_PROFILE_PARAMETER (ID, PARAMETER_NAME, PARAMETER_VALUE, IS_DEFAULT, VERSION, HW_BAUGRUPPEN_TYP_ID)
      VALUES
        (S_T_PROFILE_PARAMETER_0.nextVal, p.PARAMETER_NAME, p.PARAMETER_VALUE, p.IS_DEFAULT, 0, id_dst )
    ;
      END LOOP;
  END;
/

call clone_dslam_profile();
drop procedure clone_dslam_profile;


-- DSLAM-Profile löschen
CREATE OR REPLACE PROCEDURE delete_dslam_profile
IS
  id_1 NUMBER(19,0);
  id_2 NUMBER(19,0);
   BEGIN
    SELECT hw.id INTO id_1 FROM T_HW_BAUGRUPPEN_TYP hw WHERE hw.NAME = 'MA5811S-AE08';
    SELECT hw.id INTO id_2 FROM T_HW_BAUGRUPPEN_TYP hw WHERE hw.NAME = 'MA5811S-DE16';

    FOR p IN (SELECT id FROM T_DSLAM_PROFILE WHERE baugruppen_typ = id_1) LOOP
      delete from t_PROD_2_DSLAMPROFILE where DSLAM_PROFILE_ID = p.id;
    END LOOP;

    FOR p IN (SELECT id FROM T_DSLAM_PROFILE WHERE baugruppen_typ = id_2) LOOP
      delete from t_PROD_2_DSLAMPROFILE where DSLAM_PROFILE_ID = p.id;
    END LOOP;
  END;
/

call delete_dslam_profile();
drop procedure delete_dslam_profile;

delete from T_DSLAM_PROFILE where BAUGRUPPEN_TYP = (select id from T_HW_BAUGRUPPEN_TYP where name = 'MA5811S-DE16');
delete from T_DSLAM_PROFILE where BAUGRUPPEN_TYP = (select id from T_HW_BAUGRUPPEN_TYP where name = 'MA5811S-AE08');
