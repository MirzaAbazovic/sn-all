package de.mnet.wita.message.builder.auftrag;

import java.util.*;

import de.mnet.wita.WitaCdmVersion;
import de.mnet.wita.message.auftrag.Schaltangaben;
import de.mnet.wita.message.auftrag.SchaltungKupfer;
import de.mnet.wita.message.auftrag.SchaltungKvzTal;
import de.mnet.wita.message.builder.AbstractWitaBuilder;

/**
 *
 */
public class SchaltangabenBuilder extends AbstractWitaBuilder<Schaltangaben> {

    private List<SchaltungKupfer> schaltungKupferList;
    private List<SchaltungKvzTal> schaltungKvzTalList;

    public SchaltangabenBuilder(WitaCdmVersion witaCdmVersion) {
        super(witaCdmVersion);
    }

    @Override
    public Schaltangaben buildValid() {
        return build();
    }

    @Override
    public Schaltangaben build() {
        Schaltangaben schaltangaben = new Schaltangaben();
        schaltangaben.setSchaltungKupfer(schaltungKupferList);
        schaltangaben.setSchaltungKvzTal(schaltungKvzTalList);
        return schaltangaben;
    }

    public SchaltangabenBuilder withSchaltungKupferZweiDraht(SchaltungKupfer schaltungKupfer) {
        this.schaltungKupferList = new ArrayList<>(1);
        this.schaltungKupferList.add(schaltungKupfer);
        return this;
    }

    public SchaltangabenBuilder withSchaltungKupferVierDraht(SchaltungKupfer schaltungKupfer) {
        this.schaltungKupferList = new ArrayList<>(2);
        this.schaltungKupferList.add(schaltungKupfer);
        this.schaltungKupferList.add(schaltungKupfer);
        return this;
    }

    public SchaltangabenBuilder withSchaltungKvzTal(SchaltungKvzTal schaltungKvzTal) {
        if (schaltungKvzTalList == null) {
            schaltungKvzTalList = new ArrayList<>(2);
        }
        schaltungKvzTalList.add(schaltungKvzTal);
        return this;
    }

}
