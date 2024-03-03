-- Drop the intermediate table for IPs for BSI-View
-- Now the data is provided via MONLINE

BEGIN
     EXECUTE IMMEDIATE('DROP VIEW VH_HURRICAN_IP');
 EXCEPTION
    WHEN OTHERS THEN
 NULL;
 END;
/

drop table t_ip;
