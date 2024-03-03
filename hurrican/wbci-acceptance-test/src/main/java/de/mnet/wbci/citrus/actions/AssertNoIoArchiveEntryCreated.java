/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.10.13
 */
package de.mnet.wbci.citrus.actions;

import java.util.*;
import javax.annotation.*;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.exceptions.CitrusRuntimeException;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import org.apache.commons.lang.StringUtils;
import org.testng.Assert;

import de.mnet.common.service.HistoryService;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.IOType;
import de.mnet.wita.model.IoArchive;

/**
 * Used for asserting that NO IOArchive entry exists.
 */
public class AssertNoIoArchiveEntryCreated<E extends Enum> extends AbstractWbciTestAction {

    private HistoryService historyService;
    private IOType ioType;
    private GeschaeftsfallTyp geschaeftsfallTyp;
    private E typ;

    /**
     * Verifies that the the IOArchive does not have an entry matching the supplied parameters
     *
     * @param historyService
     * @param ioType
     * @param geschaeftsfallTyp Enum of Type {@link GeschaeftsfallTyp}
     * @param typ               checks typs of WbciMessages, could be an Enum of Typ {@link
     *                          de.mnet.wbci.model.RequestTyp} or {@link de.mnet.wita.message.MeldungsType}, see also
     *                          {@link de.mnet.wbci.model.WbciMessage#getTyp()}.
     */
    public AssertNoIoArchiveEntryCreated(HistoryService historyService, IOType ioType,
            GeschaeftsfallTyp geschaeftsfallTyp, E typ) {
        super("verifyNoIoArchiveEntryCreated");
        this.historyService = historyService;
        this.ioType = ioType;
        this.geschaeftsfallTyp = geschaeftsfallTyp;
        this.typ = typ;
    }

    @Override
    public void doExecute(TestContext context) {
        try {
            final String vorabstimmungsId = getVorabstimmungsId(context);
            if (vorabstimmungsId == null) {
                throw new CitrusRuntimeException(
                        "Variable 'PRE_AGREEMENT_ID_' is null, the test variable hast to be set in the TestContext");
            }

            List<IoArchive> archiveEntries = historyService.findIoArchivesForExtOrderNo(vorabstimmungsId);

            if (archiveEntries == null) {
                // great, nothing found matching the VA ID
                return;
            }

            Collection<IoArchive> filtered = filterByIoType(archiveEntries);
            filtered = filterByGeschaeftsfall(filtered);
            filtered = filterByMeldungstyp(filtered);

            Assert.assertEquals(filtered.size(), 0,
                    String.format("An IoArchive entry was found matching VorabstimmungsId=%s, IoType=%s, GfTyp=%s, MeldungTyp=%s",
                            vorabstimmungsId, ioType, geschaeftsfallTyp, typ)
            );
        }
        catch (AssertionError assertionError) {
            // need to translate assertion errors to CitrusRuntimeException as Citrus will not recognize
            // assertion errors properly (e.g. in repeat-on-error)
            throw new CitrusRuntimeException(assertionError);
        }
    }

    private Collection<IoArchive> filterByIoType(Collection<IoArchive> archiveEntries) {
        return Collections2.filter(archiveEntries, new Predicate<IoArchive>() {
            @Override
            public boolean apply(@Nullable IoArchive input) {
                if (ioType != null) {
                    return ioType.name().equals(input.getIoType().name());
                }
                return true;
            }
        });
    }

    private Collection<IoArchive> filterByGeschaeftsfall(Collection<IoArchive> archiveEntries) {
        return Collections2.filter(archiveEntries, new Predicate<IoArchive>() {
            @Override
            public boolean apply(@Nullable IoArchive input) {
                if (geschaeftsfallTyp != null) {
                    return StringUtils.equals(geschaeftsfallTyp.name(), input.getRequestGeschaeftsfall());
                }
                return true;
            }
        });
    }

    private Collection<IoArchive> filterByMeldungstyp(Collection<IoArchive> archiveEntries) {
        return Collections2.filter(archiveEntries, new Predicate<IoArchive>() {
            @Override
            public boolean apply(@Nullable IoArchive input) {
                if (typ != null) {
                    return StringUtils.equals(typ.toString(), input.getRequestMeldungstyp());
                }
                return true;
            }
        });
    }

}
