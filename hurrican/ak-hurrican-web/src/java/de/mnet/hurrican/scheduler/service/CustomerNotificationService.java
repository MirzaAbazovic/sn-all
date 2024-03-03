package de.mnet.hurrican.scheduler.service;

import java.util.*;
import javax.annotation.*;
import com.google.common.collect.ListMultimap;

import de.mnet.hurrican.scheduler.service.utils.SchedulerService;
import de.mnet.wita.message.meldung.Meldung;

public interface CustomerNotificationService extends SchedulerService {

    public ListMultimap<String, Meldung<?>> groupByExterneAuftragId(@Nonnull List<Meldung<?>> listIn);

    public List<Meldung<?>> findOffeneMeldungenSms();

    public List<Meldung<?>> findOffeneMeldungenEmail();

    public void sendSmsForGroup(@Nonnull List<Meldung<?>> meldungen) throws Exception;

    public void sendEmailForGroup(@Nonnull List<Meldung<?>> meldungen) throws Exception;
}
