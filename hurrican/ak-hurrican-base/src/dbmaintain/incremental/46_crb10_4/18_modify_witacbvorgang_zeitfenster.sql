alter table T_CB_VORGANG add (REALISERUNG_ZEITFENSTER  VARCHAR2(1));

alter table T_CB_VORGANG add (
  constraint T_CB_VORGANG_ZEITFENSTER
  check (REALISERUNG_ZEITFENSTER in ('2','3','4','6','7')));