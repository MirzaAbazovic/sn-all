--
-- SQL-Script, um IP-Adressen temporaer in Hurrican zu halten.
--

drop table T_IP;
CREATE TABLE T_IP (
       ID NUMBER(10) NOT  NULL
     ,  TECH_ORDER_ID NUMBER(10) 
     , IP_ADDRESS VARCHAR2(15) 
     , NETMASK VARCHAR2(15) 
     , DEFAULT_GATEWAY VARCHAR2(15)
     , IP_TYPE VARCHAR2(15)
);

ALTER TABLE T_IP ADD CONSTRAINT PK_T_IP PRIMARY KEY (ID);

create sequence S_T_IP_0 start with 1;
grant select on S_T_IP_0 to public;
commit;

grant select on T_IP to R_HURRICAN_READ_ONLY;
grant select, insert, update, delete on T_IP to R_HURRICAN_USER;
commit;

