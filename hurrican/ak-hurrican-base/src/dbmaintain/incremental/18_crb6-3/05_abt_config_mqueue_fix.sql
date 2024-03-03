
update T_BA_VERL_CONFIG set GUELTIG_VON=to_date(to_char(SYSDATE, 'dd.mm.yyyy'), 'dd.mm.yyyy')
  where userw='stollebe';

update T_BA_VERL_CONFIG set GUELTIG_BIS=to_date(to_char(SYSDATE, 'dd.mm.yyyy'), 'dd.mm.yyyy')
  where PROD_ID in (420,421) and anlass=27 and userw<>'stollebe' and GUELTIG_BIS=to_date('01.01.2200', 'dd.mm.yyyy');
