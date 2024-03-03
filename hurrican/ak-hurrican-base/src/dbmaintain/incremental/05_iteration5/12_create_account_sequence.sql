--
-- Script legt eine neue Sequence fuer die Account-Generierung an
--

create SEQUENCE SEQ_ACCOUNT start with 10000000;
grant select on SEQ_ACCOUNT to public;

