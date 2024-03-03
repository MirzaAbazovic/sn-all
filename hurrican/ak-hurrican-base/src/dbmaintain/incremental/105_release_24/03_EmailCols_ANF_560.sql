-- T_SMS_CONFIG um eine Spalte fuer den Emailtext und eine Spalte uer erweitern
ALTER TABLE T_SMS_CONFIG
  ADD
  (
  EMAIL_TEXT VARCHAR2(1024),
  EMAIL_BETREFF VARCHAR2(255)
  );
COMMENT ON COLUMN T_SMS_CONFIG.EMAIL_TEXT IS 'Diese Spalte haelt zusaetzliche Email-Text-Templates vor';
COMMENT ON COLUMN T_SMS_CONFIG.EMAIL_BETREFF IS 'Diese Spalte haelt den Betrefftext zu einer Email vor';

-- update Text fuer AENDERUNGSKENNZEICHEN=STANDARD und MONTAGE=YES
UPDATE T_SMS_CONFIG
SET
  EMAIL_TEXT    = 'Sehr geehrte(r) @vorname@ @name@,

damit Sie Ihren Anschluss @produkt@ möglichst bald nutzen können, haben wir einen Termin zur Schaltung der Leitung für Sie reserviert. Der Service-Techniker kommt am @schalttermin@ zwischen @zeitfenster@ zu Ihnen. Bitte stellen Sie sicher, dass Sie persönlich oder eine volljährige Person Ihres Vertrauens in dieser Zeit vor Ort sind. Der Techniker benötigt Zugang zur Anschlusstechnik. Diese befindet sich meist im Keller Ihres Gebäudes. Denken Sie daran, dass er Ihre Umgebung nicht kennt. Namens- oder Hinweisschilder helfen oft, damit alles schnell und reibungslos verläuft. Vielen Dank.

Viele Grüße

Ihr M-net Kundenservice'
  ,
  EMAIL_BETREFF = 'Terminankündigung'
WHERE MONTAGE = 'YES' AND AENDERUNGSKENNZEICHEN = 'STANDARD';

-- update Text fuer AENDERUNGSKENNZEICHEN=STANDARD und MONTAGE=NO
UPDATE T_SMS_CONFIG
SET
  EMAIL_TEXT    = 'Sehr geehrte(r) @vorname@ @name@,

damit Sie Ihren Anschluss @produkt@ möglichst bald nutzen können, haben wir einen Termin zur Schaltung der Leitung für Sie reserviert:
am @schalttermin@ zwischen @zeitfenster@.
Sie müssen zu dieser Zeit nicht vor Ort sein

Viele Grüße

Ihr M-net Kundenservice'
  ,
  EMAIL_BETREFF = 'Terminankündigung'
WHERE MONTAGE = 'NO' AND AENDERUNGSKENNZEICHEN = 'STANDARD';

-- update Text fuer AENDERUNGSKENNZEICHEN=STANDARD und MONTAGE=IGNORE
UPDATE T_SMS_CONFIG
SET
  EMAIL_TEXT    = 'Sehr geehrte(r) @vorname@ @name@,

leider konnte Ihr Anschluss @produkt@ nicht geschaltet werden.
Bitte rufen Sie uns an, um das weitere Vorgehen gemeinsam zu besprechen.
Unter 0800 7080810 sind wir Montag bis Freitag in der Zeit von 08:00 bis 20:00 Uhr sowie Samstag in der Zeit von 09:00 bis 15:00 Uhr für Sie da.

Viele Grüße

Ihr M-net Kundenservice'
  ,
  EMAIL_BETREFF = 'Bitte rufen Sie uns an'
WHERE MONTAGE = 'IGNORE' AND AENDERUNGSKENNZEICHEN = 'STANDARD';

-- update Texte fuer AENDERUNGSKENNZEICHEN=TERMINVERSCHIEBUNG und MONTAGE=YES
UPDATE T_SMS_CONFIG
SET
  EMAIL_TEXT    = 'Sehr geehrte(r) @vorname@ @name@,

damit Sie Ihren Anschluss @produkt@ möglichst bald nutzen können, haben wir einen neuen Termin zur Schaltung der Leitung für Sie reserviert. Der Service-Techniker kommt am @schalttermin@ zwischen @zeitfenster@ zu Ihnen. Bitte stellen Sie sicher, dass Sie persönlich oder eine volljährige Person Ihres Vertrauens in dieser Zeit vor Ort sind. Der Techniker benötigt Zugang zur Anschlusstechnik. Diese befindet sich meist im Keller Ihres Gebäudes. Denken Sie daran, dass er Ihre Umgebung nicht kennt. Namens- oder Hinweisschilder helfen oft, damit alles schnell und reibungslos verläuft. Vielen Dank.

Viele Grüße

Ihr M-net Kundenservice'
  ,
  EMAIL_BETREFF = 'Terminverschiebung'
WHERE MONTAGE = 'YES' AND AENDERUNGSKENNZEICHEN = 'TERMINVERSCHIEBUNG';

-- update Texte fuer AENDERUNGSKENNZEICHEN=TERMINVERSCHIEBUNG und MONTAGE=NO
UPDATE T_SMS_CONFIG
SET
  EMAIL_TEXT    = 'Sehr geehrte(r) @vorname@ @name@,

damit Sie Ihren Anschluss @produkt@ möglichst bald nutzen können, haben wir einen neuen Termin zur Schaltung der Leitung für Sie reserviert:
am @schalttermin@ zwischen @zeitfenster@
Sie müssen zu dieser Zeit nicht vor Ort sein.

Viele Grüße

Ihr M-net Kundenservice'
  ,
  EMAIL_BETREFF = 'Terminverschiebung'
WHERE MONTAGE = 'NO' AND AENDERUNGSKENNZEICHEN = 'TERMINVERSCHIEBUNG';

-- EMAIL_TEXT muss immer existieren
ALTER TABLE T_SMS_CONFIG
  MODIFY (EMAIL_TEXT VARCHAR2(1024) NOT NULL);
-- EMAIL_BETREFF muss immer existieren
ALTER TABLE T_SMS_CONFIG
  MODIFY (EMAIL_BETREFF VARCHAR2(255) NOT NULL);

-- T_MWF_MELDUNG um eine Spalte fuer den Emailstatus erweitern
ALTER TABLE T_MWF_MELDUNG
  ADD
  (
  EMAIL_STATUS VARCHAR2(25)
  );
COMMENT ON COLUMN T_MWF_MELDUNG.EMAIL_STATUS IS 'Diese Spalte repraesentiert den Email-Sende-Status zu einer Meldung. Der default ist OFFEN';

-- check constraint auf EMAIL_STATUS
ALTER TABLE T_MWF_MELDUNG
  ADD CONSTRAINT CHK_EMAIL_STATUS CHECK (
  EMAIL_STATUS IN
  ('OFFEN', 'GESENDET', 'KEINE_RN', 'VERALTET', 'UNGUELTIG', 'UNERWUENSCHT', 'KEINE_CONFIG', 'FALSCHER_AUFTRAGSTATUS'));

-- Initialbefuellung mit Status 'VERALTET'
UPDATE T_MWF_MELDUNG
SET EMAIL_STATUS = 'VERALTET';

-- EMAIL_STATUS muss in neuem Datensatz immer mit dem Default 'OFFEN' belegt werden
ALTER TABLE T_MWF_MELDUNG
  MODIFY (EMAIL_STATUS VARCHAR2(25 BYTE) DEFAULT 'OFFEN' NOT NULL);
