
alter table T_HW_BG_CHANGE add CHANGE_STATE_REF_ID NUMBER(10);
update T_HW_BG_CHANGE set CHANGE_STATE_REF_ID=22150;
alter table T_HW_BG_CHANGE modify CHANGE_STATE_REF_ID NOT NULL;

CREATE INDEX IX_FK_HWBGCSTATE_2_REFERENCE ON T_HW_BG_CHANGE (CHANGE_STATE_REF_ID) TABLESPACE "I_HURRICAN";
ALTER TABLE T_HW_BG_CHANGE ADD CONSTRAINT FK_HWBGCSTATE_2_REFERENCE
  FOREIGN KEY (CHANGE_STATE_REF_ID) REFERENCES T_REFERENCE (ID);

insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)
  values (22150, 'PORT_SCHWENK_STATUS', 'in Planung', '1', 10, '');
insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)
  values (22151, 'PORT_SCHWENK_STATUS', 'vorbereitet', '1', 20, 'Ports / Rangierungen sind fuer voruebergehend gesperrt');
insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)
  values (22152, 'PORT_SCHWENK_STATUS', 'storniert', '1', 30, 'Der Baugruppen-Schwenk wurde storniert');
insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)
  values (22153, 'PORT_SCHWENK_STATUS', 'ausgefuehrt', '1', 40, 'Der Baugruppen-Schwenk wurde ausgefuehrt');
insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)
  values (22154, 'PORT_SCHWENK_STATUS', 'geschlossen', '1', 50, 'Der Baugruppen-Schwenk wurde endgueltig geschlossen');


