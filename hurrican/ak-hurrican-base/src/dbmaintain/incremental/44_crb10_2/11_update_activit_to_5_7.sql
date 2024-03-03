alter table ACT_HI_PROCINST
add SUPER_PROCESS_INSTANCE_ID_ NVARCHAR2(64);

UPDATE   ACT_GE_PROPERTY P
   SET   P.REV_ = 2, P.VALUE_ = '5.7'
   where P.NAME_ = 'schema.version';