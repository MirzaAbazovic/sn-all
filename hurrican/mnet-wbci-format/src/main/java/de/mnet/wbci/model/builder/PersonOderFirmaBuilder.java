package de.mnet.wbci.model.builder;

import de.mnet.wbci.model.Anrede;
import de.mnet.wbci.model.KundenTyp;
import de.mnet.wbci.model.PersonOderFirma;

public abstract class PersonOderFirmaBuilder<T extends PersonOderFirma> implements WbciBuilder<T> {

    protected Anrede anrede;
    protected KundenTyp kundenTyp;

    protected void enrich(T personOderFirma) {
        personOderFirma.setAnrede(anrede);
        personOderFirma.setKundenTyp(kundenTyp);
    }

    public PersonOderFirmaBuilder<T> withAnrede(Anrede anrede) {
        this.anrede = anrede;
        return this;
    }

    public PersonOderFirmaBuilder<T> withKundenTyp(KundenTyp kundenTyp) {
        this.kundenTyp = kundenTyp;
        return this;
    }

}
