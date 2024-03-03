-- create database links
BEGIN
    EXECUTE IMMEDIATE ('DROP DATABASE LINK BSICRM.MNET.DE');
    EXCEPTION
     WHEN OTHERS THEN
        NULL;
END;
/
CREATE DATABASE LINK BSICRM.MNET.DE CONNECT TO ${db.ors.user.schema}
    IDENTIFIED BY "${db.ors.user.password}" USING '${db.bsi.tns.name}'
/
BEGIN
    EXECUTE IMMEDIATE ('DROP DATABASE LINK HURRICAN.MNET.DE');
    EXCEPTION
     WHEN OTHERS THEN
        NULL;
END;
/
CREATE   DATABASE LINK HURRICAN.MNET.DE CONNECT TO ${db.hurrican.migration.user.schema}
    IDENTIFIED BY "${db.hurrican.migration.user.password}" USING '${db.hurrican.tns.name}'
/
BEGIN
    EXECUTE IMMEDIATE ('DROP DATABASE LINK KUPVIS.MNET.DE');
    EXCEPTION
     WHEN OTHERS THEN
        NULL;
END;
/
CREATE  DATABASE LINK KUPVIS.MNET.DE CONNECT TO ${db.kup.user.schema}
    IDENTIFIED BY "${db.kup.user.password}" USING '${db.kup.tns.name}'
/

--------------------------------------------------------------------------------
-- LKe 8.9.2010 lesender readonly Zugriff auf das Nuernberger ICCS
--------------------------------------------------------------------------------

BEGIN
   EXECUTE IMMEDIATE ('DROP DATABASE LINK ICCS.MNET.DE');
EXCEPTION
   WHEN OTHERS
   THEN
      NULL;
END;
/
CREATE   DATABASE LINK ICCS.MNET.DE CONNECT TO ICCS_RO
 IDENTIFIED BY iccs$ro$
 USING 'ICPROD01.m-net.de'
/

--------------------------------------------------------------------------------
-- end: LKe 8.9.2010
--------------------------------------------------------------------------------