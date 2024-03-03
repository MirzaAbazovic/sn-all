-- Iteriert ueber alle Tabellen und gibt der Rolle R_HURRICAN_TECHNIKMUC
-- SELECT Rechte


BEGIN
FOR hurrican_table_name IN (SELECT * FROM user_tables)
    LOOP
    EXECUTE IMMEDIATE ('GRANT SELECT ON ' || hurrican_table_name.table_name || ' TO R_HURRICAN_TECHNIKMUC');
    END LOOP;
END;
/

