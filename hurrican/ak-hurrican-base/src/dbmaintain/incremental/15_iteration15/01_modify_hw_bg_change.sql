alter table T_HW_BG_CHANGE add CLOSED_AT DATE;
alter table T_HW_BG_CHANGE add CLOSED_FROM VARCHAR2(25);

comment on column T_HW_BG_CHANGE.CLOSED_AT is 'Datum, zu wann die Planung geschlossen wurde';
comment on column T_HW_BG_CHANGE.CLOSED_FROM is 'User, der die Planung geschlossen hat';

comment on column T_HW_BG_CHANGE.EXECUTED_AT is 'Datum, zu wann der Baugruppen-Schwenk ausgefuehrt wurden / aktiv gesetzt wurden';
comment on column T_HW_BG_CHANGE.EXECUTED_FROM is 'User, der den Baugruppen-Schwenk ausgefuehrt hat';

comment on column T_HW_BG_CHANGE.CANCELLED_AT is 'Datum, zu dem der Baugruppen-Schwenk storniert wurde';
comment on column T_HW_BG_CHANGE.CANCELLED_FROM is 'User, der den Baugruppen-Schwenk storniert hat';

comment on column T_HW_BG_CHANGE.PLANNED_DATE is 'Datum, zu dem der Baugruppen-Schwenk geplant ist';
comment on column T_HW_BG_CHANGE.PLANNED_FROM is 'User, der den Baugruppen-Schwenk geplant hat';
