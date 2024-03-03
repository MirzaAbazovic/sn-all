-- +++++++++++++++++++++++++++++++++++++++++++++++++++
-- Copyright (c) 2008 - M-net Telekommunikations GmbH
-- All rights reserved.
-- +++++++++++++++++++++++++++++++++++++++++++++++++++
--
-- View fuer die Darstellung von Auftragstexten.
-- 

CREATE OR REPLACE VIEW "AUFTRAGTEXT_VIEW"("AUFTRAG_NO",      
	"TEXTTYP","EINTRAG","USERW","DATEW") 
AS
    SELECT
	AUFTRAG_NO,
	TEXTTYP,
	EINTRAG,
	USERW,
	DATEW
	FROM
	AUFTRAGTEXT
where TEXTTYP = 'VPN-Projektleiter';

