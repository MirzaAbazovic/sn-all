package de.mnet.wbci.model.builder;

import org.apache.commons.lang.StringUtils;

import de.mnet.wbci.model.Firma;

public class FirmaBuilder extends PersonOderFirmaBuilder<Firma> {

    protected String firmenname;
    protected String firmennamenZusatz;

    @Override
    public Firma build() {
        Firma firma = new Firma();
        enrich(firma);
        firma.setFirmenname(StringUtils.stripToNull(firmenname));
        firma.setFirmennamenZusatz(StringUtils.stripToNull(firmennamenZusatz));
        return firma;
    }

    public FirmaBuilder withFirmename(String firmenname) {
        this.firmenname = firmenname;
        return this;
    }

    public FirmaBuilder withFirmennamenZusatz(String firmennamenZusatz) {
        this.firmennamenZusatz = firmennamenZusatz;
        return this;
    }

}
