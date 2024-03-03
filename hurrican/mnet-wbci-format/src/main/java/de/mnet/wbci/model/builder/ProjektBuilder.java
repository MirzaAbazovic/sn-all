package de.mnet.wbci.model.builder;

import de.mnet.wbci.model.Projekt;

public class ProjektBuilder implements WbciBuilder<Projekt> {

    protected String projektKenner;
    protected String kopplungsKenner;

    @Override
    public Projekt build() {
        Projekt projekt = new Projekt();
        projekt.setProjektKenner(projektKenner);
        projekt.setKopplungsKenner(kopplungsKenner);
        return projekt;
    }

    public ProjektBuilder withProjektKenner(String projektKenner) {
        this.projektKenner = projektKenner;
        return this;
    }

    public ProjektBuilder withKopplungsKenner(String kopplungsKenner) {
        this.kopplungsKenner = kopplungsKenner;
        return this;
    }

}
