alter table T_EG_2_AUFTRAG add DEACTIVATED CHAR(1) DEFAULT 0;
update T_EG_2_AUFTRAG set DEACTIVATED='0';
alter table T_EG_2_AUFTRAG modify DEACTIVATED CHAR(1) NOT NULL;