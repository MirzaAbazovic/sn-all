-- Service Command cps.device.data (5004) aus Service Chain CPS - Fttx SDSL (58) entfernen
delete from T_SERVICE_COMMAND_MAPPING where COMMAND_ID=5004 and REF_ID=58;