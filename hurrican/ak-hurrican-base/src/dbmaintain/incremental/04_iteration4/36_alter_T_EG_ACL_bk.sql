--
-- The Business key is name not text
--

ALTER TABLE T_EG_ACL
 DROP CONSTRAINT UK_T_EG_ACL_BK;

-- Business key is ACL_TEXT
ALTER TABLE T_EG_ACL
  ADD CONSTRAINT UK_T_EG_ACL_BK
      UNIQUE (NAME);
