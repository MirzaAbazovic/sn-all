
DECLARE
   iCount   NUMBER (11) := NULL;
   csql     VARCHAR2 (10000) := NULL;
BEGIN
   SELECT NVL ( (SELECT MAX (LEITUNG_GESAMT_ID) + 1 FROM T_RANGIERUNG), 10000)
   INTO iCount
   FROM DUAL;

   BEGIN
      EXECUTE IMMEDIATE 'DROP SEQUENCE S_T_RANGIERUNG_LTGGESID_0';
   EXCEPTION
      WHEN OTHERS
      THEN
         NULL;
   END;

   csql :=
      'CREATE SEQUENCE S_T_RANGIERUNG_LTGGESID_0 START WITH ' ||
      iCount ||
      ' INCREMENT BY 1 CACHE 20';
   EXECUTE IMMEDIATE (csql);
END;
/

grant select on S_T_RANGIERUNG_LTGGESID_0 to public;
