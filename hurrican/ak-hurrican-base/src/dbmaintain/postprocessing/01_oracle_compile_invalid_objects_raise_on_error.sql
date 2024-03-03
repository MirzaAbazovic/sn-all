DECLARE
   i   NUMBER := 1;
BEGIN
   FOR i IN 1 .. 3
   LOOP
      BEGIN
         FOR one
         IN (SELECT object_type,
                    object_name
             FROM user_objects
             WHERE object_type IN
                         ('FUNCTION',
                          'PACKAGE',
                          'VIEW',
                          'TRIGGER',
                          'PROCEDURE') AND
                   status = 'INVALID')
         LOOP
            -- dbms_output.put_line('Updating '||one.object_type||' '||one.object_name);
            BEGIN
               EXECUTE IMMEDIATE 'alter ' ||
                                one.object_type ||
                                ' ' ||
                                one.object_name ||
                                ' compile';
            EXCEPTION
               WHEN OTHERS
               THEN
                  NULL;                                -- ignore, and proceed.
            END;
         END LOOP;
      END;
   END LOOP;
END;
/
--------------------------------------

BEGIN
   FOR one
   IN (SELECT object_type,
              object_name
       FROM user_objects
       WHERE object_type IN
                   ('FUNCTION', 'PACKAGE', 'VIEW', 'TRIGGER', 'PROCEDURE') AND
             status = 'INVALID')
   LOOP
      BEGIN
         EXECUTE IMMEDIATE 'alter ' ||
                          one.object_type ||
                          ' ' ||
                          one.object_name ||
                          ' compile';
      EXCEPTION
         WHEN OTHERS
         THEN
            raise_application_error (-20002,
                                     SQLERRM ||
                                     ': ' ||
                                     one.object_type ||
                                     ' ' ||
                                     one.object_name);
      END;
   END LOOP;
END;
/
