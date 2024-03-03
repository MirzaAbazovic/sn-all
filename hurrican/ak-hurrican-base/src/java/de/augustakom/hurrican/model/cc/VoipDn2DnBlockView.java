/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.11.13 09:51
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;

/**
 *
 */
public class VoipDn2DnBlockView extends Observable {

    public VoipDn2DnBlockView(final String onkz, final String dnBase) {
        this(onkz, dnBase, new VoipDnBlock());
    }

    public VoipDn2DnBlockView(final String onkz, final String dnBase, final VoipDnBlock voipDnBlock) {
        this.onkz = onkz;
        this.dnBase = dnBase;
        this.voipDnBlock = voipDnBlock;
    }

    private VoipDnBlock voipDnBlock;
    private final String onkz;
    private final String dnBase;

    public String getAnfang() {
        return voipDnBlock.getAnfang();
    }

    public String getEnde() {
        return voipDnBlock.getEnde();
    }

    public boolean getZentrale() {
        return voipDnBlock.getZentrale();
    }

    public void setAnfang(final String start) {
        setChanged();
        voipDnBlock.setAnfang(start);
        notifyObservers();
    }

    public void setZentrale(final boolean zentrale) {
        setChanged();
        voipDnBlock.setZentrale(zentrale);
        notifyObservers();
    }

    public void setEnde(final String ende) {
        setChanged();
        voipDnBlock.setEnde(ende);
        notifyObservers();
    }

    public String getOnkz() {
        return onkz;
    }

    public String getDnBase() {
        return dnBase;
    }

    protected VoipDnBlock getVoipDnBlock() {
        return voipDnBlock;
    }

    @Override
    public int hashCode() {
        return Objects.hash(voipDnBlock, onkz, dnBase);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final VoipDn2DnBlockView other = (VoipDn2DnBlockView) obj;
        return Objects.equals(this.voipDnBlock, other.voipDnBlock) && Objects.equals(this.onkz, other.onkz)
                && Objects.equals(this.dnBase, other.dnBase);
    }

    @Override
    public String toString() {
        return getAnfang() + ((getEnde() != null) ? "-" + getEnde() : "");
    }
}
