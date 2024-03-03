/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.10.14
 */
package de.mnet.hurrican.atlas.simulator.wita.actions;

import java.util.*;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.exceptions.CitrusRuntimeException;
import com.google.common.collect.Collections2;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mnet.common.service.HistoryService;
import de.mnet.wita.message.MeldungsType;
import de.mnet.wita.model.IoArchive;

/**
 * Abstract test action checks for IOArchive entry in database. Action searches for external order id resp. contract id
 * and meldungs type.
 *
 *
 */
public abstract class WaitForIoArchiveEntryAction extends AbstractWitaTestAction {

    protected final HistoryService historyService;
    private final MeldungsType meldungsType;
    private final Long numberOfEntries;

    /** Logger */
    private static final Logger LOG = LoggerFactory.getLogger(WaitForIoArchiveEntryAction.class);

    /**
     * Default constructor using historyService and meldungs type.
     *
     * @param historyService
     * @param meldungsType
     */
    public WaitForIoArchiveEntryAction(String actionName, HistoryService historyService, MeldungsType meldungsType, Long numberOfEntries) {
        super(actionName);
        this.historyService = historyService;
        this.meldungsType = meldungsType;
        this.numberOfEntries = numberOfEntries;
    }

    protected abstract String getIdVariableName();

    protected abstract Collection<IoArchive> findIoArchiveEntries(String id);

    @Override
    public void doExecute(TestContext context) {
        String idVariableName = getIdVariableName();
        String id = context.getVariable(idVariableName);

        String meldungsTypeAsString = meldungsType != null ? meldungsType.getValue() : "null";
        LOG.info(String.format("Searching for io archive entries for %s '%s' and meldungsType '%s'",
                idVariableName, id, meldungsTypeAsString));
        Collection<IoArchive> ioArchiveEntries = findIoArchiveEntries(id);

        Long foundEntries = 0L;
        if (!CollectionUtils.isEmpty(ioArchiveEntries)) {
            ioArchiveEntries = filterByMeldungstyp(ioArchiveEntries, meldungsType);
            foundEntries += ioArchiveEntries.size();
        }

        if (!numberOfEntries.equals(foundEntries)) {
            throw new CitrusRuntimeException(
                    String.format("Failed to read IoArchive entry for %s '%s' and meldungsType '%s' expected %s but " +
                            "got %s entries", idVariableName, id, meldungsTypeAsString, numberOfEntries, foundEntries));
        }
        else {
            LOG.info(String.format("Io archive entry for %s '%s' and meldungsType '%s' found!",
                    idVariableName, id, meldungsTypeAsString));
        }
    }

    private Collection<IoArchive> filterByMeldungstyp(final Collection<IoArchive> archiveEntries,
            final MeldungsType meldungsType) {
        return Collections2.filter(archiveEntries, input -> {
            if (meldungsType == null) {
                return input.getRequestMeldungstyp() == null;
            }
            else {
                return StringUtils.equals(meldungsType.toString(), input.getRequestMeldungstyp())
                        || StringUtils.equals(meldungsType.getLongName(), input.getRequestMeldungstyp());
            }
        });
    }

}
