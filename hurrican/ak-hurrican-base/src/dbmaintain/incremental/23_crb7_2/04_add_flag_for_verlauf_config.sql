alter table T_BA_VERL_CONFIG add CPS_NECESSARY CHAR(1) DEFAULT '0' NOT NULL;
comment on column T_BA_VERL_CONFIG.CPS_NECESSARY
  is 'Flag gibt an, dass die automatische Verteilung nur dann zulaessig ist, wenn der Auftrag CPS-faehig ist.';
