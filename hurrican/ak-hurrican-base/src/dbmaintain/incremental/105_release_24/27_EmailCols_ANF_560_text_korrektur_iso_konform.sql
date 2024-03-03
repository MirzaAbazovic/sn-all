-- update Text fuer AENDERUNGSKENNZEICHEN=STANDARD und MONTAGE=YES
UPDATE T_SMS_CONFIG
SET
  EMAIL_TEXT = 'Sehr geehrte(r) @vorname@ @name@,

damit Sie Ihren Anschluss @produkt@ möglichst bald nutzen können, haben wir einen Termin zur Schaltung der Leitung für Sie reserviert. Der Service-Techniker kommt am @schalttermin@ @zeitfenster@ zu Ihnen. Bitte stellen Sie sicher, dass Sie persönlich oder eine volljährige Person Ihres Vertrauens in dieser Zeit vor Ort sind. Der Techniker benötigt Zugang zur Anschlusstechnik. Diese befindet sich meist im Keller Ihres Gebäudes. Denken Sie daran, dass er Ihre Umgebung nicht kennt. Namens- oder Hinweisschilder helfen oft, damit alles schnell und reibungslos verläuft. Vielen Dank.

Viele Grüße

Ihr M-net Kundenservice'

WHERE MONTAGE = 'YES' AND AENDERUNGSKENNZEICHEN = 'STANDARD';

-- update Text fuer AENDERUNGSKENNZEICHEN=STANDARD und MONTAGE=NO
UPDATE T_SMS_CONFIG
SET
  EMAIL_TEXT = 'Sehr geehrte(r) @vorname@ @name@,

damit Sie Ihren Anschluss @produkt@ möglichst bald nutzen können, haben wir einen Termin zur Schaltung der Leitung für Sie reserviert:
am @schalttermin@ @zeitfenster@.
Sie müssen zu dieser Zeit nicht vor Ort sein.

Viele Grüße

Ihr M-net Kundenservice'
WHERE MONTAGE = 'NO' AND AENDERUNGSKENNZEICHEN = 'STANDARD';

-- update Texte fuer AENDERUNGSKENNZEICHEN=TERMINVERSCHIEBUNG und MONTAGE=YES
UPDATE T_SMS_CONFIG
SET
  EMAIL_TEXT = 'Sehr geehrte(r) @vorname@ @name@,

damit Sie Ihren Anschluss @produkt@ möglichst bald nutzen können, haben wir einen neuen Termin zur Schaltung der Leitung für Sie reserviert. Der Service-Techniker kommt am @schalttermin@ @zeitfenster@ zu Ihnen. Bitte stellen Sie sicher, dass Sie persönlich oder eine volljährige Person Ihres Vertrauens in dieser Zeit vor Ort sind. Der Techniker benötigt Zugang zur Anschlusstechnik. Diese befindet sich meist im Keller Ihres Gebäudes. Denken Sie daran, dass er Ihre Umgebung nicht kennt. Namens- oder Hinweisschilder helfen oft, damit alles schnell und reibungslos verläuft. Vielen Dank.

Viele Grüße

Ihr M-net Kundenservice'
WHERE MONTAGE = 'YES' AND AENDERUNGSKENNZEICHEN = 'TERMINVERSCHIEBUNG';

-- update Texte fuer AENDERUNGSKENNZEICHEN=TERMINVERSCHIEBUNG und MONTAGE=NO
UPDATE T_SMS_CONFIG
SET
  EMAIL_TEXT = 'Sehr geehrte(r) @vorname@ @name@,

damit Sie Ihren Anschluss @produkt@ möglichst bald nutzen können, haben wir einen neuen Termin zur Schaltung der Leitung für Sie reserviert:
am @schalttermin@ @zeitfenster@
Sie müssen zu dieser Zeit nicht vor Ort sein.

Viele Grüße

Ihr M-net Kundenservice'
WHERE MONTAGE = 'NO' AND AENDERUNGSKENNZEICHEN = 'TERMINVERSCHIEBUNG';

UPDATE T_REGISTRY
SET DESCRIPTION = 'Email-Absende-Addresse für Emailversand an die Kunden bei WITA/WBCI-Rueckmeldungen zusätzlich zum SMS-Versand'
WHERE ID = 81;
