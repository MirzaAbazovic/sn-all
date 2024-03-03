/* 
 * Copyright (c) 2013 - M-net Telekommunikations GmbH 
 * All rights reserved. 
 * ------------------------------------------------------- 
 * File created: 06.11.13 
 */
package de.mnet.wbci.ticketing.customerservice.converter;

import java.time.*;
import org.springframework.beans.factory.annotation.Autowired;

import de.mnet.esb.cdm.customer.customerservice.v1.AddCommunication;
import de.mnet.wbci.model.Abbruchmeldung;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.Erledigtmeldung;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.IOType;
import de.mnet.wbci.model.Meldung;
import de.mnet.wbci.model.StornoAnfrage;
import de.mnet.wbci.model.WbciGeschaeftsfallStatus;
import de.mnet.wbci.model.WbciMessage;

/**
 * Basis-Klasse fuer sog. 'Enricher', die ein CustomerService-Protocol mit weiteren Daten anreichern.
 *
 * @param <T>
 */
public abstract class AbstractCustomerServiceProtocolEnricher<T extends WbciMessage> implements CustomerServiceProtocolEnricher<T> {

    @Autowired
    protected InboundMessageCommentGenerator inboundMessageCommentGenerator;

    @Autowired
    protected OutboundMessageCommentGenerator outboundMessageCommentGenerator;

    @Autowired
    protected NewVaExpiredCommentGenerator newVaExpiredCommentGenerator;

    @Override
    public void enrich(T wbciMessage, AddCommunication csProtocol) {
        if (isInbound(wbciMessage)) {
            enrichInboundMessage(wbciMessage, csProtocol);
        }
        else {
            enrichOutboundMessage(wbciMessage, csProtocol);
        }
    }

    /**
     * Methode, um Daten von der ausgehenden WBCI Message {@code wbciMessage} nach {@code csProtocol} zu uebertragen.
     *
     * @param wbciMessage
     * @param csProtocol
     */
    protected abstract void enrichOutboundMessage(T wbciMessage, AddCommunication csProtocol);

    /**
     * Methode, um Daten von der eingehenden WBCI Message {@code wbciMessage} nach {@code csProtocol} zu uebertragen.
     *
     * @param wbciMessage
     * @param csProtocol
     */
    protected abstract void enrichInboundMessage(T wbciMessage, AddCommunication csProtocol);

    /**
     * Returns true if the {@code wbciMessage} is an inbound message (being sent to MNet) otherwise returns false (MNet
     * is sending out the message)
     *
     * @param wbciMessage
     * @return
     */
    protected boolean isInbound(T wbciMessage) {
        return IOType.IN.equals(wbciMessage.getIoType());
    }

    /**
     * Gets the carrier code of the carrier that M-Net is communicating with.
     *
     * @param wbciMessage
     * @return
     */
    protected CarrierCode getOtherPartyCarrierCode(T wbciMessage) {
        CarrierCode abgebenderEKP = wbciMessage.getWbciGeschaeftsfall().getAbgebenderEKP();
        if (!CarrierCode.MNET.equals(abgebenderEKP)) {
            return abgebenderEKP;
        }
        return wbciMessage.getWbciGeschaeftsfall().getAufnehmenderEKP();
    }

    protected GeschaeftsfallTyp getGfTyp(T wbciMessage) {
        return wbciMessage.getWbciGeschaeftsfall().getTyp();
    }

    protected LocalDateTime getKundenwunschtermin(T wbciMessage) {
        return wbciMessage.getWbciGeschaeftsfall().getKundenwunschtermin().atStartOfDay();
    }

    protected String getVaId(T wbciMessage) {
        return wbciMessage.getWbciGeschaeftsfall().getVorabstimmungsId();
    }

    protected String getStornoReqAenderungsId(StornoAnfrage str) {
        return str.getAenderungsId();
    }

    protected String getErledigtMeldungAenderungsId(Erledigtmeldung meldung) {
        return meldung.getAenderungsIdRef();
    }

    protected String getErledigtMeldungStornoId(Erledigtmeldung meldung) {
        return meldung.getStornoIdRef();
    }

    protected String getAbbruchmeldungAenderungsId(Abbruchmeldung meldung) {
        return meldung.getAenderungsIdRef();
    }

    protected String getAbbruchmeldungStornoId(Abbruchmeldung meldung) {
        return meldung.getStornoIdRef();
    }

    protected String getMeldungsText(Meldung meldung) {
        return meldung.getMeldungsTexte();
    }

    protected boolean hasGeschaeftsfallStatus(WbciMessage wbciMessage, WbciGeschaeftsfallStatus status) {
        return status.equals(wbciMessage.getWbciGeschaeftsfall().getStatus());
    }
}
