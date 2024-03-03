--
-- Script entfernt die Berechtigungen auf T_SPERRE fuer die
-- Rolle der externen Tools
--

REVOKE SELECT, UPDATE on T_SPERRE from R_HURRICAN_TOOLS;
REVOKE SELECT on T_SPERRE from R_HURRICAN_READ_ONLY;
