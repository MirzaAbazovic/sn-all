alter table T_HW_RACK drop constraint CK_HW_RACK_TYP;
alter table T_HW_RACK add constraint CK_HW_RACK_TYP check (RACK_TYP IN ('DSLAM', 'DLU', 'ROUTER', 'LTG', 'UEVT', 'MDU', 'OLT', 'PDH', 'SDH', 'ONT')) enable novalidate;
