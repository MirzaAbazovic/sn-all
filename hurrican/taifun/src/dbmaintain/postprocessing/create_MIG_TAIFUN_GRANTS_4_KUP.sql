BEGIN
  sys.utl_recomp.recomp_serial();
  COMMIT;
END;
/
BEGIN
   FOR one
   IN ( SELECT  a.object_name
           FROM   dba_objects a
          WHERE
          (
          object_name like '%WEBGATE_PW%' OR
          object_name like 'AUFTRAGPOS' OR
          object_name like 'SERVICE_VALUE_PRICE' OR
          object_name like 'LEISTUNG' OR
          object_name like 'LEISTUNG_LANG' OR
          object_name like 'PERSON' OR
          object_name like 'PERSON' OR
          object_name like 'DN' OR
          object_name like 'PURCHASE_ORDER' OR
          object_name like 'BILL_SPEC' OR
          object_name like 'CUSTOMER'  OR
          object_name like '%ADDRESS%' OR
          object_name like '%ADDRESS%' OR
          object_name like '%AUFTRAG%'
          ) and
          instr(object_name,'HIST') = 0 and
          instr(object_name,'/') = 0 and
          instr(object_name,'$') = 0 and
          status = 'VALID' and
          object_type = 'SYNONYM' and
          owner = 'PUBLIC'
       ORDER BY   a.object_name)
   LOOP
      BEGIN
         EXECUTE IMMEDIATE 'GRANT ALL ON ' || one.object_name  || ' TO TAIFUN_KUP WITH GRANT OPTION';
      EXCEPTION
         WHEN OTHERS
         THEN
            NULL;
      END;
   END LOOP;
END;
/
BEGIN
  sys.utl_recomp.recomp_serial();
  COMMIT;
END;
/
