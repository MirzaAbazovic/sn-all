ALTER TABLE t_tech_leistung  MODIFY typ VARCHAR2(20);

INSERT INTO t_tech_leistung
(id, name, extern_leistung__no, typ,
 prod_name_str, description, snapshot_rel,
 gueltig_von,
 gueltig_bis)
VALUES
  (600, 'Installationspaket Kunde', 31200, 'INHOUSE_VERKABELUNG',
   ' ', 'Eigeninstallation durch Kunde', '1',
   to_date('09/01/2014 00:00:00', 'MM/DD/YYYY HH24:MI:SS'),
   to_date('01/01/2200 00:00:00', 'MM/DD/YYYY HH24:MI:SS'));

INSERT INTO t_tech_leistung
(id, name, extern_leistung__no, typ,
 prod_name_str, description, snapshot_rel,
 gueltig_von,
 gueltig_bis)
VALUES
  (601, 'Installationspaket Keller', 31201, 'INHOUSE_VERKABELUNG',
   ' ', 'Installationspaket (im Keller)', '1',
   to_date('09/01/2014 00:00:00', 'MM/DD/YYYY HH24:MI:SS'),
   to_date('01/01/2200 00:00:00', 'MM/DD/YYYY HH24:MI:SS'));

INSERT INTO t_tech_leistung
(id, name, extern_leistung__no, typ,
 prod_name_str, description, snapshot_rel,
 gueltig_von,
 gueltig_bis)
VALUES
  (602, 'Installationspaket M-net', 31202, 'INHOUSE_VERKABELUNG',
   ' ', 'Installationpaket (durch M-net)', '1',
   to_date('09/01/2014 00:00:00', 'MM/DD/YYYY HH24:MI:SS'),
   to_date('01/01/2200 00:00:00', 'MM/DD/YYYY HH24:MI:SS'));
