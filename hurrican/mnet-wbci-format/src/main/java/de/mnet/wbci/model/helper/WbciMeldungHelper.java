package de.mnet.wbci.model.helper;

import java.util.*;
import java.util.stream.*;

import de.mnet.wbci.model.Meldung;
import de.mnet.wbci.model.MeldungTyp;

public class WbciMeldungHelper {

    public static List<MeldungTyp> mapMeldungen2MeldungTyp(List<Meldung> meldungen) {
        if (meldungen == null) {
            return Collections.emptyList();
        }
        return meldungen.stream().map(Meldung::getTyp).collect(Collectors.toList());
    }
}
