-- update Text fuer MELDUNG_TYP = 'ABM' und AENDERUNGSKENNZEICHEN=STANDARD und MONTAGE=YES            (ID = 1,2,3,4,5,6)
UPDATE T_SMS_CONFIG
SET
  EMAIL_TEXT = 'Sehr geehrte Kundin, sehr geehrter Kunde,

damit Sie Ihren Anschluss @produkt@ möglichst bald nutzen können, haben wir einen Termin zur Schaltung der Leitung für Sie reserviert. Der Service-Techniker kommt am @schalttermin@ @zeitfenster@ zu Ihnen. Bitte stellen Sie sicher, dass Sie persönlich oder eine volljährige Person Ihres Vertrauens in dieser Zeit vor Ort sind. Der Techniker benötigt Zugang zur Anschlusstechnik. Diese befindet sich meist im Keller Ihres Gebäudes. Denken Sie daran, dass er Ihre Umgebung nicht kennt. Namens- oder Hinweisschilder helfen oft, damit alles schnell und reibungslos verläuft. Vielen Dank.

Viele Grüße

Ihr M-net Kundenservice'

WHERE MELDUNG_TYP = 'ABM' AND MONTAGE = 'YES' AND AENDERUNGSKENNZEICHEN = 'STANDARD';

-- update Text fuer MELDUNG_TYP = 'ABM' und AENDERUNGSKENNZEICHEN=STANDARD und MONTAGE=NO    (ID = 7,8,9,10,11,12)
UPDATE T_SMS_CONFIG
SET
  EMAIL_TEXT = 'Sehr geehrte Kundin, sehr geehrter Kunde,

damit Sie Ihren Anschluss @produkt@ möglichst bald nutzen können, haben wir einen Termin zur Schaltung der Leitung für Sie reserviert:
am @schalttermin@ @zeitfenster@.
Sie müssen zu dieser Zeit nicht vor Ort sein.

Viele Grüße

Ihr M-net Kundenservice'
WHERE MELDUNG_TYP = 'ABM' AND MONTAGE = 'NO' AND AENDERUNGSKENNZEICHEN = 'STANDARD';

-- update Text fuer MELDUNG_TYP = 'ABM' und MONTAGE=YES und AENDERUNGSKENNZEICHEN=TERMINVERSCHIEBUNG        (ID = 13,14,15,16,17,18)
UPDATE T_SMS_CONFIG
SET
  EMAIL_TEXT    = 'Sehr geehrte Kundin, sehr geehrter Kunde,

damit Sie Ihren Anschluss @produkt@ möglichst bald nutzen können, haben wir einen neuen Termin zur Schaltung der Leitung für Sie reserviert. Der Service-Techniker kommt am @schalttermin@ @zeitfenster@ zu Ihnen. Bitte stellen Sie sicher, dass Sie persönlich oder eine volljährige Person Ihres Vertrauens in dieser Zeit vor Ort sind. Der Techniker benötigt Zugang zur Anschlusstechnik. Diese befindet sich meist im Keller Ihres Gebäudes. Denken Sie daran, dass er Ihre Umgebung nicht kennt. Namens- oder Hinweisschilder helfen oft, damit alles schnell und reibungslos verläuft. Vielen Dank.

Viele Grüße

Ihr M-net Kundenservice'
WHERE MELDUNG_TYP = 'ABM' AND MONTAGE = 'YES' AND AENDERUNGSKENNZEICHEN = 'TERMINVERSCHIEBUNG';

-- update Texte fuer MELDUNG_TYP = 'ABM' und MONTAGE=NO und AENDERUNGSKENNZEICHEN=TERMINVERSCHIEBUNG     (ID = 19,20,21,22,23,24)
UPDATE T_SMS_CONFIG
SET
  EMAIL_TEXT = 'Sehr geehrte Kundin, sehr geehrter Kunde,

damit Sie Ihren Anschluss @produkt@ möglichst bald nutzen können, haben wir einen neuen Termin zur Schaltung der Leitung für Sie reserviert:
am @schalttermin@ @zeitfenster@
Sie müssen zu dieser Zeit nicht vor Ort sein.

Viele Grüße

Ihr M-net Kundenservice'
WHERE MELDUNG_TYP = 'ABM' AND MONTAGE = 'NO' AND AENDERUNGSKENNZEICHEN = 'TERMINVERSCHIEBUNG';

-- update Texte fuer MELDUNG_TYP = 'TAM' und MONTAGE=IGNORE und AENDERUNGSKENNZEICHEN=STANDARD und MELDUNGS_CODE=0     (ID = 25,26,27,28,29,30)
UPDATE T_SMS_CONFIG
SET
  EMAIL_TEXT = 'Sehr geehrte Kundin, sehr geehrter Kunde,

leider konnte Ihr Anschluss @produkt@ nicht geschaltet werden.
Bitte rufen Sie uns an, um das weitere Vorgehen gemeinsam zu besprechen.
Unter 0800 7080810 sind wir Montag bis Freitag in der Zeit von 08:00 bis 20:00 Uhr sowie Samstag in der Zeit von 09:00 bis 15:00 Uhr für Sie da.

Viele Grüße

Ihr M-net Kundenservice'
WHERE MELDUNG_TYP = 'TAM' AND MONTAGE = 'IGNORE' AND AENDERUNGSKENNZEICHEN = 'STANDARD' AND MELDUNGS_CODE = '0' ;

-- update Texte fuer MELDUNG_TYP = 'TAM' und MONTAGE=IGNORE und AENDERUNGSKENNZEICHEN=STANDARD und MELDUNGS_CODE=6012           (ID = 41,42,43)
UPDATE T_SMS_CONFIG
SET
  EMAIL_TEXT = 'Sehr geehrte Kundin, sehr geehrter Kunde,

leider konnte Ihr Anschluss am @schalttermin@ nicht geschaltet werden, eine erneute Schaltung findet am Folgewerktag statt.

Viele Grüße

Ihr M-net Kundenservice',
  EMAIL_BETREFF = 'Schaltung am Folgewerktag'
WHERE MELDUNG_TYP = 'TAM' AND MONTAGE = 'IGNORE' AND AENDERUNGSKENNZEICHEN = 'STANDARD' AND MELDUNGS_CODE = '6012' ;
