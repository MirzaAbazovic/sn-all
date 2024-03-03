--
-- GRANTs fuer die DB-Rolle 'R_HURRICAN_SCANVIEW_VIEWS'
-- (Rolle besitzt nur Lese-Rechte auf die fuer Scanview erstellte DB-Views.)
--

CREATE ROLE "R_HURRICAN_SCANVIEW_VIEWS" NOT IDENTIFIED;

GRANT SELECT ON V_HURRICAN_TECH_ORDER_BASE TO R_HURRICAN_SCANVIEW_VIEWS;

commit;



