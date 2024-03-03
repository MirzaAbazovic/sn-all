CREATE OR REPLACE FORCE VIEW V_HW_BG_PORT2PORT_DETAIL
(
   PORT2PORT_ID,
   HW_BG_CHANGE_ID,
   EQ_ID_OLD,
   HW_EQN_OLD,
   RANG_BUCHT_OLD,
   RANG_VERTEILER_OLD,
   RANG_LEISTE1_OLD,
   RANG_STIFT1_OLD,
   EQ_ID_NEW,
   HW_EQN_NEW,
   RANG_BUCHT_NEW,
   RANG_VERTEILER_NEW,
   RANG_LEISTE1_NEW,
   RANG_STIFT1_NEW
)
AS
   SELECT P2P.ID AS PORT2PORT_ID,
          P2P.HW_BG_CHANGE_ID,
          EQ_OLD.EQ_ID AS EQ_ID_OLD,
          EQ_OLD.HW_EQN AS HW_EQN_OLD,
          EQ_OLD.RANG_BUCHT as RANG_BUCHT_OLD,
          EQ_OLD.RANG_VERTEILER as RANG_VERTEILER_OLD,
          EQ_OLD.RANG_LEISTE1 as RANG_LEISTE1_OLD,
          EQ_OLD.RANG_STIFT1 as RANG_STIFT1_OLD,
          EQ_NEW.EQ_ID AS EQ_ID_NEW,
          EQ_NEW.HW_EQN AS HW_EQN_NEW,
          EQ_NEW.RANG_BUCHT as RANG_BUCHT_NEW,
          EQ_NEW.RANG_VERTEILER as RANG_VERTEILER_NEW,
          EQ_NEW.RANG_LEISTE1 as RANG_LEISTE1_NEW,
          EQ_NEW.RANG_STIFT1 as RANG_STIFT1_NEW
     FROM T_HW_BG_CHANGE_PORT2PORT p2p
          LEFT JOIN T_HW_BG_CHANGE hwbgc
             ON p2p.HW_BG_CHANGE_ID = hwbgc.ID
          LEFT JOIN T_EQUIPMENT eq_new
             ON P2P.EQ_ID_NEW = EQ_NEW.EQ_ID
          LEFT JOIN T_EQUIPMENT eq_old
             ON P2P.EQ_ID_OLD = EQ_OLD.EQ_ID
          LEFT JOIN T_RANGIERUNG rang
             ON (((eq_old.EQ_ID = RANG.EQ_IN_ID
                    OR EQ_OLD.EQ_ID = RANG.EQ_OUT_ID) and hwbgc.change_state_ref_id < 22153)
                 or ((EQ_NEW.EQ_ID=RANG.EQ_IN_ID
                        or EQ_NEW.EQ_ID=RANG.EQ_OUT_ID) and hwbgc.change_state_ref_id >= 22153)
             )

    WHERE (rang.gueltig_bis IS NULL OR rang.gueltig_bis > SYSDATE);

comment on table V_HW_BG_PORT2PORT_DETAIL
  is 'View mit Darstellung der Ports (inkl. Leisten/Stifte) fuer Baugruppen-Schwenks. View kann z.B. als Print-Basis verwendet werden';

grant select on V_HW_BG_PORT2PORT_DETAIL to R_HURRICAN_USER;
grant select on V_HW_BG_PORT2PORT_DETAIL to R_HURRICAN_READ_ONLY;
