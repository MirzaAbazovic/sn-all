-- Fuer das Produkt 'TV Signallieferung MV' wird der initiale Erstellstatus auf 'In Betrieb' und
-- der Kuendigungsstatus auf 'Auftrag gekuendigt' gesetzt
update t_produkt set erstell_status_id = 6000, kuendigung_status_id = 9800 where prod_id = 522;