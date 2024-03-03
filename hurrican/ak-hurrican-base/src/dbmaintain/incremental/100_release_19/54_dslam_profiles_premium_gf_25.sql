create or REPLACE PROCEDURE dp_for_premium_gf_25(
  pname IN T_DSLAM_PROFILE.name%TYPE,
  upst_tls IN T_DSLAM_PROFILE.UPSTREAM_TECH_LS%TYPE,
  upstream IN T_DSLAM_PROFILE.UPSTREAM%TYPE)
IS
  BEGIN
    DECLARE
      dpid T_DSLAM_PROFILE.id%TYPE;
      cnt Number;
    BEGIN
      -- alle baugruppen betrachten die fuer MD_18000_... / Premium GF relevant sind
      FOR to_copy in (select * from T_DSLAM_PROFILE dp join T_PROD_2_DSLAMPROFILE p2dp on (dp.id = p2dp.DSLAM_PROFILE_ID) where p2dp.PROD_ID = 540 and dp.name LIKE  'MD_18000_%') LOOP
        DBMS_OUTPUT.PUT('benoetige Profil fuer bg-typ = ');
        DBMS_OUTPUT.PUT(to_copy.BAUGRUPPEN_TYP);
        DBMS_OUTPUT.PUT(' mit name ');
        DBMS_OUTPUT.PUT_LINE(pname);
        select count(*) into cnt from T_DSLAM_PROFILE dp
        where (dp.name = pname) and ((dp.BAUGRUPPEN_TYP = to_copy.BAUGRUPPEN_TYP) or (dp.BAUGRUPPEN_TYP is null and to_copy.BAUGRUPPEN_TYP is NULL));
        -- existiert bereits ein passendes profil?
        DBMS_OUTPUT.put('count = ');
        DBMS_OUTPUT.put_line(cnt);
        if(cnt > 0) then
          for inner_row in (select dp.id, dp.gueltig, dp.BAUGRUPPEN_TYP from T_DSLAM_PROFILE dp
          where (pname = dp.name) and ((dp.BAUGRUPPEN_TYP = to_copy.BAUGRUPPEN_TYP) or (dp.BAUGRUPPEN_TYP is null and to_copy.BAUGRUPPEN_TYP is NULL))) LOOP
            DBMS_OUTPUT.PUT('Profil mit BG-Typ ');
            DBMS_OUTPUT.PUT(to_copy.BAUGRUPPEN_TYP);
            DBMS_OUTPUT.PUT(' und Name ');
            DBMS_OUTPUT.PUT(pname);
            DBMS_OUTPUT.PUT_LINE('gefunden');
            -- profil muss reaktiviert werden?
            if inner_row.GUELTIG = '0' THEN
              DBMS_OUTPUT.PUT('bestehendes profil auf gueltig setzen: ');
              dbms_output.put_line(inner_row.id);
              update T_DSLAM_PROFILE set GUELTIG = '1' where id = inner_row.id;
            end if;
            -- auf Premium Glasfaser-DSL mappen
            DBMS_OUTPUT.PUT('bestehendes profil auf 540 mappen: ');
            dbms_output.put_line(inner_row.id);
            Insert into T_PROD_2_DSLAMPROFILE (PROD_ID, DSLAM_PROFILE_ID) Values (540, inner_row.id);
          end loop;
        else
          Select S_T_DSLAM_PROFILE_0.nextval into dpid from dual;

          -- neues Profil anlegen
          DBMS_OUTPUT.PUT('neues profil anlegen: bg_id = ');
          DBMS_OUTPUT.PUT(to_copy.BAUGRUPPEN_TYP);
          DBMS_OUTPUT.PUT(' name = ');
          DBMS_OUTPUT.PUT(pname);
          DBMS_OUTPUT.PUT(' id = ');
          DBMS_OUTPUT.PUT_LINE(dpid);

          Insert into T_DSLAM_PROFILE
          (ID, NAME, FASTPATH, DOWNSTREAM_TECH_LS, UPSTREAM_TECH_LS,
           GUELTIG, VERSION, ADSL1FORCE, BAUGRUPPEN_TYP, ENABLED_FOR_AUTOCHANGE,
           DOWNSTREAM, UPSTREAM)
          Values
            (dpid, pname, to_copy.FASTPATH, 23, upst_tls,
             '1', 0, to_copy.ADSL1FORCE, to_copy.BAUGRUPPEN_TYP, to_copy.ENABLED_FOR_AUTOCHANGE,
             25000, upstream);
          DBMS_OUTPUT.PUT('neues profil auf 540 mappen: ');
          dbms_output.put_line(to_copy.BAUGRUPPEN_TYP);
          Insert into T_PROD_2_DSLAMPROFILE (PROD_ID, DSLAM_PROFILE_ID) Values (540, dpid);
        end if;
      END LOOP;
    END;
  END;
/

call dp_for_premium_gf_25('MD_25000_2500_H', 114, 2500);
call dp_for_premium_gf_25('MD_25000_5000_H',  27, 5000);
drop PROCEDURE dp_for_premium_gf_25;
