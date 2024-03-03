

DECLARE
   iCount   NUMBER (11) := NULL;
   csql     VARCHAR2 (10000) := NULL;
BEGIN
   SELECT NVL ( (SELECT MAX (ID) + 1 FROM T_TDN), 10000)
   INTO iCount
   FROM DUAL;

   BEGIN
      EXECUTE IMMEDIATE 'DROP SEQUENCE S_T_TDN_0';
   EXCEPTION
      WHEN OTHERS
      THEN
         NULL;
   END;

   csql :=
      'CREATE SEQUENCE S_T_TDN_0 START WITH ' ||
      iCount ||
      ' INCREMENT BY 1 CACHE 20';
   EXECUTE IMMEDIATE (csql);
END;
/
GRANT SELECT ON S_T_TDN_0 TO PUBLIC;

-- ############################

DECLARE
   iCount   NUMBER (11) := NULL;
   csql     VARCHAR2 (10000) := NULL;
BEGIN
   SELECT NVL ( (SELECT MAX (ID) + 1 FROM T_HOUSING_BUILDING), 10000)
   INTO iCount
   FROM DUAL;

   BEGIN
      EXECUTE IMMEDIATE 'DROP SEQUENCE S_T_HOUSING_BUILDING_0';
   EXCEPTION
      WHEN OTHERS
      THEN
         NULL;
   END;

   csql :=
      'CREATE SEQUENCE S_T_HOUSING_BUILDING_0 START WITH ' ||
      iCount ||
      ' INCREMENT BY 1 CACHE 20';
   EXECUTE IMMEDIATE (csql);
END;
/
GRANT SELECT ON S_T_HOUSING_BUILDING_0 TO PUBLIC;


-- ############################

DECLARE
   iCount   NUMBER (11) := NULL;
   csql     VARCHAR2 (10000) := NULL;
BEGIN
   SELECT NVL ( (SELECT MAX (ID) + 1 FROM T_HOUSING_FLOOR), 10000)
   INTO iCount
   FROM DUAL;

   BEGIN
      EXECUTE IMMEDIATE 'DROP SEQUENCE S_T_HOUSING_FLOOR_0';
   EXCEPTION
      WHEN OTHERS
      THEN
         NULL;
   END;

   csql :=
      'CREATE SEQUENCE S_T_HOUSING_FLOOR_0 START WITH ' ||
      iCount ||
      ' INCREMENT BY 1 CACHE 20';
   EXECUTE IMMEDIATE (csql);
END;
/
GRANT SELECT ON S_T_HOUSING_FLOOR_0 TO PUBLIC;


-- ############################

DECLARE
   iCount   NUMBER (11) := NULL;
   csql     VARCHAR2 (10000) := NULL;
BEGIN
   SELECT NVL ( (SELECT MAX (ID) + 1 FROM T_HOUSING_PARCEL), 10000)
   INTO iCount
   FROM DUAL;

   BEGIN
      EXECUTE IMMEDIATE 'DROP SEQUENCE S_T_HOUSING_PARCEL_0';
   EXCEPTION
      WHEN OTHERS
      THEN
         NULL;
   END;

   csql :=
      'CREATE SEQUENCE S_T_HOUSING_PARCEL_0 START WITH ' ||
      iCount ||
      ' INCREMENT BY 1 CACHE 20';
   EXECUTE IMMEDIATE (csql);
END;
/
GRANT SELECT ON S_T_HOUSING_PARCEL_0 TO PUBLIC;



-- ############################

DECLARE
   iCount   NUMBER (11) := NULL;
   csql     VARCHAR2 (10000) := NULL;
BEGIN
   SELECT NVL ( (SELECT MAX (ID) + 1 FROM T_HOUSING_ROOM), 10000)
   INTO iCount
   FROM DUAL;

   BEGIN
      EXECUTE IMMEDIATE 'DROP SEQUENCE S_T_HOUSING_ROOM_0';
   EXCEPTION
      WHEN OTHERS
      THEN
         NULL;
   END;

   csql :=
      'CREATE SEQUENCE S_T_HOUSING_ROOM_0 START WITH ' ||
      iCount ||
      ' INCREMENT BY 1 CACHE 20';
   EXECUTE IMMEDIATE (csql);
END;
/
GRANT SELECT ON S_T_HOUSING_ROOM_0 TO PUBLIC;



-- ############################

DECLARE
   iCount   NUMBER (11) := NULL;
   csql     VARCHAR2 (10000) := NULL;
BEGIN
   SELECT NVL ( (SELECT MAX (ID) + 1 FROM T_AUFTRAG_CONNECT), 10000)
   INTO iCount
   FROM DUAL;

   BEGIN
      EXECUTE IMMEDIATE 'DROP SEQUENCE S_T_AUFTRAG_CONNECT_0';
   EXCEPTION
      WHEN OTHERS
      THEN
         NULL;
   END;

   csql :=
      'CREATE SEQUENCE S_T_AUFTRAG_CONNECT_0 START WITH ' ||
      iCount ||
      ' INCREMENT BY 1 CACHE 20';
   EXECUTE IMMEDIATE (csql);
