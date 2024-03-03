--
-- Script, um die Endstelle mit der Spalte LAST_CHANGE zu versehen
--

-- ausgefuehrt (in PROD) am 10.04.2007 (JG)
--alter table T_ENDSTELLE add COLUMN LAST_CHANGE DATE;
--update T_ENDSTELLE set LAST_CHANGE=now() where LAST_CHANGE is null;

-- erst ausfuehren, wenn alle Clients auf Version >= 2007-04-10 sind
alter table T_ENDSTELLE change COLUMN LAST_CHANGE LAST_CHANGE DATE NOT NULL;



