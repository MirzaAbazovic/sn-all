update t_ffm_qualification set ffm_qualification='Interne Arbeit' where ffm_qualification='interne Arbeit';

alter table t_ffm_feedback_material modify serial_number null;
