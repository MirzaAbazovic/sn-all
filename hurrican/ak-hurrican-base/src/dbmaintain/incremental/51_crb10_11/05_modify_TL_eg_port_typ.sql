-- Typ der technischen Leistung 'Endgeräteport' von 'VOIP' auf 'VOIP_ADD' ändern, da 'VOIP' ein Unique Typ ist
UPDATE T_TECH_LEISTUNG SET TYP = 'VOIP_ADD' WHERE ID = 299;
