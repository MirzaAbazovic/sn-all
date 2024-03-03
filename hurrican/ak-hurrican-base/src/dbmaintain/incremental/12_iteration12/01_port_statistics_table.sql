CREATE TABLE T_PORT_STATISTICS (
	anzahl number(18),
	karten_typ varchar2(50),
	port_typ varchar2(50),
	prod_id number(18),
	asb number(10),
	onkz varchar2(10),
	niederlassung varchar2(50),
	monat varchar2(10),
	UNIQUE(karten_typ, port_typ, prod_id, asb, onkz, niederlassung, monat)
);

GRANT SELECT ON T_PORT_STATISTICS TO R_HURRICAN_READ_ONLY;
GRANT SELECT ON T_PORT_STATISTICS TO R_HURRICAN_TECHNIKMUC;
GRANT SELECT, UPDATE ON T_PORT_STATISTICS TO R_HURRICAN_TOOLS;
GRANT INSERT, SELECT, UPDATE ON T_PORT_STATISTICS TO R_HURRICAN_USER;