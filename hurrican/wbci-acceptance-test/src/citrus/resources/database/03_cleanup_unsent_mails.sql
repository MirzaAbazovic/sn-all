update t_mail set sent_at = CREATED_AT, subject = concat('CLEAN-UP: ', subject) where sent_at is null;
