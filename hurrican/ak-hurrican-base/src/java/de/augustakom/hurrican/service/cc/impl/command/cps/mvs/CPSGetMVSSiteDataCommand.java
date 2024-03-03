/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.01.2012 14:54:46
 */
package de.augustakom.hurrican.service.cc.impl.command.cps.mvs;

import java.util.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.common.service.exceptions.ServiceCommandException;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.exceptions.NoDataFoundException;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.annotation.CcTxMandatory;
import de.augustakom.hurrican.model.billing.Leistung;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragMVSEnterprise;
import de.augustakom.hurrican.model.cc.AuftragMVSSite;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSMVSData;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSMVSSiteData;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSMVSSiteData.Number;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSServiceOrderData;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.cc.AvailabilityService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.MVSService;

/**
 * Command zur Ermittlung der MVS Site Daten fuer die CPS-Provisionierung.
 */
public class CPSGetMVSSiteDataCommand extends AbstractGetMVSDataCommand {

    private static final Logger LOGGER = Logger.getLogger(CPSGetMVSSiteDataCommand.class);

    private Long enterpriseId;
    private Long resellerId;
    private String username;
    private String password;
    private String subdomain;
    private List<Number> numbers;
    private Pair<Integer, String> asbAndOnkz;
    private String location;
    private Long channels;

    private Long ccAuftragId;

    private AuftragMVSSite siteAuftrag;
    private AuftragMVSEnterprise epAuftrag;


    @Override
    @CcTxMandatory
    public Object execute() throws HurricanServiceCommandException {
        String resultMsg = null;
        int cmdResult = ServiceCommandResult.CHECK_STATUS_OK;

        try {
            collectData();

            final CPSMVSSiteData epData = new CPSMVSSiteData(resellerId, enterpriseId, username, password, subdomain,
                    numbers, location, channels);
            final CPSMVSData mvsData = new CPSMVSData(epData);
            final CPSServiceOrderData soData = getServiceOrderData();

            soData.setOnkz(getAsbAndOnkz().getSecond());
            soData.setAsb(getAsbAndOnkz().getFirst());
            soData.setMvs(mvsData);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            cmdResult = ServiceCommandResult.CHECK_STATUS_INVALID;
            resultMsg = e.getMessage();
        }
        return ServiceCommandResult.createCmdResult(cmdResult, resultMsg, getClass());
    }

    @Override
    Long getCCAuftragIdMVSEp() throws FindException, ServiceNotFoundException, HurricanServiceCommandException {
        return getAuftragMVSEnterprise().getAuftragId();
    }

    @Override
    AuftragMVSEnterprise getAuftragMVSEnterprise() throws FindException, ServiceNotFoundException,
            HurricanServiceCommandException {
        if (epAuftrag == null) {
            try {
                epAuftrag = getCCService(MVSService.class).findEnterpriseForSiteAuftragId(getCCAuftragID());
            }
            catch (NoDataFoundException ndfe) {
                throw new HurricanServiceCommandException(String.format(
                        "Es wurden keine MVS Enterprise Auftrag für den techn. Auftrag mit Id %d gefunden!",
                        getAuftragDaten().getAuftragId()), ndfe);
            }
        }
        return epAuftrag;
    }

    List<Number> findNumbers() throws FindException, ServiceNotFoundException, ServiceCommandException {
        List<Rufnummer> rufnummern = getActiveDNs(getAuftragDaten().getAuftragNoOrig());
        if (CollectionUtils.isEmpty(rufnummern)) {
            throw new HurricanServiceCommandException(String.format(
                    "Es wurden keine Rufnummern für den techn. Auftrag mit Id %d gefunden!", getAuftragDaten()
                            .getAuftragId()
            ));
        }
        List<Number> numbers = new ArrayList<>(rufnummern.size());
        for (Rufnummer dn : rufnummern) {
            Number number = new Number();
            number.transferDNData(dn);
            Pair<String, String> blocks = adjustBlockLength(number.getBlockStart(), number.getBlockEnd());
            number.setBlockStart(blocks.getFirst());
            number.setBlockEnd(blocks.getSecond());
            if (StringUtils.isEmpty(number.getLac()) || !number.getLac().equals(getAsbAndOnkz().getSecond())) {
                throw new HurricanServiceCommandException(
                        String.format(
                                "Rufnummern hat andere OnKz (%s) als dem Standort des techn. Auftrags mit Id %d (OnKz %s) zugeordnet ist!",
                                dn.getOnKz(), getAuftragDaten().getAuftragId(), getAsbAndOnkz().getSecond())
                );
            }
            numbers.add(number);
        }
        return numbers;
    }

    String findSubdomain() throws NoDataFoundException, HurricanServiceCommandException, FindException,
            ServiceNotFoundException {
        return getCCService(MVSService.class).getQualifiedDomain(getAuftragMVSEnterprise(), getAuftragMVSSite());
    }

    String findPassword() throws FindException, ServiceNotFoundException, HurricanServiceCommandException {
        password = getAuftragMVSSite().getPassword();
        if (StringUtils.isEmpty(password)) {
            throw new HurricanServiceCommandException(String.format(
                    "Password zum MVS Site Auftrag mit techn. Auftrag-Id %d ist nicht gesetzt!", getAuftragDaten()
                            .getAuftragId()
            ));
        }

        return password;
    }

