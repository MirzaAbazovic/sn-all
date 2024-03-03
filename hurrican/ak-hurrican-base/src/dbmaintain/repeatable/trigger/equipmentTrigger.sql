create or replace trigger TRBIU_EQUIPMENT BEFORE INSERT or UPDATE on T_EQUIPMENT
for each row
  begin
    if (:NEW.RANG_SCHNITTSTELLE is not null and :NEW.RANG_SCHNITTSTELLE not in ('LWL', 'FTTB_LZV', 'H', 'N', 'CFV', 'FTTB_MNET', 'FTTB_LVT', 'FttB'))
    then
      raise_application_error(-20000, 'RANG_SCHNITTSTELLE is not valid');
    end if;
    
    if (:NEW.STATUS is not null and :NEW.STATUS not in ('abgebaut', 'locked', 'res', 'vorb', 'defekt', 'frei', 'rang', 'WEPLA'))
    then
      raise_application_error(-20000, 'STATUS is not valid');
    end if;
    
    if (:NEW.UETV is not null and :NEW.UETV not in ('H01', 'H02', 'H03', 'H04', 'H05', 'H07', 'H08', 'H11', 'H13', 'H16', 'H18', 'LWL', 'N01'))
    then
      raise_application_error(-20000, 'UETV is not valid');
    end if;
    
    if (:NEW.VERWENDUNG is not null and :NEW.VERWENDUNG not in ('CONNECT', 'STANDARD'))
    then
      raise_application_error(-20000, 'VERWENDUNG is not valid');
    end if;
    
    if (:NEW.SCHICHT2_PROTOKOLL is not null and :NEW.SCHICHT2_PROTOKOLL not in ('ATM', 'EFM'))
    then
      raise_application_error(-20000, 'SCHICHT2_PROTOKOLL is not valid');
    end if;
    
  end;
/
