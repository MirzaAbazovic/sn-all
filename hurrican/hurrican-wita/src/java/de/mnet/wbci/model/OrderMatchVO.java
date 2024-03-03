/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.03.14
 */
package de.mnet.wbci.model;

import java.io.*;
import java.util.*;
import javax.validation.constraints.*;

/**
 * VO fuer die Angabe, welcher Taifun Auftrag zu einer WBCI VA matched bzw. bei welchen Parametern die Daten nicht
 * passen.
 */
public class OrderMatchVO implements Serializable {

    private static final long serialVersionUID = -9084007972391496485L;

    /**
     * Enum definiert, ueber welche Parameter die Basis-Liste der Auftraege ermittelt wurden.
     */
    public enum BasicSearch {
        /**
         * Basis-Suche ging ueber Rufnummern.
         */
        DN("Rufnummer"),
        /**
         * Basis-Suche ging ueber den Standort.
         */
        LOCATION("Standort");

        public String searchName;

        BasicSearch(String searchName) {
            this.searchName = searchName;
        }
    }


    /**
     * Enum definiert, welche Parameter fuer eine Auftragsermittlung nicht(!) matchen
     */
    public enum MatchViolation {
        /**
         * Standort des Auftrags matched nicht.
         */
        LOCATION("Standort"),
        /**
         * Kundenname zum Auftrag matched nicht.
         */
        NAME("Kundenname"),
        /**
         * Rufnummer des Auftrags matched nicht.
         */
        DN("Rufnummer");

        public String violate;

        MatchViolation(String violate) {
            this.violate = violate;
        }
    }

    public OrderMatchVO(Long orderNoOrig) {
        this.orderNoOrig = orderNoOrig;
    }

    private Long orderNoOrig;
    private BasicSearch basicSearch;
    private Set<MatchViolation> matchViolations;

    public Long getOrderNoOrig() {
        return orderNoOrig;
    }

    public void setOrderNoOrig(Long orderNoOrig) {
        this.orderNoOrig = orderNoOrig;
    }

    public BasicSearch getBasicSearch() {
        return basicSearch;
    }

    public void setBasicSearch(BasicSearch basicSearch) {
        this.basicSearch = basicSearch;
    }

    public Set<MatchViolation> getMatchViolations() {
        return matchViolations;
    }

    public void setMatchViolations(Set<MatchViolation> matchViolations) {
        this.matchViolations = matchViolations;
    }

    public void addMatchViolation(MatchViolation matchViolation) {
        if (matchViolations == null) {
            matchViolations = new HashSet<>();
        }
        matchViolations.add(matchViolation);
    }

    @Override
    public String toString() {
        return "OrderMatchVO{" +
                "orderNoOrig=" + orderNoOrig +
                ", basicSearch=" + basicSearch +
                ", matchViolations=" + matchViolations +
                '}';
    }

    /**
     * Generiert aus aus den angegebenen {@code orderNoOrigs} eine Liste von {@code OrderMatchVO}s.
     *
     * @param orderNoOrigs Liste mit den Auftragsnummern
     * @param basicSearch  Angabe, ueber welche Such-Strategie die Auftraege ermittelt wurden
     * @return Liste mit den generierten {@code OrderMatchVO}s
     */
    public static Collection<OrderMatchVO> createOrderMatches(@NotNull Set<Long> orderNoOrigs, @NotNull BasicSearch basicSearch) {
        List<OrderMatchVO> orderMatches = new ArrayList<>();
        for (Long orderNo : orderNoOrigs) {
            OrderMatchVO vo = new OrderMatchVO(orderNo);
            vo.setBasicSearch(basicSearch);
            orderMatches.add(vo);
        }
        return orderMatches;
    }

    public static Collection<OrderMatchVO> addNewOrderMatches(@NotNull Collection<OrderMatchVO> baseOrderMatchVOs,
            @NotNull Collection<OrderMatchVO> newOrderMatchVOs) {
        SortedSet<OrderMatchVO> orderMatchVOs = new TreeSet<>(new Comparator<OrderMatchVO>() {
            @Override
            public int compare(OrderMatchVO o1, OrderMatchVO o2) {
                return o1.getOrderNoOrig().compareTo(o2.getOrderNoOrig());
            }
        });
        orderMatchVOs.addAll(baseOrderMatchVOs);
        orderMatchVOs.addAll(newOrderMatchVOs);
        return orderMatchVOs;
    }

    /**
     * Durchlaeuft die {@code orderMatchVOs} Liste und ermittelt alle Auftraege, die NICHT in {@code filteredOrderNos}
     * enthalten sind. Diese {@link OrderMatchVO}s werden dann mit der angegebenen MatchViolation vesehen.
     *
     * @param orderMatchVOs    aktuelle Treffer
     * @param filteredOrderNos Subset von gefilteren OrderNoOrigs.
     * @param matchViolation   {@link OrderMatchVO.MatchViolation} die zu setzen ist
     */
    public static void violates(@NotNull Collection<OrderMatchVO> orderMatchVOs, @NotNull Set<Long> filteredOrderNos,
            @NotNull MatchViolation matchViolation) {
        for (OrderMatchVO orderMatchVO : orderMatchVOs) {
            if (!filteredOrderNos.contains(orderMatchVO.getOrderNoOrig())) {
                orderMatchVO.addMatchViolation(matchViolation);
            }
        }
    }

    public static Set<Long> getOrderNoOrigs(Collection<OrderMatchVO> orderMatchVOs) {
        Set<Long> filteredOrderNos = new HashSet<>();
        for (OrderMatchVO filteredOrder : orderMatchVOs) {
            filteredOrderNos.add(filteredOrder.getOrderNoOrig());
        }
        return filteredOrderNos;
    }

}
