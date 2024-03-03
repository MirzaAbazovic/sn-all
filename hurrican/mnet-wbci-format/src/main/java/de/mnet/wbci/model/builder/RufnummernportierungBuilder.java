package de.mnet.wbci.model.builder;

import de.mnet.wbci.model.Portierungszeitfenster;
import de.mnet.wbci.model.Rufnummernportierung;

public abstract class RufnummernportierungBuilder<T extends Rufnummernportierung> implements WbciBuilder<T> {

    protected Portierungszeitfenster portierungszeitfenster;
    protected String portierungskennungPKIauf;

    protected void enrich(T rufnummernportierung) {
        rufnummernportierung.setPortierungszeitfenster(portierungszeitfenster);
        rufnummernportierung.setPortierungskennungPKIauf(portierungskennungPKIauf);
    }

    public RufnummernportierungBuilder<T> withPortierungszeitfenster(Portierungszeitfenster portierungszeitfenster) {
        this.portierungszeitfenster = portierungszeitfenster;
        return this;
    }

    public RufnummernportierungBuilder<T> withPortierungskennungPKIauf(String portierungskennungPKIauf) {
        this.portierungskennungPKIauf = portierungskennungPKIauf;
        return this;
    }
}
