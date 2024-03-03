-- Fuegt einen neuen Grund fuer die Aenderung des DSLAM Profils hinzu. Der neue Grund ist die nachtraegliche
-- automatische Ermittlung der Bandbreiten nach dem Sync
insert into T_DSLAM_PROFILE_CHANGE_REASON (id, name) values (10, 'Automatisch nach Sync');