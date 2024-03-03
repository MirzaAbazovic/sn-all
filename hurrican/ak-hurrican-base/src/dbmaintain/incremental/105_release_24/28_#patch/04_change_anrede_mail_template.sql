-- update Text fuer AENDERUNGSKENNZEICHEN=STANDARD und MONTAGE=YES
UPDATE T_SMS_CONFIG
SET
  EMAIL_TEXT    = 'Sehr geehrte Kundin, sehr geehrter Kunde,

damit Sie Ihren Anschluss @produkt@ möglichst bald nutzen können, haben wir einen Termin zur Schaltung der Leitung für Sie reserviert. Der Service-Techniker kommt am @schalttermin@ zwischen @zeitfenster@ zu Ihnen. Bitte stellen Sie sicher, dass Sie persönlich oder eine volljährige Person Ihres Vertrauens in dieser Zeit vor Ort sind. Der Techniker benötigt Zugang zur Anschlusstechnik. Diese befindet sich meist im Keller Ihres Gebäudes. Denken Sie daran, dass er Ihre Umgebung nicht kennt. Namens- oder Hinweisschilder helfen oft, damit alles schnell und reibungslos verläuft. Vielen Dank.

Viele Grüße

Ihr M-net Kundenservice'
  ,
  EMAIL_BETREFF = 'Terminankündigung'
WHERE MONTAGE = 'YES' AND AENDERUNGSKENNZEICHEN = 'STANDARD';

-- update Text fuer AENDERUNGSKENNZEICHEN=STANDARD und MONTAGE=NO
UPDATE T_SMS_CONFIG
SET
  EMAIL_TEXT    = 'Sehr geehrte Kundin, sehr geehrter Kunde,

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
  EMAIL_TEXT    = 'Sehr geehrte Kundin, sehr geehrter Kunde,

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
  EMAIL_TEXT    = 'Sehr geehrte Kundin, sehr geehrter Kunde,

damit Sie Ihren Anschluss @produkt@ möglichst bald nutzen können, haben wir einen neuen Termin zur Schaltung der Leitung für Sie reserviert. Der Service-Techniker kommt am @schalttermin@ zwischen @zeitfenster@ zu Ihnen. Bitte stellen Sie sicher, dass Sie persönlich oder eine volljährige Person Ihres Vertrauens in dieser Zeit vor Ort sind. Der Techniker benötigt Zugang zur Anschlusstechnik. Diese befindet sich meist im Keller Ihres Gebäudes. Denken Sie daran, dass er Ihre Umgebung nicht kennt. Namens- oder Hinweisschilder helfen oft, damit alles schnell und reibungslos verläuft. Vielen Dank.

Viele Grüße

Ihr M-net Kundenservice'
  ,
  EMAIL_BETREFF = 'Terminverschiebung'
WHERE MONTAGE = 'YES' AND AENDERUNGSKENNZEICHEN = 'TERMINVERSCHIEBUNG';

-- update Texte fuer AENDERUNGSKENNZEICHEN=TERMINVERSCHIEBUNG und MONTAGE=NO
UPDATE T_SMS_CONFIG
SET
  EMAIL_TEXT    = 'Sehr geehrte Kundin, sehr geehrter Kunde,

damit Sie Ihren Anschluss @produkt@ möglichst bald nutzen können, haben wir einen neuen Termin zur Schaltung der Leitung für Sie reserviert:
am @schalttermin@ zwischen @zeitfenster@
Sie müssen zu dieser Zeit nicht vor Ort sein.

Viele Grüße

Ihr M-net Kundenservice'
  ,
  EMAIL_BETREFF = 'Terminverschiebung'
WHERE MONTAGE = 'NO' AND AENDERUNGSKENNZEICHEN = 'TERMINVERSCHIEBUNG';