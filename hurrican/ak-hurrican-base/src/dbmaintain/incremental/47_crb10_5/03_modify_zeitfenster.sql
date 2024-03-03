-- Zeitfenster CLomun Größe und Contstraint angepasst damit der Slot-Name speicherbar ist. --

alter table T_CB_VORGANG drop constraint T_CB_VORGANG_ZEITFENSTER;

alter table T_CB_VORGANG modify (REALISERUNG_ZEITFENSTER  VARCHAR2(6));

alter table T_CB_VORGANG add (
  constraint T_CB_VORGANG_ZEITFENSTER
  check (REALISERUNG_ZEITFENSTER in ('SLOT_2','SLOT_3','SLOT_4','SLOT_6','SLOT_7')));