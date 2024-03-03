-- zusätzliches Feld zum Speichern der ONT-ID aus der ResourceSpecs
alter table t_hw_rack_ont add ( ont_id varchar2 ( 20 ) NOT NULL );
