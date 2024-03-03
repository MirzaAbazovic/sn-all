-- FTTH Korrekturen fuer Ultraspeed (300Mbit)
UPDATE t_physiktyp
SET max_bandwidth = 300000
WHERE name = 'FTTH_ETH';
UPDATE t_hw_baugruppen_typ
SET max_bandwidth = 300000
WHERE name = 'HG865_ETH';
UPDATE t_hw_baugruppen_typ
SET max_bandwidth = 300000
WHERE name = 'HG8242_ETH';
UPDATE t_hw_baugruppen_typ
SET max_bandwidth = 300000
WHERE name = 'I-241G-Q_ETH';
UPDATE t_hw_baugruppen_typ
SET max_bandwidth = 300000
WHERE name = 'I-010G-P_ETH';
UPDATE t_hw_baugruppen_typ
SET max_bandwidth = 100000
WHERE name = 'I-221E-A_ETH';