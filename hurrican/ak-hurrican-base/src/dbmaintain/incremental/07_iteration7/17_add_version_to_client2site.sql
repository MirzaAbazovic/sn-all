--
-- Version fuer Hibernate hinzufuegen
--

ALTER TABLE T_IPSEC_C2S
 ADD (VERSION  NUMBER(18)                           NOT NULL);

ALTER TABLE T_IPSEC_C2S_TOKEN
 ADD (VERSION  NUMBER(18)                           NOT NULL);