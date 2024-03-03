/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.08.13
 */
package de.mnet.wbci.model.builder;

import static de.mnet.wbci.model.RufnummernportierungVO.*;

import java.util.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import de.augustakom.hurrican.model.billing.Rufnummer;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.RufnummerOnkz;
import de.mnet.wbci.model.Rufnummernblock;
import de.mnet.wbci.model.Rufnummernportierung;
import de.mnet.wbci.model.RufnummernportierungAnlage;
import de.mnet.wbci.model.RufnummernportierungAware;
import de.mnet.wbci.model.RufnummernportierungEinzeln;
import de.mnet.wbci.model.RufnummernportierungVO;
import de.mnet.wbci.model.UebernahmeRessourceMeldung;
import de.mnet.wbci.model.WbciRequest;

/**
 * Builder-Klasse, um {@code RufnummernportierungVO} Objekte zu generieren. <br> <br> Neben der einfachen Transformation
 * von WBCI zu VO wird auch noch ein Vergleich durchgefuehrt, welche der angegebenen WBCI Meldungs-Rufnummern auch in
 * dem WBCI-Request angegeben waren. Die im Request nicht enthaltenen Rufnummern werden dabei markiert.
 */
public class RufnummernportierungVOBuilder {

    private Rufnummernportierung rufnummernportierungMeldung;
    private WbciRequest wbciRequest;
    private Collection<Rufnummer> rufnummern;
    private UebernahmeRessourceMeldung akmTr;

    public RufnummernportierungVOBuilder withWbciRequest(WbciRequest wbciRequest) {
        this.wbciRequest = wbciRequest;
        return this;
    }

    public RufnummernportierungVOBuilder withRufnummernportierungFromMeldung(Rufnummernportierung rufnummernportierung) {
        this.rufnummernportierungMeldung = rufnummernportierung;
        return this;
    }

    public RufnummernportierungVOBuilder withRufnummern(Collection<Rufnummer> rufnummern) {
        this.rufnummern = rufnummern;
        return this;
    }

    public RufnummernportierungVOBuilder withAkmTr(UebernahmeRessourceMeldung akmTr) {
        this.akmTr = akmTr;
        return this;
    }

    /**
     * @return eine Liste an {@link RufnummernportierungVO}-Objekten und konsolidiert diese gegebenenfalls mit der
     * Rufnummerportierung der angegebenen Meldung (siehe {@link #withRufnummernportierungFromMeldung(Rufnummernportierung)}).
     */
    public List<RufnummernportierungVO> build() {
        List<RufnummernportierungVO> requestVOs = new ArrayList<>();
        if (wbciRequest != null && wbciRequest.getWbciGeschaeftsfall() instanceof RufnummernportierungAware) {
            RufnummernportierungAware request = (RufnummernportierungAware) wbciRequest.getWbciGeschaeftsfall();
            requestVOs = buildInitialRufnummernportierungVOList(request.getRufnummernportierung());
        }
        List<RufnummernportierungVO> meldungVOs = buildInitialRufnummernportierungVOList(rufnummernportierungMeldung);

        List<RufnummernportierungVO> consolidatedVOs = consolidateAndUpdateStates(requestVOs, meldungVOs);
        updatePkiAuf(consolidatedVOs);
        return consolidatedVOs;
    }

    void updatePkiAuf(List<RufnummernportierungVO> rufnummernportierungVOs) {
        if (CollectionUtils.isNotEmpty(rufnummernportierungVOs) && CollectionUtils.isNotEmpty(rufnummern)) {
            for (RufnummernportierungVO rufnummernportierungVO : rufnummernportierungVOs) {
                Rufnummer rn = lookupRufnummer(rufnummernportierungVO);
                if (rn != null) {
                    // falls M-net abgebender EKP ist, dann FUTURE_CARRIER verwenden; sonst ACT_CARRIER
                    String pkiAuf = (CarrierCode.MNET.equals(wbciRequest.getWbciGeschaeftsfall().getAbgebenderEKP()))
                            ? rn.getFutureCarrierPortKennung()
                            : rn.getActCarrierPortKennung();

                    rufnummernportierungVO.setPkiAuf(pkiAuf);
                    rufnummernportierungVO.checkPkiAufMatch(akmTr);
                }
            }
        }
    }

    /**
     * Using the supplied {@code rnVo} iterates through the list of {@link #rufnummern} looking for a matching {@link
     * de.augustakom.hurrican.model.billing.Rufnummer}.
     *
     * @param rnVo
     * @return the matching rufnummer or null, if no match was found
     */
    private Rufnummer lookupRufnummer(RufnummernportierungVO rnVo) {
        for (Rufnummer rn : rufnummern) {
            if (rn.getDnBase().equals(rnVo.getDnBase()) && rn.getOnKzWithoutLeadingZeros().equals(rnVo.getOnkz())) {
                if (rn.isBlock()) {
                    if (StringUtils.equals(rnVo.getBlockFrom(), rn.getRangeFrom())
                            && StringUtils.equals(rnVo.getBlockTo(), rn.getRangeTo())) {
                        return rn;
                    }
                }
                else {
                    return rn;
                }
            }
        }
        return null;
    }

