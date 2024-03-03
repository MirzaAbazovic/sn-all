/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.06.2011 09:55:52
 */
package de.augustakom.hurrican.gui.tools.tal.ioarchive;

import java.awt.*;
import javax.swing.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.netbeans.swing.outline.RenderDataProvider;

import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.MeldungTyp;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wita.IOArchiveProperties.IOSource;
import de.mnet.wita.message.MeldungsType;
import de.mnet.wita.model.IoArchive;

public class IoArchiveRenderData implements RenderDataProvider {
    private static final Logger LOGGER = Logger.getLogger(IoArchiveRenderData.class);

    @Override
    public Color getBackground(Object node) {
        return null;
    }

    @Override
    public String getDisplayName(Object node) {
        IoArchive ioArchive = (IoArchive) node;
        // FOR WBCI
        if (IOSource.WBCI.equals(ioArchive.getIoSource())) {
            RequestTyp requestTyp = RequestTyp.buildFromName(ioArchive.getRequestMeldungstyp());
            // for Requests
            if (!requestTyp.equals(RequestTyp.UNBEKANNT)) {
                if (requestTyp.isVorabstimmung()) {
                    return GeschaeftsfallTyp.buildFromName(ioArchive.getRequestGeschaeftsfall()).getShortName();
                }
                return requestTyp.getShortName();
            }
            // for the other types
            else {
                return MeldungTyp.buildFromShortName(ioArchive.getRequestMeldungstyp()).getShortName();
            }
        }
        // FOR WITA ...
        else if (IOSource.WITA.equals(ioArchive.getIoSource())) {
            if (StringUtils.isNotBlank(ioArchive.getRequestMeldungstyp())) {
                return ioArchive.getRequestMeldungstyp();
            }
            return de.mnet.wita.message.GeschaeftsfallTyp.valueOf(ioArchive.getRequestGeschaeftsfall())
                    .getDisplayName();
        }

        LOGGER.warn(String.format("IoArchive entry contains unsupported or missing IoSource - %s",
                ioArchive.getIoSource()));
        return "Unbekannt";
    }

    @Override
    public Color getForeground(Object node) {
        return null;
    }

    @Override
    public Icon getIcon(Object node) {
        return null;
    }

    @Override
    public String getTooltipText(Object node) {
        IoArchive ioArchive = (IoArchive) node;
        if (IOSource.WITA == ioArchive.getIoSource()) {
            return getWitaTooltip(ioArchive);
        }
        else if (IOSource.WBCI == ioArchive.getIoSource()) {
            return getWbciTooltip(ioArchive);
        }
        return null;
    }

    private String getWitaTooltip(IoArchive ioArchive) {
        if (StringUtils.isNotBlank(ioArchive.getRequestMeldungstyp())
                && !ioArchive.isStorno() && !ioArchive.isTerminverschiebung()) {
            return MeldungsType.of(ioArchive.getRequestMeldungstyp()).getLongName();
        }
        return null;
    }

    private String getWbciTooltip(IoArchive ioArchive) {
        RequestTyp requestTyp = RequestTyp.buildFromName(ioArchive.getRequestMeldungstyp());
        // for Requests
        if (!requestTyp.equals(RequestTyp.UNBEKANNT)) {
            if (requestTyp.isVorabstimmung()) {
                return GeschaeftsfallTyp.buildFromName(ioArchive.getRequestGeschaeftsfall()).getLongName();
            }
            return requestTyp.getLongName();
        }
        // for the other types
        else {
            return MeldungTyp.buildFromShortName(ioArchive.getRequestMeldungstyp()).getLongName();
        }
    }

    @Override
    public boolean isHtmlDisplayName(Object node) {
        return false;
    }

}
