create table T_KUENDIGUNG_CHECK (
  ID number(19) primary key,
  OE__NO number(19) not null,
  DURCH_VERTRIEB char(1) default 0 not null,
  AUTO_VERLAENGERUNG number(19) default 0 not null,
  version number(19) default 0 not null
);
create sequence S_T_KUENDIGUNG_CHECK_0 start with 1 increment by 1;
alter table T_KUENDIGUNG_CHECK add constraint UQ_KUENDIGUNG_CHECK UNIQUE (OE__NO);

COMMENT ON TABLE T_KUENDIGUNG_CHECK IS 'Basistabelle fuer Berechnung der Kuendigungsfrist von Auftraegen';
COMMENT ON COLUMN T_KUENDIGUNG_CHECK.OE__NO IS 'Produkt Id aus Taifun';
COMMENT ON COLUMN T_KUENDIGUNG_CHECK.DURCH_VERTRIEB IS 'Flag definiert, dass die Berechnung durch den Vertrieb geschehen muss';
COMMENT ON COLUMN T_KUENDIGUNG_CHECK.AUTO_VERLAENGERUNG IS 'Anzahl Monate, um die sich ein Vertrag automatisch verlaengert, wenn nicht rechtzeitig gekuendigt';


create table T_KUENDIGUNG_FRIST (
  ID number(19) primary key,
  KUENDIGUNG_CHECK_ID number(19) references T_KUENDIGUNG_CHECK(ID) not null,
  MIT_MVLZ char(1) default 0 not null,
  FRIST_IN_WOCHEN number(19) default 0 not null,
  FRIST_AUF varchar2(20) not null,
  VERTRAG_AB_JAHR number(19),
  VERTRAG_AB_MONAT number(19),
  DESCRIPTION VARCHAR2(255),
  VERSION NUMBER(19) default 0 not null
);
create sequence S_T_KUENDIGUNG_FRIST_0 start with 1 increment by 1;

ALTER TABLE T_KUENDIGUNG_FRIST
ADD CONSTRAINT CK_KUENDF_FRIST_AUF
CHECK (FRIST_AUF IN (
    'ENDE_MVLZ',
    'MONATESENDE',
    'EINGANGSDATUM'
  )) ENABLE NOVALIDATE;

COMMENT ON TABLE T_KUENDIGUNG_FRIST IS 'Tabelle zur Konfiguration der Kuendigungsfristen abhaengig von Mindestvertragslaufzeit (MVLZ) bzw. Vertrags-Start';
COMMENT ON COLUMN T_KUENDIGUNG_FRIST.MIT_MVLZ IS 'Flag definiert, dass die Konfiguration gueltig ist, wenn der Auftrag eine Mindestvertragslaufzeit besitzt';
COMMENT ON COLUMN T_KUENDIGUNG_FRIST.FRIST_IN_WOCHEN IS 'Anzahl Wochen, wann die Kuendigung eingehen muss';
COMMENT ON COLUMN T_KUENDIGUNG_FRIST.FRIST_AUF IS 'Gibt an, ob sich die FRIST_IN_WOCHEN auf das Monatsende, das Eingangsdatum oder auf Ende der MVLZ bezogen ist';
COMMENT ON COLUMN T_KUENDIGUNG_FRIST.VERTRAG_AB_JAHR IS 'Konfiguration ist nur fuer Auftraege gueltig, die ab diesem Jahr eingegangen sind';
COMMENT ON COLUMN T_KUENDIGUNG_FRIST.VERTRAG_AB_MONAT IS 'Konfiguration ist nur fuer Auftraege gueltig, die ab diesem Monat (in Verbindung mit ...JAHR) eingegangen sind';


GRANT SELECT, INSERT, UPDATE ON T_KUENDIGUNG_CHECK TO R_HURRICAN_USER;
GRANT SELECT ON T_KUENDIGUNG_CHECK TO R_HURRICAN_READ_ONLY;
GRANT SELECT ON S_T_KUENDIGUNG_CHECK_0 TO PUBLIC;

GRANT SELECT, INSERT, UPDATE ON T_KUENDIGUNG_FRIST TO R_HURRICAN_USER;
GRANT SELECT ON T_KUENDIGUNG_FRIST TO R_HURRICAN_READ_ONLY;
GRANT SELECT ON S_T_KUENDIGUNG_FRIST_0 TO PUBLIC;