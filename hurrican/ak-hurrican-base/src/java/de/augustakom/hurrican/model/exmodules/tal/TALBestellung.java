/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.07.2007 14:38:23
 */
package de.augustakom.hurrican.model.exmodules.tal;


/**
 * Modell fuer die Abbildung einer elektronischen TAL-Bestellung (ueber die DTAG-Schnittstelle von MNETCALL).
 *
 *
 */
public class TALBestellung extends AbstractTALBestellungModel {

    /**
     * Kennzeichnung fuer TAL-Bestellungen aus Augsburg.
     */
    public static final Long SOURCE_ID_AKOM = Long.valueOf(0);

    /**
     * Sender-ID von Carrier 'M-net'
     */
    public static final String SENDER_ID_MNET = "D052-089";

    /**
     * String, um den Wert 'true' anzugeben.
     */
    public static final String TRUE_STRING = "J";
    /**
     * String, um den Wert 'false' anzugeben.
     */
    public static final String FALSE_STRING = "N";

    private Long vorfallId = null;
    private Long sourceId = null;
    private Long firstId = null;
    private Long status = null;
    private String sender = null;
    private String recipient = null;

    /**
     * @see de.augustakom.hurrican.model.exmodules.tal.AbstractTALBestellungModel#getTalBestellungId()
     */
    @Override
    public Long getTalBestellungId() {
        return super.getId();
    }

    /**
     * @see de.augustakom.hurrican.model.exmodules.tal.AbstractTALBestellungModel#setTalBestellungId(java.lang.Long)
     */
    @Override
    public void setTalBestellungId(Long talBestellungId) {
        super.setId(talBestellungId);
    }

    /**
     * @return Returns the vorfallId.
     */
    public Long getVorfallId() {
        return vorfallId;
    }

    /**
     * @param vorfallId The vorfallId to set.
     */
    public void setVorfallId(Long vorfallId) {
        this.vorfallId = vorfallId;
    }

    /**
     * @return Returns the sourceId.
     */
    public Long getSourceId() {
        return sourceId;
    }

    /**
     * @param sourceId The sourceId to set.
     */
    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    /**
     * @return Returns the firstId.
     */
    public Long getFirstId() {
        return firstId;
    }

    /**
     * @param firstId The firstId to set.
     */
    public void setFirstId(Long firstId) {
        this.firstId = firstId;
    }

    /**
     * @return Returns the status.
     */
    public Long getStatus() {
        return status;
    }

    /**
     * @param status The status to set.
     */
    public void setStatus(Long status) {
        this.status = status;
    }

    /**
     * @return Returns the sender.
     */
    public String getSender() {
        return sender;
    }

    /**
     * @param sender The sender to set.
     */
    public void setSender(String sender) {
        this.sender = sender;
    }

    /**
     * @return Returns the recipient.
     */
    public String getRecipient() {
        return recipient;
    }

    /**
     * @param recipient The recipient to set.
     */
    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

}


