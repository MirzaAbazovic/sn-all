alter table T_USER_TASK add (
  constraint T_USER_TASK_AKM_PV_STATUS
  check (AKM_PV_STATUS in (
        'AKM_PV_EMPFANGEN',
        'RUEM_PV_GESENDET',
        'ABM_PV_EMPFANGEN',
        'ABBM_PV_EMPFANGEN')));
