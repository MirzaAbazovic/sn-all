-- update Text fuer AENDERUNGSKENNZEICHEN=STANDARD und MONTAGE=YES
UPDATE T_SMS_CONFIG
SET
  EMAIL_TEXT = 'Sehr geehrte(r) @vorname@ @name@,

damit Sie Ihren Anschluss @produkt@ m�glichst bald nutzen k�nnen, haben wir einen Termin zur Schaltung der Leitung f�r Sie reserviert. Der Service-Techniker kommt am @schalttermin@ @zeitfenster@ zu Ihnen. Bitte stellen Sie sicher, dass Sie pers�nlich oder eine vollj�hrige Person Ihres Vertrauens in dieser Zeit vor Ort sind. Der Techniker ben�tigt Zugang zur Anschlusstechnik. Diese befindet sich meist im Keller Ihres Geb�udes. Denken Sie daran, dass er Ihre Umgebung nicht kennt. Namens- oder Hinweisschilder helfen oft, damit alles schnell und reibungslos verl�uft. Vielen Dank.

Viele Gr��e

Ihr M-net Kundenservice'

WHERE MONTAGE = 'YES' AND AENDERUNGSKENNZEICHEN = 'STANDARD';

-- update Text fuer AENDERUNGSKENNZEICHEN=STANDARD und MONTAGE=NO
UPDATE T_SMS_CONFIG
SET
  EMAIL_TEXT = 'Sehr geehrte(r) @vorname@ @name@,

damit Sie Ihren Anschluss @produkt@ m�glichst bald nutzen k�nnen, haben wir einen Termin zur Schaltung der Leitung f�r Sie reserviert:
am @schalttermin@ @zeitfenster@.
Sie m�ssen zu dieser Zeit nicht vor Ort sein.

Viele Gr��e

Ihr M-net Kundenservice'
WHERE MONTAGE = 'NO' AND AENDERUNGSKENNZEICHEN = 'STANDARD';

-- update Texte fuer AENDERUNGSKENNZEICHEN=TERMINVERSCHIEBUNG und MONTAGE=YES
UPDATE T_SMS_CONFIG
SET
  EMAIL_TEXT = 'Sehr geehrte(r) @vorname@ @name@,

damit Sie Ihren Anschluss @produkt@ m�glichst bald nutzen k�nnen, haben wir einen neuen Termin zur Schaltung der Leitung f�r Sie reserviert. Der Service-Techniker kommt am @schalttermin@ @zeitfenster@ zu Ihnen. Bitte stellen Sie sicher, dass Sie pers�nlich oder eine vollj�hrige Person Ihres Vertrauens in dieser Zeit vor Ort sind. Der Techniker ben�tigt Zugang zur Anschlusstechnik. Diese befindet sich meist im Keller Ihres Geb�udes. Denken Sie daran, dass er Ihre Umgebung nicht kennt. Namens- oder Hinweisschilder helfen oft, damit alles schnell und reibungslos verl�uft. Vielen Dank.

Viele Gr��e

Ihr M-net Kundenservice'
WHERE MONTAGE = 'YES' AND AENDERUNGSKENNZEICHEN = 'TERMINVERSCHIEBUNG';

-- update Texte fuer AENDERUNGSKENNZEICHEN=TERMINVERSCHIEBUNG und MONTAGE=NO
UPDATE T_SMS_CONFIG
SET
  EMAIL_TEXT = 'Sehr geehrte(r) @vorname@ @name@,

damit Sie Ihren Anschluss @produkt@ m�glichst bald nutzen k�nnen, haben wir einen neuen Termin zur Schaltung der Leitung f�r Sie reserviert:
am @schalttermin@ @zeitfenster@
Sie m�ssen zu dieser Zeit nicht vor Ort sein.

Viele Gr��e

Ihr M-net Kundenservice'
WHERE MONTAGE = 'NO' AND AENDERUNGSKENNZEICHEN = 'TERMINVERSCHIEBUNG';

-- add entry to T_REGISTRY for email from address for customers concerning appointments
INSERT INTO T_REGISTRY
(ID, NAME, STR_VALUE, DESCRIPTION)
VALUES (81, 'customer.email.from', 'noreply-auftragsstatus@m-net.de',
        'Email-Absende-Addresse für Emailversand an die Kunden bei WITA/WBCI-Rueckmeldungen zusaetzlich zum SMS-Versand');
