ALTER TABLE T_MWF_MELDUNG
 ADD (GESCHAEFTSFALLTYP  VARCHAR2(40));

ALTER TABLE T_MWF_MELDUNG
 ADD (AENDERUNGSKENNZEICHEN  VARCHAR2(20));

UPDATE   T_MWF_MELDUNG M
   SET   M.GESCHAEFTSFALLTYP =
            (SELECT   AT.GESCHAEFTSFALLTYP
               FROM   T_MWF_MELDUNG_AUFTRAGSTYP AT
              WHERE   AT.ID = M.AUFTRAGSTYP_ID);

UPDATE   T_MWF_MELDUNG M
   SET   M.AENDERUNGSKENNZEICHEN =
            (SELECT   AT.AENDERUNGSKENNZEICHEN
               FROM   T_MWF_MELDUNG_AUFTRAGSTYP AT
              WHERE   AT.ID = M.AUFTRAGSTYP_ID);

ALTER TABLE T_MWF_MELDUNG
MODIFY(GESCHAEFTSFALLTYP  NOT NULL);


ALTER TABLE T_MWF_MELDUNG
MODIFY(AENDERUNGSKENNZEICHEN  NOT NULL);

ALTER TABLE T_MWF_MELDUNG DROP COLUMN AUFTRAGSTYP_ID;

DROP TABLE T_MWF_MELDUNG_AUFTRAGSTYP cascade constraints;