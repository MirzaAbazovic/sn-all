/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.11.13
 */
package de.augustakom.hurrican.service.cc.impl;

import java.math.*;
import java.util.*;
import java.util.stream.*;
import com.google.common.base.CharMatcher;
import org.apache.commons.lang.StringUtils;

import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.model.cc.VoipDn2DnBlockView;
import de.augustakom.hurrican.model.cc.VoipDnPlanView;
import de.augustakom.hurrican.model.shared.view.voip.AuftragVoipDNView;
import de.augustakom.hurrican.service.cc.VoipDnPlanValidationService;

public class VoipDnPlanValidationServiceImpl extends DefaultCCService implements VoipDnPlanValidationService {

    @Override
    public List<String> allErrorMessages(final VoipDnPlanView plan, final AuftragVoipDNView dnView) {
        List<String> messages = new ArrayList<>();

        for (final VoipDn2DnBlockView block : getNonnumericalBlocks(plan)) {
            messages.add(String.format("Der Rufnummernblock \"%s\" besteht nicht nur aus Ziffern", block.toString()));
        }
        if (isTooLong(plan)) {
            messages.add(String.format("Rufnummernplan enthält mehr als %s Blöcke", MAX_PLAN_LENGTH));
        }
        if (!boundaryMatchesRangeFromTo(plan, dnView)) {
            messages.add("Anfang und Ende des Rufnummernplans passen nicht zu \"Range from\" und \"Range to\" " +
                    "der zugehörigen Rufnummer");
        }
        if (!hasUniqueGueltigAb(plan, dnView)) {
            messages.add(String.format("Der Rufnummer ist bereits ein Rufnummernplan mit dem Gültigkeitszeitpunkt " +
                    "%s zugeordnet", DateTools.formatDate(plan.getGueltigAb(), DateTools.PATTERN_DATE_TIME)));
        }
        if (!hasExactlyOneZentrale(plan)) {
            messages.add(String.format("Rufnummernplan enthält mehr als eine Zentrale"));
        }
        for (final Pair<VoipDn2DnBlockView, VoipDn2DnBlockView> blocks : getBlocksWithGaps(plan)) {
            messages.add(String.format("Rufnummernplan enthält eine Lücke zwischen den Blöcken \"%s\" und \"%s\"",
                    blocks.getFirst().toString(), blocks.getSecond().toString()));
        }
        for (final Pair<VoipDn2DnBlockView, VoipDn2DnBlockView> blocks : getOverlappingBlocks(plan)) {
            messages.add(String.format(
                    "Rufnummernplan enthält eine Überlappung zwischen den Blöcken \"%s\" und \"%s\"",
                    blocks.getFirst().toString(), blocks.getSecond().toString()));
        }
        for (final VoipDn2DnBlockView block : getTooLongBlocks(plan)) {
            messages.add(String.format("ONKz, Durchwahlnummer und Rufnummernblock \"%s\" sind länger als %s Ziffern",
                    block.toString(), MAX_RUFNUMMER_LENGTH));
        }
        for (final VoipDn2DnBlockView block : getBlocksWithInconsistentArity(plan)) {
            messages.add(String.format(
                    "Anfang und Ende des Rufnummernblocks \"%s\" haben eine unterschiedliche Stelligkeit",
                    block.toString()));
        }
        return messages;
    }

    @Override
    public boolean isValid(final VoipDnPlanView plan, final AuftragVoipDNView dnView) {
        return !isTooLong(plan) &&
                boundaryMatchesRangeFromTo(plan, dnView) &&
                hasUniqueGueltigAb(plan, dnView) &&
                hasExactlyOneZentrale(plan) &&
                getBlocksWithGaps(plan).isEmpty() &&
                getOverlappingBlocks(plan).isEmpty() &&
                getTooLongBlocks(plan).isEmpty() &&
                getBlocksWithInconsistentArity(plan).isEmpty() &&
                getNonnumericalBlocks(plan).isEmpty();
    }

    @Override
    public boolean isTooLong(final VoipDnPlanView plan) {
        return plan.getVoipDn2DnBlockViews().size() > MAX_PLAN_LENGTH;
    }

    @Override
    public boolean hasExactlyOneZentrale(final VoipDnPlanView plan) {
        List<VoipDn2DnBlockView> zentrale = new ArrayList<>();
        for (final VoipDn2DnBlockView block : plan.getVoipDn2DnBlockViews()) {
            if (block.getZentrale()) {
                zentrale.add(block);
            }
        }
        return ((zentrale.size() == 1) && (zentrale.get(0).getEnde() == null)) || zentrale.isEmpty() ;
    }

