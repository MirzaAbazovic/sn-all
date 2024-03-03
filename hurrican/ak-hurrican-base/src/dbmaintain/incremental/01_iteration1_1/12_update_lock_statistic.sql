--
-- Aktualisiert die Abfrage fuer die Sperren innerhalb eines best. Zeitraums
--

update T_DB_QUERIES set
    SQL_QUERY='select l.CUSTOMER_NO, l.DEB_ID, rmode.STR_VALUE as LOCK_MODE,
    rreason.STR_VALUE as LOCK_REASON, l.LOCK_REASON_TEXT, l.CREATED_AT, l.CREATED_FROM
    from T_LOCK l
    inner join T_REFERENCE rmode on l.LOCK_MODE_REF_ID=rmode.ID
    left join T_REFERENCE rreason on l.LOCK_REASON_REF_ID=rreason.ID
    where l.CREATED_AT>=? and l.CREATED_AT<=? order by l.ID'
where ID=11;

update T_DB_QUERIES set NAME='OLD: Sperren - aktiv',
    DESCRIPTION='DON´T USE THIS QUERY! JUST FOR MIGRATION VALIDATION! to-be-deleted!'
    where ID=13;
commit;
