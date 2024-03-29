-- Add constraint to limit the list of valid request statuses (taken from WbciRequestStatus.java)
ALTER TABLE T_WBCI_REQUEST DROP CONSTRAINT CK_WBCI_REQ_STATUS;

ALTER TABLE T_WBCI_REQUEST
ADD CONSTRAINT CK_WBCI_REQ_STATUS
CHECK (STATUS IN (
    'VA_EMPFANGEN',
    'RUEM_VA_VERSENDET',
    'ABBM_VERSENDET',
    'AKM_TR_EMPFANGEN',
    'ABBM_TR_VERSENDET',
    'VA_VORGEHALTEN',
    'VA_VERSENDET',
    'RUEM_VA_EMPFANGEN',
    'ABBM_EMPFANGEN',
    'AKM_TR_VERSENDET',
    'ABBM_TR_EMPFANGEN',
    'TV_EMPFANGEN',
    'TV_ERLM_VERSENDET',
    'TV_ABBM_VERSENDET',
    'TV_VORGEHALTEN',
    'TV_VERSENDET',
    'TV_ERLM_EMPFANGEN',
    'TV_ABBM_EMPFANGEN',
    'STORNO_VORGEHALTEN',
    'STORNO_VERSENDET',
    'STORNO_ERLM_EMPFANGEN',
    'STORNO_ABBM_EMPFANGEN',
    'STORNO_EMPFANGEN',
    'STORNO_ERLM_VERSENDET',
    'STORNO_ABBM_VERSENDET'
  )) ENABLE NOVALIDATE;
