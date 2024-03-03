--
-- Erhoehe die Anzahl der Pools um den Faktor 5
--

update Account
set max_active = 10,
    max_idle = 5
where acc_name = 'billing.default';

update Account
set max_active = 25,
    max_idle = 5
where acc_name = 'cc.writer';

update Account
set max_active = 10,
    max_idle = 5
where acc_name = 'monline.default';