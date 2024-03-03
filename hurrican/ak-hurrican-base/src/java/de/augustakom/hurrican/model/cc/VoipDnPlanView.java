/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.11.13 07:48
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;
import javax.annotation.*;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

/**
 *
 */
public class VoipDnPlanView extends Observable implements Observer {

    public static final Ordering<VoipDn2DnBlockView> VOIP_DN_BLOCK_VIEW_ORDERING =
            Ordering.natural().onResultOf(new Function<VoipDn2DnBlockView, String>() {
                @Nullable
                @Override
                public String apply(@Nullable final VoipDn2DnBlockView input) {
                    return (input == null) ? null : input.getAnfang();
                }
            }).nullsLast();

    private final VoipDnPlan voipDnPlan;
    private Date gueltigAb;
    private List<VoipDn2DnBlockView> voipDn2DnBlockViews = Lists.newArrayList();

    private String sipLogin;
    private String sipHauptrufnummer;

    public VoipDnPlanView(final String onkz, final String dnBase, final VoipDnPlan voipDnPlan) {
        this.gueltigAb = voipDnPlan.getGueltigAb();
        for (final VoipDnBlock voipDnBlock : voipDnPlan.getVoipDnBlocks()) {
            final VoipDn2DnBlockView voipDn2DnBlockView = new VoipDn2DnBlockView(onkz, dnBase, voipDnBlock);
            voipDn2DnBlockView.addObserver(this);
            this.voipDn2DnBlockViews.add(voipDn2DnBlockView);
        }

        this.sipLogin = voipDnPlan.getSipLogin();
        this.sipHauptrufnummer = voipDnPlan.getSipHauptrufnummer();
        this.voipDnPlan = voipDnPlan;
    }

    public void setGueltigAb(final Date gueltigAb) {
        this.gueltigAb = new Date(gueltigAb.getTime());
    }

    public Date getGueltigAb() {
        return new Date(this.gueltigAb.getTime());
    }

    public List<VoipDn2DnBlockView> getVoipDn2DnBlockViews() {
        return ImmutableList.copyOf(this.voipDn2DnBlockViews);
    }

    public List<VoipDn2DnBlockView> getSortedVoipDn2DnBlockViews() {
        return VOIP_DN_BLOCK_VIEW_ORDERING.immutableSortedCopy(this.voipDn2DnBlockViews);
    }

    public void addVoipDn2BlockView(final VoipDn2DnBlockView view) {
        setChanged();
        view.addObserver(this);
        this.voipDn2DnBlockViews.add(view);
        notifyObservers();
    }

    public void removeVoipDn2BlockView(final VoipDn2DnBlockView view) {
        setChanged();
        this.voipDn2DnBlockViews.remove(view);
        view.deleteObserver(this);
        notifyObservers();
    }

    public VoipDnPlan toEntity() {
        voipDnPlan.setGueltigAb(this.gueltigAb);
        voipDnPlan.setSipLogin(this.sipLogin);
        voipDnPlan.setSipHauptrufnummer(this.sipHauptrufnummer);

        voipDnPlan.clearVoipDnBlocks();
        for (VoipDn2DnBlockView voipDn2DnBlockView : getSortedVoipDn2DnBlockViews()) {
            voipDnPlan.addVoipDnBlock(voipDn2DnBlockView.getVoipDnBlock());
        }
        return voipDnPlan;
    }

    @Override
    public void update(final Observable o, final Object arg) {
        if (o instanceof VoipDn2DnBlockView) {
            setChanged();
            notifyObservers();
        }
    }

    /**
     * @return Returns the sipLogin.
     */
    public String getSipLogin() {
        return sipLogin;
    }

    /**
     * @param sipLogin The sipLogin to set.
     */
    public void setSipLogin(String sipLogin) {
        setChanged();
        this.sipLogin = sipLogin;
        notifyObservers();
    }

    /**
     * @return Returns the sipHauptrufnummer.
     */
    public String getSipHauptrufnummer() {
        return sipHauptrufnummer;
    }

    /**
     * @param sipHauptrufnummer The sipHauptrufnummer to set.
     */
    public void setSipHauptrufnummer(String sipHauptrufnummer) {
        setChanged();
        this.sipHauptrufnummer = sipHauptrufnummer;
        notifyObservers();
    }
}
