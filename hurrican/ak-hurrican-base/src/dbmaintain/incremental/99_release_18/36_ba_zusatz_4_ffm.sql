-- FFM Abteilung fuer alle Produkte welche ueber FTTB/H realisiert werden koennen
-- BA-Anlaesse "Neuschaltung" und "Anbieterwechsel"

-- FTTB
INSERT INTO T_BA_ZUSATZ (ID, ABT_ID, BA_VERL_CONFIG_ID, STANDORT_TYP_REF_ID, AUCH_SM, GUELTIG_VON, GUELTIG_BIS, USERW, DATEW, VERSION)
    SELECT S_T_BA_ZUSATZ_0.NEXTVAL, 5, b.id, 11002, 0, TRUNC(SYSDATE), TO_DATE('01/01/2200', 'DD/MM/YYYY'), 'dbmaintain', SYSDATE, 0
        from T_BA_VERL_CONFIG b
    	where b.PROD_ID in (select distinct p.PROD_ID from T_PRODUKT_2_PHYSIKTYP p
    	                        where p.PHYSIKTYP in (600, 700, 800, 801, 803, 805, 806, 807, 808, 809))
    	and b.ANLASS in (27, 71, 72) and b.GUELTIG_BIS > sysdate;

-- FTTH
INSERT INTO T_BA_ZUSATZ (ID, ABT_ID, BA_VERL_CONFIG_ID, STANDORT_TYP_REF_ID, AUCH_SM, GUELTIG_VON, GUELTIG_BIS, USERW, DATEW, VERSION)
    SELECT S_T_BA_ZUSATZ_0.NEXTVAL, 5, b.id, 11011, 0, TRUNC(SYSDATE), TO_DATE('01/01/2200', 'DD/MM/YYYY'), 'dbmaintain', SYSDATE, 0
        from T_BA_VERL_CONFIG b
    	where b.PROD_ID in (select distinct p.PROD_ID from T_PRODUKT_2_PHYSIKTYP p
    	                        where p.PHYSIKTYP in (600, 700, 800, 801, 803, 805, 806, 807, 808, 809))
    	and b.ANLASS in (27, 71, 72) and b.GUELTIG_BIS > sysdate;

-- FTTB_H
INSERT INTO T_BA_ZUSATZ (ID, ABT_ID, BA_VERL_CONFIG_ID, STANDORT_TYP_REF_ID, AUCH_SM, GUELTIG_VON, GUELTIG_BIS, USERW, DATEW, VERSION)
    SELECT S_T_BA_ZUSATZ_0.NEXTVAL, 5, b.id, 11017, 0, TRUNC(SYSDATE), TO_DATE('01/01/2200', 'DD/MM/YYYY'), 'dbmaintain', SYSDATE, 0
        from T_BA_VERL_CONFIG b
    	where b.PROD_ID in (select distinct p.PROD_ID from T_PRODUKT_2_PHYSIKTYP p
    	                        where p.PHYSIKTYP in (600, 700, 800, 801, 803, 805, 806, 807, 808, 809))
    	and b.ANLASS in (27, 71, 72) and b.GUELTIG_BIS > sysdate;
