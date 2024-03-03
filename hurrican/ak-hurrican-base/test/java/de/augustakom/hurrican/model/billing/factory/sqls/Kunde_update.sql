Update CUSTOMER SET
  POSTAL_ADDR_NO = ${postalAddrNo:-NULL},
  CSR_NO         = ${kundenbetreuerNo:-NULL}
  where
    CUST_NO = ${kundeNo:-NULL}
