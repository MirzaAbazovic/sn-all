alter table T_MWF_ANLAGE add INHALT BLOB;
update T_MWF_ANLAGE set INHALT='C9';
alter table T_MWF_ANLAGE modify INHALT NOT NULL;
