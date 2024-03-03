INSERT INTO DN (DN_NO, DN__NO, ORDER__NO, HIST_STATE, HIST_LAST, PRIMARY_INSTANCE,
        VALID_FROM, VALID_TO, ONKZ, DN_BASE, DIRECT_DIAL, RANGE_FROM, RANGE_TO,
        DN_SIZE, ALLOCATED_SIZE, STATE, OE__NO, PORT_MODE, ACT_CARRIER, LAST_CARRIER, FUTURE_CARRIER, USERW, DATEW,
        USE_A_PRIME_AS_GROUP_QUALIFIER, REAL_DATE, USERI, DATEI, NON_BILLABLE)
   VALUES (
      ${dnNo:-NULL},
      ${dnNoOrig:-NULL},
      ${auftragNoOrig:-NULL},
      '${histStatus:-AKT}',
      '${histLast:-1}',
      '${primaryInstance:-0}',
      TO_DATE('${gueltigVon:-01/01/2011 00:00:00}', 'MM/DD/YYYY HH24:MI:SS'),
      TO_DATE('${gueltigBis:-01/01/2011 00:00:00}', 'MM/DD/YYYY HH24:MI:SS'),
      '${onKz}',
      '${dnBase}',
      '${directDial:-NULL}',
      '${rangeFrom:-NULL}',
      '${rangeTo:-NULL}',
      ${dnSize:-1},
      1,
      'ALLOCATED',
      ${oeNoOrig:-60},
      '${portMode:-PORTIERUNG_K}',
      '${actCarrier:-Mnet}',
      '${lastCarrier:-DTAG}',
      '${futureCarrier:-NULL}',
      'TEST',
      sysdate,
      '0',
      TO_DATE('${realDate}', 'MM/DD/YYYY HH24:MI:SS'),
      'TEST',
      sysdate,
      DECODE ('${nonBillable:-false}', 'true', '1',
                                       'TRUE', '1',
                                       '0'))