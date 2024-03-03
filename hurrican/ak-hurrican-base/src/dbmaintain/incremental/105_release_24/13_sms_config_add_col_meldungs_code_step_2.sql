ALTER TABLE T_SMS_CONFIG ADD MELDUNGS_CODE varchar2(10) DEFAULT '0' NOT NULL ;
COMMENT ON COLUMN T_SMS_CONFIG.MELDUNGS_CODE IS 'Der Meldungscode gibt Auskunft ueber die genaue Ursache der Meldung. Im TA-FAll ist der Meldungscode 6012';
ALTER TABLE T_SMS_CONFIG ADD CONSTRAINT UK_SMS_CONFIG UNIQUE (SCHNITTSTELLE, GESCHAEFTSFALL_TYP, MELDUNG_TYP, MONTAGE, AENDERUNGSKENNZEICHEN, MELDUNGS_CODE);
COMMIT;

INSERT INTO T_SMS_CONFIG
(ID, SCHNITTSTELLE, GESCHAEFTSFALL_TYP, MELDUNG_TYP, MONTAGE, AENDERUNGSKENNZEICHEN, MELDUNGS_CODE, TEMPLATE_TEXT, EMAIL_TEXT, EMAIL_BETREFF )
VALUES
  (S_T_SMS_CONFIG_0.nextVal, 'WITA', 'BEREITSTELLUNG', 'TAM', 'IGNORE', 'STANDARD',
   '6012',
   'Ihr Anschluss konnte am @schalttermin@ nicht geschaltet werden, eine erneute Schaltung findet am Folgewerktag statt. Ihr M-net Team',
   'Ihr Anschluss konnte am @schalttermin@ nicht geschaltet werden, eine erneute Schaltung findet am Folgewerktag statt. Ihr M-net Team',
   'Schaltung am Folgewerktag');
INSERT INTO T_SMS_CONFIG
(ID, SCHNITTSTELLE, GESCHAEFTSFALL_TYP, MELDUNG_TYP, MONTAGE, AENDERUNGSKENNZEICHEN, MELDUNGS_CODE, TEMPLATE_TEXT, EMAIL_TEXT, EMAIL_BETREFF )
VALUES
  (S_T_SMS_CONFIG_0.nextVal, 'WITA', 'PROVIDERWECHSEL', 'TAM', 'IGNORE', 'STANDARD',
   '6012',
   'Ihr Anschluss konnte am @schalttermin@ nicht geschaltet werden, eine erneute Schaltung findet am Folgewerktag statt. Ihr M-net Team',
   'Ihr Anschluss konnte am @schalttermin@ nicht geschaltet werden, eine erneute Schaltung findet am Folgewerktag statt. Ihr M-net Team',
   'Schaltung am Folgewerktag');
INSERT INTO T_SMS_CONFIG
(ID, SCHNITTSTELLE, GESCHAEFTSFALL_TYP, MELDUNG_TYP, MONTAGE, AENDERUNGSKENNZEICHEN, MELDUNGS_CODE, TEMPLATE_TEXT, EMAIL_TEXT, EMAIL_BETREFF )
VALUES
  (S_T_SMS_CONFIG_0.nextVal, 'WITA', 'VERBUNDLEISTUNG', 'TAM', 'IGNORE', 'STANDARD',
   '6012',
   'Ihr Anschluss konnte am @schalttermin@ nicht geschaltet werden, eine erneute Schaltung findet am Folgewerktag statt. Ihr M-net Team',
   'Ihr Anschluss konnte am @schalttermin@ nicht geschaltet werden, eine erneute Schaltung findet am Folgewerktag statt. Ihr M-net Team',
   'Schaltung am Folgewerktag');