    String findUsername() throws FindException, ServiceNotFoundException, HurricanServiceCommandException {
        String username = getAuftragMVSSite().getUserName();
        if (StringUtils.isEmpty(username)) {
            throw new HurricanServiceCommandException(String.format(
                    "Username zum MVS Site Auftrag mit techn. Auftrag-Id %d ist nicht gesetzt!", getAuftragDaten()
                            .getAuftragId()
            ));
        }
        return username;
    }

    Long findEnterpriseId() throws FindException, ServiceNotFoundException, HurricanServiceCommandException {
        Long epId = getCCAuftragIdMVSEp();
        AuftragDaten adEp = getCCService(CCAuftragService.class).findAuftragDatenByAuftragIdTx(epId);
        if (adEp == null) {
            throw new HurricanServiceCommandException(String.format(
                    "AuftragDaten mit Id %d konnte nicht ermittelt werden", epId));
        }
        return adEp.getAuftragNoOrig();
    }

    Pair<Integer, String> findAsbAndOnkz() throws FindException, ServiceNotFoundException,
            HurricanServiceCommandException {
        final Endstelle endstelle = getCCService(EndstellenService.class).findEndstelle4Auftrag(
                getAuftragDaten().getAuftragId(), Endstelle.ENDSTELLEN_TYP_B);
        if (endstelle == null) {
            throw new HurricanServiceCommandException(String.format(
                    "Keine Endstelle vom Typ B für den techn. Auftrag mit Id %d gefunden", getAuftragDaten()
                            .getAuftragId()
            ));
        }
        final Long geoId = endstelle.getGeoId();

        if (geoId == null) {
            throw new HurricanServiceCommandException(String.format(
                    "Endstelle B mit Id %d ist keine GeoId zuogeordnet!", endstelle.getId()));
        }

        final Pair<Integer, String> asbAndOnkz = getCCService(AvailabilityService.class).findAsbAndOnKzForGeoId(geoId);
        checkOnkzAndAsb(asbAndOnkz);
        return asbAndOnkz;
    }

    Long findChannels() throws FindException, ServiceNotFoundException,
            HurricanServiceCommandException {
        Map<Long, Long> lsCntByMiscNo = findLeistungen(Produkt.PROD_ID_MVS_SITE);

        if ((lsCntByMiscNo.get(Leistung.EXT_MISC_NO_MVS_CHANNELS) == null)
                || NumberTools.equal(lsCntByMiscNo.get(Leistung.EXT_MISC_NO_MVS_CHANNELS), (long) 0)) {
            final Long auftragNoOrig = getAuftragDaten().getAuftragNoOrig();
            throw new HurricanServiceCommandException(String.format(
                    "Es konnten keine Sprachkanäle zum Taifun-Auftrag mit Id %d ermittelt werden!", auftragNoOrig));
        }

        return lsCntByMiscNo.get(Leistung.EXT_MISC_NO_MVS_CHANNELS) * 2L;
    }

    private void checkOnkzAndAsb(Pair<Integer, String> asbAndOnkz) throws HurricanServiceCommandException {
        if ((asbAndOnkz == null) || (asbAndOnkz.getFirst() == null) || (asbAndOnkz.getSecond() == null)) {
            Integer asb = null;
            String onkz = null;
            if (asbAndOnkz != null) {
                asb = asbAndOnkz.getFirst();
                onkz = asbAndOnkz.getSecond();
            }
            throw new HurricanServiceCommandException(
                    String.format(
                            "Es wurde keine ONKZ und/oder ASB zum MVS Site Auftrag mit techn. Auftrag-Id %d gefunden! (onkz=%s, asb=%d)",
                            getAuftragDaten().getAuftragId(), onkz, asb)
            );
        }
    }

    private Pair<Integer, String> getAsbAndOnkz() throws HurricanServiceCommandException, FindException,
            ServiceNotFoundException {
        if (asbAndOnkz == null) {
            asbAndOnkz = findAsbAndOnkz();
        }
        return asbAndOnkz;
    }

    private Long getCCAuftragID() {
        if (ccAuftragId == null) {
            ccAuftragId = getAuftragDaten().getAuftragId();
        }
        return ccAuftragId;
    }

    String findLocation() throws HurricanServiceCommandException, FindException, ServiceNotFoundException {
        // Leeres Standortkuerzel wird ignoriert
        return getAuftragMVSSite().getStandortKuerzel();
    }

    private AuftragMVSSite getAuftragMVSSite() throws FindException, ServiceNotFoundException,
            HurricanServiceCommandException {
        if (siteAuftrag == null) {
            siteAuftrag = getCCService(MVSService.class).findMvsSite4Auftrag(getCCAuftragID(), true);
            if (siteAuftrag == null) {
                throw new HurricanServiceCommandException(String.format(
                        "Es wurde kein MVS Site Auftrag mit techn Auftrag-Id %d gefunden!", getCCAuftragID()));
            }
        }
        return siteAuftrag;
    }

    private void collectData() throws FindException, ServiceNotFoundException, ServiceCommandException {
        resellerId = findResellerId();
        enterpriseId = findEnterpriseId();
        username = findUsername();
        password = findPassword();
        subdomain = findSubdomain();
        asbAndOnkz = getAsbAndOnkz();
        numbers = findNumbers();
        location = findLocation();
        channels = findChannels();
    }
}
