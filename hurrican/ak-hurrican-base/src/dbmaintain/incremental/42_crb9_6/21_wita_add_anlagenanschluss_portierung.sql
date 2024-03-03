create table T_MWF_PORT_RUFNUMMERNBLOCK (
    ID NUMBER(19,0) not null
  , VERSION NUMBER(19,0) not null
  , PORTIERUNGS_ID NUMBER(19,0) not null
  , VON VARCHAR2(10)
  , BIS VARCHAR2(10)
  , PRIMARY KEY (ID)
);

grant select, insert, update on  T_MWF_PORT_RUFNUMMERNBLOCK to R_HURRICAN_USER;
grant select on  T_MWF_PORT_RUFNUMMERNBLOCK to R_HURRICAN_READ_ONLY;

create sequence S_T_MWF_PORT_RBLOCK_0 start with 1;
grant select on S_T_MWF_PORT_RBLOCK_0 to public;

ALTER TABLE T_MWF_PORTIERUNG ADD (ONKZ VARCHAR2(5));
ALTER TABLE T_MWF_PORTIERUNG ADD (DURCHWAHL VARCHAR2(8));
ALTER TABLE T_MWF_PORTIERUNG ADD (ABFRAGESTELLE VARCHAR2(6));
