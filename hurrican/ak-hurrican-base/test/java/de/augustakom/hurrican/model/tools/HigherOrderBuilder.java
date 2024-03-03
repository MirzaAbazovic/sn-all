package de.augustakom.hurrican.model.tools;

import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.AuftragTechnikBuilder;
import de.augustakom.hurrican.model.cc.Carrierbestellung2EndstelleBuilder;
import de.augustakom.hurrican.model.cc.EndstelleBuilder;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;

/**
 * TestBuilder
 */
public interface HigherOrderBuilder<T> {

    public interface Results {
    }

    public class Auftrag implements Results {
        public AuftragBuilder auftragBuilder;
        public AuftragDatenBuilder auftragDatenBuilder;
        public AuftragTechnikBuilder auftragTechnikBuilder;
    }

    public class AuftragWithEndstelle extends Auftrag {
        public EndstelleBuilder endstelleBuilder;
    }

    public class AuftragWithCb extends AuftragWithEndstelle {
        public Carrierbestellung2EndstelleBuilder carrierbestellung2EndstelleBuilder;
        public de.augustakom.hurrican.model.cc.CarrierbestellungBuilder carrierbestellungBuilder;
    }

    public Results prepare(AbstractHurricanBaseServiceTest test, T results);
}
