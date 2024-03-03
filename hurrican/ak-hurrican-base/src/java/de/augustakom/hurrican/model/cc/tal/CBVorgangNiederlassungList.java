/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.11.2011 10:46:59
 */
package de.augustakom.hurrican.model.cc.tal;

import java.util.*;
import com.google.common.collect.Ordering;


public class CBVorgangNiederlassungList {

    private List<CBVorgangNiederlassung> list = new ArrayList<CBVorgangNiederlassung>();

    public CBVorgangNiederlassungList(List<CBVorgangNiederlassung> list) {
        super();
        this.list = list;
    }

    /**
     * Sortiert die uebergebene Liste von {@link CBVorgangNiederlassung} Objekten. Dabei werden Eintraege mit gesetztem
     * {@link CBVorgangNiederlassung#getSecondAbmReceived()} an den Anfang der Liste gesetzt.
     */
    public void sortOpenCbVorgaenge() {

        Ordering<CBVorgangNiederlassung> prioOrdering = Ordering.natural()
                .onResultOf(CBVorgangNiederlassung.GET_PRIO).reverse();
        Ordering<CBVorgangNiederlassung> dateOrdering = Ordering.natural().onResultOf(
                CBVorgangNiederlassung.GET_VORGABE_MNET);
        Ordering<CBVorgangNiederlassung> prioAuftragsKlammer = Ordering.natural().onResultOf(
                CBVorgangNiederlassung.GET_AUFTRAGSKLAMMER);
        Ordering<CBVorgangNiederlassung> prioCbVorgangId = Ordering.natural().onResultOf(
                CBVorgangNiederlassung.GET_CB_VORGANG_ID);

        Ordering<CBVorgangNiederlassung> prioAndVorgabeOrdering = prioOrdering.compound(dateOrdering).compound(
                prioAuftragsKlammer).compound(prioCbVorgangId);

        Collections.sort(getList(), prioAndVorgabeOrdering);
    }

    public void setAuftragsKlammerSymbole() {
        Long lastKlammer = null;
        for (CBVorgangNiederlassung current : list) {
            if (current.getAuftragsKlammer() != null) {
                if (current.getAuftragsKlammer().equals(lastKlammer)) {
                    current.setAuftragsKlammerSymbol("--»");
                }
                else {
                    current.setAuftragsKlammerSymbol("[»]");
                }
                lastKlammer = current.getAuftragsKlammer();
            }
        }
    }

    public List<CBVorgangNiederlassung> getList() {
        return list;
    }

}


