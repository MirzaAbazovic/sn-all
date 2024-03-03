--
-- SQL-Script fuehrt die Migration der Sperren durch.
--

-- ungueltige Sperr-Eintraege werden geloescht
delete from T_SPERRE where S_DATUM is null;
commit;

-- weitere Migration ueber Java-Klasse: to-be-defined

