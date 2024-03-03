-- DN Leistungen fuer SIP InterTrunk Endkunden Produkt definieren
-- (da keine Leistungen definiert werden muessen, wird ein leeres Buendel angelegt!)

Insert into T_LEISTUNGSBUENDEL
   (ID, NAME, BESCHREIBUNG, VERSION)
 Values
   (15, 'SIP InterTrunk End.', 'Leistungsbündel für SIP InterTrunk Endkunden', 0);

-- Konfiguration von T_LB_2_PRODUKT muss noch offen bleiben, da Taifun Leistungen noch nicht bekannt!