-- SIP_LOGIN setzt sich zusammen aus:
--          +49 [ONKZ][DN-Base][Range from/Start]@[SIP Domain]
-- VarChar  3    10    10       5          1 100
alter table T_AUFTRAG_VOIP_DN add SIP_LOGIN VARCHAR2(130);

-- SIP_HAUPTRUFNR setzt sich zusammen aus:
--          +49 [ONKZ][DN-Base][Range from/Start]+Zentrale(Flag)
-- VarChar  3    10    10       5          1
alter table T_AUFTRAG_VOIP_DN add SIP_HAUPTRUFNUMMER VARCHAR2(30);
