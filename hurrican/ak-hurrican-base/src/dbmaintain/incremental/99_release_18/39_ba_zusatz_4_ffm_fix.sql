-- Datensaetze des vorherigen skripts von Fieldservice auf FFM aendern
UPDATE T_BA_ZUSATZ set ABT_ID = (select id from T_ABTEILUNG WHERE text = 'FFM')
  where USERW = 'dbmaintain'
  and GUELTIG_BIS = TO_DATE('01/01/2200', 'DD/MM/YYYY')
  and BA_VERL_CONFIG_ID  IN (select b.id from T_BA_VERL_CONFIG b
                            where b.PROD_ID in (select distinct p.PROD_ID from T_PRODUKT_2_PHYSIKTYP p
                            where p.PHYSIKTYP in (600, 700, 800, 801, 803, 805, 806, 807, 808, 809))
                            and b.ANLASS in (27, 71, 72) and b.GUELTIG_BIS > sysdate) 
  and STANDORT_TYP_REF_ID IN (11002, 11011, 11017)
  and AUCH_SM = 0
  and abt_id = 5
;
