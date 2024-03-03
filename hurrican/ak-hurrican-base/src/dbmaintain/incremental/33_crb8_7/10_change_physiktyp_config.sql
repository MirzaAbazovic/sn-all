-- Hersteller von Physiktyp FTTB_VDSL2 und FTTH entfernen
update t_physiktyp set hvt_technik_id=null where id in (800,803);