    /**
     * Konsolidiert die {@link RufnummernportierungVO}-Objekte des Requests mit den VO-Objekten der Meldung und liefert
     * eine neue Liste mit den aktualisierten Stati zurück.
     *
     * @param requestVOs Liste {@link de.mnet.wbci.model.RufnummernportierungVO}-Objekten die aus dem {@link
     *                   de.mnet.wbci.model.WbciRequest} stammen.
     * @param meldungVOs Liste {@link de.mnet.wbci.model.RufnummernportierungVO}-Objekten die aus einer {@link
     *                   de.mnet.wbci.model.Meldung} stammen.
     */
    List<RufnummernportierungVO> consolidateAndUpdateStates(List<RufnummernportierungVO> requestVOs,
            List<RufnummernportierungVO> meldungVOs) {

        // if there are VOs from the RUEM-VA
        if (CollectionUtils.isNotEmpty(meldungVOs)) {
            List<RufnummernportierungVO> consolidatedVOs = new ArrayList<>();
            // add all VOs and update the state
            for (RufnummernportierungVO ruemVaVo : meldungVOs) {
                if (doesExistsInList(ruemVaVo, requestVOs)) {
                    ruemVaVo.setStatusInfo(StatusInfo.BESTAETIGT);
                }
                else {
                    ruemVaVo.setStatusInfo(StatusInfo.NEU_AUS_RUEM_VA);
                }
                consolidatedVOs.add(ruemVaVo);
            }

            // add the missing VOs and update the state in NICHT_IN_RUEM_VA
            for (RufnummernportierungVO requestVo : requestVOs) {
                if (!doesExistsInList(requestVo, consolidatedVOs)) {
                    requestVo.setStatusInfo(StatusInfo.NICHT_IN_RUEM_VA);
                    consolidatedVOs.add(requestVo);
                }
            }
            return consolidatedVOs;
        }
        // else set the initial state for the requested VOs
        for (RufnummernportierungVO requestVo : requestVOs) {
            requestVo.setStatusInfo(RufnummernportierungVO.StatusInfo.ANGEFRAGT);
        }
        return requestVOs;
    }

    /**
     * Erstellt aus der angegebenen {@code Rufnummernportierung} die entsprechenden {@code RufnummernportierungVO}
     * Objekte und liefert sie als Liste zurück.
     */
    private List<RufnummernportierungVO> buildInitialRufnummernportierungVOList(
            Rufnummernportierung rufnummernportierung) {
        List<RufnummernportierungVO> result = new ArrayList<>();
        if (rufnummernportierung != null) {
            if (rufnummernportierung instanceof RufnummernportierungAnlage) {
                flatRufnummernportierungAnlage((RufnummernportierungAnlage) rufnummernportierung, result);
            }
            else if (rufnummernportierung instanceof RufnummernportierungEinzeln) {
                flatRufnummernportierungEinzeln((RufnummernportierungEinzeln) rufnummernportierung, result);
            }
        }
        return result;
    }

    private void flatRufnummernportierungAnlage(RufnummernportierungAnlage rnpAnlage,
            List<RufnummernportierungVO> flattedList) {
        if (rnpAnlage.getRufnummernbloecke() != null) {
            for (Rufnummernblock block : rnpAnlage.getRufnummernbloecke()) {
                RufnummernportierungVO vo = new RufnummernportierungVO();
                vo.setOnkz(rnpAnlage.getOnkz());
                vo.setDnBase(rnpAnlage.getDurchwahlnummer());
                vo.setDirectDial(rnpAnlage.getAbfragestelle());
                vo.setBlockFrom(block.getRnrBlockVon());
                vo.setBlockTo(block.getRnrBlockBis());
                vo.setPkiAbg(block.getPortierungskennungPKIabg());
                flattedList.add(vo);
            }
        }
    }

    private void flatRufnummernportierungEinzeln(RufnummernportierungEinzeln rnpEinzeln,
            List<RufnummernportierungVO> flattedList) {
        if (rnpEinzeln.getRufnummernOnkz() != null) {
            for (RufnummerOnkz rnrOnkz : rnpEinzeln.getRufnummernOnkz()) {
                RufnummernportierungVO vo = new RufnummernportierungVO();
                vo.setOnkz(rnrOnkz.getOnkz());
                vo.setDnBase(rnrOnkz.getRufnummer());
                vo.setPkiAbg(rnrOnkz.getPortierungskennungPKIabg());
                flattedList.add(vo);
            }
        }
    }

    private boolean doesExistsInList(RufnummernportierungVO element, List<RufnummernportierungVO> list) {
        for (RufnummernportierungVO voCompare : list) {
            if (RufnummernportierungVO.doesExist.compare(element, voCompare) == 0) {
                return true;
            }
        }
        return false;
    }

}
