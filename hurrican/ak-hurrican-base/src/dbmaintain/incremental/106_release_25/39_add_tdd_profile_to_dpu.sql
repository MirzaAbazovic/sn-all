ALTER TABLE T_HW_RACK_DPU ADD TDD_PROFIL VARCHAR2(5);

GRANT UPDATE ON T_HW_RACK TO R_HURRICAN_USER;
GRANT UPDATE ON T_HW_RACK_DPU TO R_HURRICAN_USER;