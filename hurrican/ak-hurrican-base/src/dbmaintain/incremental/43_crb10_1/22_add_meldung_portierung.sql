ALTER TABLE T_MWF_MELDUNG ADD (RUFNUMMERNPORTIERUNG_ID NUMBER(19,0));

create table T_MWF_MELDUNG_RUF_PORTIERUNG (
    ID NUMBER(19,0) not null
  , VERSION NUMBER(19,0) not null
  , ONKZ VARCHAR2(5)
  , DIALERNUMBER VARCHAR2(8)
  , DIRECTDIAL VARCHAR2(6)
  , PRIMARY KEY (ID)
);
 
grant select, insert, update on  T_MWF_MELDUNG_RUF_PORTIERUNG to R_HURRICAN_USER;
grant select on  T_MWF_MELDUNG_RUF_PORTIERUNG to R_HURRICAN_READ_ONLY;

create sequence S_T_MWF_MELDUNG_PORTIERUNG_0 start with 1;
grant select on S_T_MWF_MELDUNG_PORTIERUNG_0 to public;

create table T_MWF_RUFNR_BLOCK_PORTIERUNG (
    ID NUMBER(19,0) not null
  , RUF_PORTIERUNG_ID NUMBER(19,0)   
  , BLOCK_VON VARCHAR2(10)
  , BLOCK_BIS VARCHAR2(10)
  , PRIMARY KEY (ID)
);

ALTER TABLE T_MWF_RUFNR_BLOCK_PORTIERUNG ADD CONSTRAINT FK_MWF_RUFNR_PORTIERUNG
  FOREIGN KEY (RUF_PORTIERUNG_ID)
  REFERENCES T_MWF_RUFNR_BLOCK_PORTIERUNG (ID);
  
  
grant select, insert, update on  T_MWF_RUFNR_BLOCK_PORTIERUNG to R_HURRICAN_USER;
grant select on  T_MWF_RUFNR_BLOCK_PORTIERUNG to R_HURRICAN_READ_ONLY;

create sequence S_T_MWF_RUFNR_BLK_PORT start with 1;
grant select on S_T_MWF_RUFNR_BLK_PORT to public;