    @Override
    public boolean boundaryMatchesRangeFromTo(final VoipDnPlanView plan, final AuftragVoipDNView dnView) {
        final List<VoipDn2DnBlockView> blocks = plan.getSortedVoipDn2DnBlockViews();
        if (blocks.isEmpty() || dnView == null) {
            return false;
        }

        final VoipDn2DnBlockView firstBlock = blocks.get(0);
        final VoipDn2DnBlockView lastBlock = blocks.get(blocks.size() - 1);

        final String anfang = firstBlock.getAnfang();
        final String ende = getEndeOrAnfang(lastBlock);

        final String extRangeFrom = StringUtils.rightPad(dnView.getRangeFrom(), anfang.length(), '0');
        final String extRangeTo = StringUtils.rightPad(dnView.getRangeTo(), ende.length(), '9');

        return extRangeFrom.startsWith(anfang) && extRangeTo.startsWith(ende);
    }

    @Override
    public boolean hasUniqueGueltigAb(final VoipDnPlanView plan, final AuftragVoipDNView dnView) {
        for (final VoipDnPlanView otherPlan : dnView.getVoipDnPlanViewsDesc()) {
            if (plan != otherPlan && plan.getGueltigAb().equals(otherPlan.getGueltigAb())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public List<Pair<VoipDn2DnBlockView, VoipDn2DnBlockView>> getBlocksWithGaps(final VoipDnPlanView plan) {
        List<Pair<VoipDn2DnBlockView, VoipDn2DnBlockView>> retval = new ArrayList<>();

        final List<VoipDn2DnBlockView> blocks = plan.getSortedVoipDn2DnBlockViews();
        for (int i = 0; i < blocks.size() - 1; i++) {
            final VoipDn2DnBlockView b1 = blocks.get(i);
            final VoipDn2DnBlockView b2 = blocks.get(i + 1);

            if (getEndeOrAnfang(b1).compareTo(b2.getAnfang()) >= 0) {
                continue; // Ignoriere Überlappungen
            }

            final String b1EndeIncr =
                    StringUtils.rightPad(stringPlus(getEndeOrAnfang(b1), 1), b2.getAnfang().length(), '0');
            final String b2Anfang =
                    StringUtils.rightPad(b2.getAnfang(), getEndeOrAnfang(b1).length(), '0');

            if (!b1EndeIncr.equals(b2Anfang)) {
                retval.add(Pair.create(b1, b2));
            }
        }
        return retval;
    }

    @Override
    public List<Pair<VoipDn2DnBlockView, VoipDn2DnBlockView>> getOverlappingBlocks(final VoipDnPlanView plan) {
        List<Pair<VoipDn2DnBlockView, VoipDn2DnBlockView>> retval = new ArrayList<>();

        final List<VoipDn2DnBlockView> blocks = plan.getSortedVoipDn2DnBlockViews();
        for (int i = 0; i < blocks.size() - 1; i++) {
            final VoipDn2DnBlockView b1 = blocks.get(i);
            final VoipDn2DnBlockView b2 = blocks.get(i + 1);

            if (getEndeOrAnfang(b1).compareTo(b2.getAnfang()) >= 0) {
                retval.add(Pair.create(b1, b2));
            }
        }
        return retval;
    }

    @Override
    public List<VoipDn2DnBlockView> getTooLongBlocks(final VoipDnPlanView plan) {
        List<VoipDn2DnBlockView> retval = new ArrayList<>();
        for (VoipDn2DnBlockView block : plan.getVoipDn2DnBlockViews()) {
            final String onkzWithoutTrailingZero = CharMatcher.is('0').trimLeadingFrom(block.getOnkz());
            final int prefixLength = onkzWithoutTrailingZero.length() + block.getDnBase().length();

            final int maxBlockLength = Math.max(block.getAnfang().length(), getEndeOrAnfang(block).length());
            if (maxBlockLength + prefixLength > MAX_RUFNUMMER_LENGTH) {
                retval.add(block);
            }
        }
        return retval;
    }

    @Override
    public List<VoipDn2DnBlockView> getBlocksWithInconsistentArity(final VoipDnPlanView plan) {
        return plan.getVoipDn2DnBlockViews().stream()
                .filter(block -> block.getEnde() != null && block.getAnfang().length() != block.getEnde().length())
                .collect(Collectors.toList());
    }

    @Override
    public List<VoipDn2DnBlockView> getNonnumericalBlocks(final VoipDnPlanView plan) {
        final CharMatcher numeric = CharMatcher.inRange('0', '9');
        return plan.getVoipDn2DnBlockViews().stream()
                .filter(block -> !numeric.matchesAllOf(block.getAnfang()) || !numeric.matchesAllOf(getEndeOrAnfang(block)))
                .collect(Collectors.toList());

    }

    private static String getEndeOrAnfang(final VoipDn2DnBlockView block) {
        return (block.getEnde() != null) ? block.getEnde() : block.getAnfang();
    }

    private static String stringPlus(final String input, int addend) {
        String numInput = input.replaceAll("[^\\d]", "");
        if (numInput.isEmpty()) {
            numInput = "0";
        }

        final String sum = (new BigInteger(numInput)).add(BigInteger.valueOf(addend)).toString();
        return StringUtils.leftPad(sum, input.length(), '0');
    }
}
