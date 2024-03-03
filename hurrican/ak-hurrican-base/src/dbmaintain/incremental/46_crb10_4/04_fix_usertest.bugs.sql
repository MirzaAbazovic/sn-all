-- Die Spalte muss groesser sein, damit die Flexagon-Testusernamen Platz haben
ALTER TABLE T_GEO_ID_2_TECH_LOCATION MODIFY(USERW VARCHAR2(50 BYTE));

-- Die Spalte muss nullable sein, da bei N01 das Uebertragungsverfahren leer bleiben muss
ALTER TABLE T_MWF_SCHALTUNG_KUPFER MODIFY(UEBERTRAGUNGSVERFAHREN NULL);