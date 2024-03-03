-- Erstelle Tabelle fuer die Zuordnung von Endgeraeteports zu VOIPnummern.
CREATE TABLE T_AUFTRAG_VOIP_DN_2_EG_PORT
(
   AUFTRAG_VOIP_DN_ID   NUMBER (19, 0) NOT NULL,
   constraint           FK_AUFTRAG_VOIP_DN
   foreign key          (AUFTRAG_VOIP_DN_ID)
   references           T_AUFTRAG_VOIP_DN(ID),
   EG_PORT_ID           NUMBER (19, 0) NOT NULL,
   constraint           FK_EG_PORT
   foreign key          (EG_PORT_ID)
   references           T_EG_PORT(ID),
   PRIMARY KEY          (AUFTRAG_VOIP_DN_ID, EG_PORT_ID)
);

-- Zugriff auf die Tabelle regeln
GRANT SELECT, INSERT, UPDATE ON T_AUFTRAG_VOIP_DN_2_EG_PORT TO R_HURRICAN_USER;
GRANT SELECT ON T_AUFTRAG_VOIP_DN_2_EG_PORT TO R_HURRICAN_READ_ONLY;