END;
/
GRANT SELECT ON S_T_AUFTRAG_CONNECT_0 TO PUBLIC;



-- ############################

DECLARE
   iCount   NUMBER (11) := NULL;
   csql     VARCHAR2 (10000) := NULL;
BEGIN
   SELECT NVL ( (SELECT MAX (ID) + 1 FROM T_AUFTRAG_HOUSING), 10000)
   INTO iCount
   FROM DUAL;

   BEGIN
      EXECUTE IMMEDIATE 'DROP SEQUENCE S_T_AUFTRAG_HOUSING_0';
   EXCEPTION
      WHEN OTHERS
      THEN
         NULL;
   END;

   csql :=
      'CREATE SEQUENCE S_T_AUFTRAG_HOUSING_0 START WITH ' ||
      iCount ||
      ' INCREMENT BY 1 CACHE 20';
   EXECUTE IMMEDIATE (csql);
END;
/
GRANT SELECT ON S_T_AUFTRAG_HOUSING_0 TO PUBLIC;



-- ############################

DECLARE
   iCount   NUMBER (11) := NULL;
   csql     VARCHAR2 (10000) := NULL;
BEGIN
   SELECT NVL ( (SELECT MAX (ID) + 1 FROM T_ENDSTELLE_CONNECT), 10000)
   INTO iCount
   FROM DUAL;

   BEGIN
      EXECUTE IMMEDIATE 'DROP SEQUENCE S_T_ENDSTELLE_CONNECT_0';
   EXCEPTION
      WHEN OTHERS
      THEN
         NULL;
   END;

   csql :=
      'CREATE SEQUENCE S_T_ENDSTELLE_CONNECT_0 START WITH ' ||
      iCount ||
      ' INCREMENT BY 1 CACHE 20';
   EXECUTE IMMEDIATE (csql);
END;
/
GRANT SELECT ON S_T_ENDSTELLE_CONNECT_0 TO PUBLIC;


-- ############################

DECLARE
   iCount   NUMBER (11) := NULL;
   csql     VARCHAR2 (10000) := NULL;
BEGIN
   SELECT NVL ( (SELECT MAX (ID) + 1 FROM T_LEITUNGSNUMMER), 10000)
   INTO iCount
   FROM DUAL;

   BEGIN
      EXECUTE IMMEDIATE 'DROP SEQUENCE S_T_LEITUNGSNUMMER_0';
   EXCEPTION
      WHEN OTHERS
      THEN
         NULL;
   END;

   csql :=
      'CREATE SEQUENCE S_T_LEITUNGSNUMMER_0 START WITH ' ||
      iCount ||
      ' INCREMENT BY 1 CACHE 20';
   EXECUTE IMMEDIATE (csql);
END;
/
GRANT SELECT ON S_T_LEITUNGSNUMMER_0 TO PUBLIC;


-- ############################

DECLARE
   iCount   NUMBER (11) := NULL;
   csql     VARCHAR2 (10000) := NULL;
BEGIN
   SELECT NVL ( (SELECT MAX (ID) + 1 FROM T_MAIL), 10000)
   INTO iCount
   FROM DUAL;

   BEGIN
      EXECUTE IMMEDIATE 'DROP SEQUENCE S_T_MAIL_0';
   EXCEPTION
      WHEN OTHERS
      THEN
         NULL;
   END;

   csql :=
      'CREATE SEQUENCE S_T_MAIL_0 START WITH ' ||
      iCount ||
      ' INCREMENT BY 1 CACHE 20';
   EXECUTE IMMEDIATE (csql);
END;
/
GRANT SELECT ON S_T_MAIL_0 TO PUBLIC;


-- ############################

DECLARE
   iCount   NUMBER (11) := NULL;
   csql     VARCHAR2 (10000) := NULL;
BEGIN
   SELECT NVL ( (SELECT MAX (ID) + 1 FROM T_MAIL_ATTACHMENT), 10000)
   INTO iCount
   FROM DUAL;

   BEGIN
      EXECUTE IMMEDIATE 'DROP SEQUENCE S_T_MAIL_ATTACHMENT_0';
   EXCEPTION
      WHEN OTHERS
      THEN
         NULL;
   END;

   csql :=
      'CREATE SEQUENCE S_T_MAIL_ATTACHMENT_0 START WITH ' ||
      iCount ||
      ' INCREMENT BY 1 CACHE 20';
   EXECUTE IMMEDIATE (csql);
END;
/
GRANT SELECT ON S_T_MAIL_ATTACHMENT_0 TO PUBLIC;
