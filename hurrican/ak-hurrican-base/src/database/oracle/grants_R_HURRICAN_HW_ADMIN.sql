--
-- GRANTs fuer die DB-Rolle 'R_HURRICAN_TOOLS'
--

-- Rolle fuer externe Tools anlegen
CREATE ROLE "R_HURRICAN_HW_ADMIN" NOT IDENTIFIED;
GRANT SELECT, INSERT, UPDATE, DELETE ON T_HW_RACK TO R_HURRICAN_HW_ADMIN;
GRANT SELECT, INSERT, UPDATE, DELETE ON T_HW_RACK_DLU TO R_HURRICAN_HW_ADMIN;
GRANT SELECT, INSERT, UPDATE, DELETE ON T_HW_RACK_DSLAM TO R_HURRICAN_HW_ADMIN;
GRANT SELECT, INSERT, UPDATE, DELETE ON T_HW_RACK_LTG TO R_HURRICAN_HW_ADMIN;
GRANT SELECT, INSERT, UPDATE, DELETE ON T_HW_RACK_ROUTER TO R_HURRICAN_HW_ADMIN;
GRANT SELECT, INSERT, UPDATE, DELETE ON T_HW_BAUGRUPPE TO R_HURRICAN_HW_ADMIN;
GRANT SELECT, INSERT, UPDATE, DELETE ON T_HW_BAUGRUPPEN_TYP TO R_HURRICAN_HW_ADMIN;
GRANT SELECT, INSERT, UPDATE, DELETE ON T_HVT_RAUM TO R_HURRICAN_HW_ADMIN;
GRANT SELECT, INSERT, UPDATE, DELETE ON T_UEVT TO R_HURRICAN_HW_ADMIN;
GRANT SELECT, UPDATE ON T_HVT_STANDORT TO R_HURRICAN_HW_ADMIN;

GRANT SELECT, INSERT, UPDATE, DELETE ON T_HW_SUBRACK TO R_HURRICAN_HW_ADMIN;
GRANT SELECT, INSERT, UPDATE, DELETE ON T_HW_SUBRACK_TYP TO R_HURRICAN_HW_ADMIN;

GRANT SELECT, INSERT, UPDATE, DELETE ON T_HW_RACK_MDU TO R_HURRICAN_HW_ADMIN;
GRANT SELECT, INSERT, UPDATE, DELETE ON T_HW_RACK_OLT TO R_HURRICAN_HW_ADMIN;
