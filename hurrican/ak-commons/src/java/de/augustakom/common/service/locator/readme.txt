Inhalt von de.augustakom.common.service.locator:
-----------------------------------------------------

Enthält eine ServiceLocator-Factory sowie spezielle ServiceLocator-Implementierungen.

Beispiel:
ServiceLocatorFactory
  public ServiceLocator getServiceLocator();
  // Wird ueber XML konfiguriert

XyzServiceLocator implements ServiceLocator
  public Object getService(ServiceContext srvCtx, String name) throws ServiceNotFoundException; 
  // ServiceContext waere ebenfalls in diesem Package implementiert


-----------------

Anmerkung:
Der Service-Locator sollte möglichst über eine Factory erzeugt werden, um das Basis-Framework (evtl. <Spring>) ohne große Änderungen auch später noch austauschen zu können.
Um zu bestimmen, welche Locator-Implementierung die Factory erzeugt, müsste die Factory natürlich konfigurierbar sein.
Alle ServiceLocator-Implementierungen müssen einheitliche Interfaces implementieren.

-----------------

Mögliche Konfiguration von ServiceLocatorFactory:

<services>
  <servicelocator name="hurrican.gui">
    <class>de.augustakom.common.service.locator.SpringServiceLocator</class>
  </servicelocator>

  <servicelocator name="hurrican.web">
    <class>de.augustakom.common.service.locator.PicoServiceLocator</class>
  </servicelocator>
</services>


