
UPDATE T_RANGIERUNG SET FREIGEGEBEN = '0' WHERE FREIGEGEBEN = 'gesperrt';
UPDATE T_RANGIERUNG SET FREIGEGEBEN = '1' WHERE FREIGEGEBEN = 'freigegeben';
UPDATE T_RANGIERUNG SET FREIGEGEBEN = '1' WHERE FREIGEGEBEN = 'deactivated';
UPDATE T_RANGIERUNG SET FREIGEGEBEN = '4' WHERE FREIGEGEBEN = 'Backbone_down';
UPDATE T_RANGIERUNG SET FREIGEGEBEN = '5' WHERE FREIGEGEBEN = 'WEPLA';
UPDATE T_RANGIERUNG SET FREIGEGEBEN = '7' WHERE FREIGEGEBEN = 'defekt';
UPDATE T_RANGIERUNG SET FREIGEGEBEN = '9' WHERE FREIGEGEBEN = 'in_Aufbau';


UPDATE T_HW_BG_CHANGE_PORT2PORT SET RANG_FREIGABE_ORIG_OLD = '0' WHERE RANG_FREIGABE_ORIG_OLD = 'gesperrt';
UPDATE T_HW_BG_CHANGE_PORT2PORT SET RANG_FREIGABE_ORIG_OLD = '1' WHERE RANG_FREIGABE_ORIG_OLD = 'freigegeben';
UPDATE T_HW_BG_CHANGE_PORT2PORT SET RANG_FREIGABE_ORIG_OLD = '2' WHERE RANG_FREIGABE_ORIG_OLD = 'deactivated';
UPDATE T_HW_BG_CHANGE_PORT2PORT SET RANG_FREIGABE_ORIG_OLD = '4' WHERE RANG_FREIGABE_ORIG_OLD = 'Backbone_down';
UPDATE T_HW_BG_CHANGE_PORT2PORT SET RANG_FREIGABE_ORIG_OLD = '5' WHERE RANG_FREIGABE_ORIG_OLD = 'WEPLA';
UPDATE T_HW_BG_CHANGE_PORT2PORT SET RANG_FREIGABE_ORIG_OLD = '7' WHERE RANG_FREIGABE_ORIG_OLD = 'defekt';
UPDATE T_HW_BG_CHANGE_PORT2PORT SET RANG_FREIGABE_ORIG_OLD = '9' WHERE RANG_FREIGABE_ORIG_OLD = 'in_Aufbau';


UPDATE T_HW_BG_CHANGE_PORT2PORT SET RANG_FREIGABE_ORIG_NEW = '0' WHERE RANG_FREIGABE_ORIG_NEW = 'gesperrt';
UPDATE T_HW_BG_CHANGE_PORT2PORT SET RANG_FREIGABE_ORIG_NEW = '1' WHERE RANG_FREIGABE_ORIG_NEW = 'freigegeben';
UPDATE T_HW_BG_CHANGE_PORT2PORT SET RANG_FREIGABE_ORIG_NEW = '2' WHERE RANG_FREIGABE_ORIG_NEW = 'deactivated';
UPDATE T_HW_BG_CHANGE_PORT2PORT SET RANG_FREIGABE_ORIG_NEW = '4' WHERE RANG_FREIGABE_ORIG_NEW = 'Backbone_down';
UPDATE T_HW_BG_CHANGE_PORT2PORT SET RANG_FREIGABE_ORIG_NEW = '5' WHERE RANG_FREIGABE_ORIG_NEW = 'WEPLA';
UPDATE T_HW_BG_CHANGE_PORT2PORT SET RANG_FREIGABE_ORIG_NEW = '7' WHERE RANG_FREIGABE_ORIG_NEW = 'defekt';
UPDATE T_HW_BG_CHANGE_PORT2PORT SET RANG_FREIGABE_ORIG_NEW = '9' WHERE RANG_FREIGABE_ORIG_NEW = 'in_Aufbau';
