ALTER TABLE T_AUFTRAG_TECHNIK
 ADD (PROJECT_RESPONSIBLE  NUMBER(10));

COMMENT ON COLUMN T_AUFTRAG_TECHNIK.PROJECT_RESPONSIBLE IS 'ID des Hurrican Users, der Hauptprojektverantwortlicher fuer den Auftrag ist';
