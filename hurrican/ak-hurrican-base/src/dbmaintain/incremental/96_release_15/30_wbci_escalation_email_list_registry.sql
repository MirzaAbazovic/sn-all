--add flag for HTML mail to table T_MAIL
ALTER TABLE T_MAIL ADD (TEXT_HTML NUMBER(1) DEFAULT 0);

-- insert e-mail-addresses for wbci escalation reports
INSERT INTO T_REGISTRY (ID, NAME, STR_VALUE, INT_VALUE, DESCRIPTION)
VALUES (5050, 'email.wbci.escalation.report.from', 'hurrican@m-net.de', NULL,
        'Absender E-Mail-Adresse für die WBCI-Eskalationsreports');

INSERT INTO T_REGISTRY (ID, NAME, STR_VALUE, INT_VALUE, DESCRIPTION)
VALUES (5051, 'email.wbci.escalation.report.to', 'norman.seyfarth@m-net.de;yvonne.sunke@m-net.de', NULL,
        'Empfänger E-Mail-Adresse für die WBCI-Eskalationsreports');
