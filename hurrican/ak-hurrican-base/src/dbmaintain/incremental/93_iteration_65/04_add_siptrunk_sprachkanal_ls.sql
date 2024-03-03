create or replace procedure add_techls_sprachkanaele (id IN NUMBER , name in VARCHAR2, extern_ls_no IN NUMBER, anz IN NUMBER)
IS
  BEGIN
    insert into T_TECH_LEISTUNG (ID, NAME, EXTERN_LEISTUNG__NO, TYP, LONG_VALUE,
                                 PROD_NAME_STR, DESCRIPTION,
                                 GUELTIG_VON, GUELTIG_BIS)
      VALUES (id, name, extern_ls_no, 'SPRACHKANAELE', anz,
              ' ', concat (anz, ' Sprachkanäle für Produkt SIP-Trunk'),
              to_date('01.11.2013', 'dd.mm.yyyy'), to_date('01.01.2200', 'dd.mm.yyyy'))
    ;
  END;
/

call add_techls_sprachkanaele(500,   '2_SPRACHKANAELE', 31100,   2);
call add_techls_sprachkanaele(501,   '4_SPRACHKANAELE', 31101,   4);
call add_techls_sprachkanaele(502,   '6_SPRACHKANAELE', 31102,   6);
call add_techls_sprachkanaele(503,   '8_SPRACHKANAELE', 31103,   8);
call add_techls_sprachkanaele(504,  '10_SPRACHKANAELE', 31104,  10);
call add_techls_sprachkanaele(505,  '15_SPRACHKANAELE', 31105,  15);
call add_techls_sprachkanaele(506,  '20_SPRACHKANAELE', 31106,  20);
call add_techls_sprachkanaele(507,  '25_SPRACHKANAELE', 31107,  25);
call add_techls_sprachkanaele(508,  '30_SPRACHKANAELE', 31108,  30);
call add_techls_sprachkanaele(509,  '40_SPRACHKANAELE', 31109,  40);
call add_techls_sprachkanaele(510,  '50_SPRACHKANAELE', 31110,  50);
call add_techls_sprachkanaele(511,  '60_SPRACHKANAELE', 31111,  60);
call add_techls_sprachkanaele(512,  '70_SPRACHKANAELE', 31112,  70);
call add_techls_sprachkanaele(513,  '80_SPRACHKANAELE', 31113,  80);
call add_techls_sprachkanaele(514,  '90_SPRACHKANAELE', 31114,  90);
call add_techls_sprachkanaele(515, '120_SPRACHKANAELE', 31115, 120);
call add_techls_sprachkanaele(516, '130_SPRACHKANAELE', 31116, 130);
call add_techls_sprachkanaele(517, '150_SPRACHKANAELE', 31117, 150);
call add_techls_sprachkanaele(518, '180_SPRACHKANAELE', 31118, 180);
call add_techls_sprachkanaele(519, '210_SPRACHKANAELE', 31119, 210);
call add_techls_sprachkanaele(520, '240_SPRACHKANAELE', 31120, 240);
call add_techls_sprachkanaele(521, '270_SPRACHKANAELE', 31121, 270);
call add_techls_sprachkanaele(522, '300_SPRACHKANAELE', 31122, 300);

drop procedure add_techls_sprachkanaele;

create or replace procedure sprachkanaele_2_prod
IS
  BEGIN
    FOR techLsId IN 500..522 LOOP
      insert into T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT)
        values (S_T_PROD_2_TECH_LEISTUNG_0.nextval, 70, techLsId, 0, 0)
      ;
    END LOOP;
  END;
/

call sprachkanaele_2_prod();
drop procedure sprachkanaele_2_prod;
