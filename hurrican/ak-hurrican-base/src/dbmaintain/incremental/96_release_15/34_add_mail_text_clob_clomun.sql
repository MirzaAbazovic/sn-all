alter table T_MAIL add (TEXT_LONG CLOB);
--add flag for HTML mail to table T_MAIL
ALTER TABLE T_MAIL ADD (TEXT_LONG_HTML NUMBER(1) DEFAULT 0);
