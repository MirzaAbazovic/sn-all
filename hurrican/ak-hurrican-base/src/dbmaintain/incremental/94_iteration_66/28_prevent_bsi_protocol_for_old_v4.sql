update T_MWF_REQUEST req set REQ.SENT_TO_BSI='DONT_SEND_TO_BSI' where REQ.SENT_TO_BSI='ERROR_SEND_TO_BSI' and REQ.WITA_VERSION=4;
update T_MWF_MELDUNG m set m.SENT_TO_BSI='DONT_SEND_TO_BSI' where m.SENT_TO_BSI='ERROR_SEND_TO_BSI' and m.WITA_VERSION=4;