/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.08.13
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

public class VerifyIoArchiveEntryCreatedAction<E extends Enum> extends AbstractWbciTestAction {

    private HistoryService historyService;
    private IOType expectedIoType;
    private GeschaeftsfallTyp expectedGeschaeftsfallTyp;
    private E expectedTyp;
    private int countOfEntries;

    /**
     * checks the IOArchive for the following paramteres
     *
     * @param historyService
     * @param expectedIoType
     * @param expectedGeschaeftsfallTyp Enum of Type {@link de.mnet.wbci.model.GeschaeftsfallTyp}
     * @param expectedTyp               checks typs of WbciMessages, could be an Enum of Typ {@link
     *                                  de.mnet.wbci.model.RequestTyp} or {@link de.mnet.wita.message.MeldungsType}, see
     *                                  also {@link de.mnet.wbci.model.WbciMessage#getTyp()}.
     * @param countOfEntries
     */
    public VerifyIoArchiveEntryCreatedAction(HistoryService historyService, IOType expectedIoType,
            GeschaeftsfallTyp expectedGeschaeftsfallTyp, E expectedTyp, int countOfEntries) {
        super("verifyIoArchiveEntryCreated");
        this.historyService = historyService;
        this.expectedIoType = expectedIoType;
        this.expectedGeschaeftsfallTyp = expectedGeschaeftsfallTyp;
        this.expectedTyp = expectedTyp;
        this.countOfEntries = countOfEntries;
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
            Assert.assertNotNull(archiveEntries, String.format("No archive entries found for %s", vorabstimmungsId));
            Assert.assertTrue(!archiveEntries.isEmpty(), String.format("No archive entries found for %s", vorabstimmungsId));

            Collection<IoArchive> filtered = filterByIoType(archiveEntries);
            filtered = filterByGeschaeftsfall(filtered);
            filtered = filterByMeldungstyp(filtered);

            Assert.assertEquals(filtered.size(), countOfEntries,
                    String.format("Number of IoArchive entries does not match, found %s, expected %s for VorabstimmungsId=%s, IoType=%s, GfTyp=%s, MeldungTyp=%s",
                            filtered.size(), countOfEntries, vorabstimmungsId, expectedIoType, expectedGeschaeftsfallTyp, expectedTyp)
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
                if (expectedIoType != null) {
                    return expectedIoType.name().equals(input.getIoType().name());
                }
                return true;
            }
        });
    }

    private Collection<IoArchive> filterByGeschaeftsfall(Collection<IoArchive> archiveEntries) {
        return Collections2.filter(archiveEntries, new Predicate<IoArchive>() {
            @Override
            public boolean apply(@Nullable IoArchive input) {
                if (expectedGeschaeftsfallTyp != null) {
                    return StringUtils.equals(expectedGeschaeftsfallTyp.name(), input.getRequestGeschaeftsfall());
                }
                return true;
            }
        });
    }

    private Collection<IoArchive> filterByMeldungstyp(Collection<IoArchive> archiveEntries) {
        return Collections2.filter(archiveEntries, new Predicate<IoArchive>() {
            @Override
            public boolean apply(@Nullable IoArchive input) {
                if (expectedTyp != null) {
                    return StringUtils.equals(expectedTyp.toString(), input.getRequestMeldungstyp());
                }
                return true;
            }
        });
    }

}
