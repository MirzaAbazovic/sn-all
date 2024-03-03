-- For consistency with request_meldungstext

alter table t_io_archive modify (response_meldungstext varchar2(300));