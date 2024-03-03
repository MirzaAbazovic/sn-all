ALTER TABLE T_WITA_CONFIG MODIFY (CONFIG_KEY VARCHAR2(128));

INSERT INTO T_WITA_CONFIG
     VALUES (22,
             'wita.count.of.days.before.sent.KUENDIGUNG_KUNDE',
             '10',
             SYSDATE,
             1);

INSERT INTO T_WITA_CONFIG
     VALUES (
               23,
               'wita.count.of.days.before.sent.RUFNUMMERNEXPORT_ANSCHLUSSKUENDIGUNG',
               '120',
               SYSDATE,
               1);

UPDATE T_WITA_CONFIG
   SET CONFIG_VALUE = '60'
 WHERE CONFIG_KEY = 'wita.count.of.days.before.sent';
