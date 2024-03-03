Inhalt von de.augustakom.common.service.locator:
-----------------------------------------------------

Enth�lt eine ServiceLocator-Factory sowie spezielle ServiceLocator-Implementierungen.

Beispiel:
ServiceLocatorFactory
  public ServiceLocator getServiceLocator();
  // Wird ueber XML konfiguriert

XyzServiceLocator implements ServiceLocator
  public Object getService(ServiceContext srvCtx, String name) throws ServiceNotFoundException; 
  // ServiceContext waere ebenfalls in diesem Package implementiert


-----------------

Anmerkung:
Der Service-Locator sollte m�glichst �ber eine Factory erzeugt werden, um das Basis-Framework (evtl. <Spring>) ohne gro�e �nderungen auch sp�ter noch austauschen zu k�nnen.
Um zu bestimmen, welche Locator-Implementierung die Factory erzeugt, m�sste die Factory nat�rlich konfigurierbar sein.
Alle ServiceLocator-Implementierungen m�ssen einheitliche Interfaces implementieren.

-----------------

M�gliche Konfiguration von ServiceLocatorFactory:

<services>
  <servicelocator name="hurrican.gui">
    <class>de.augustakom.common.service.locator.SpringServiceLocator</class>
  </servicelocator>

  <servicelocator name="hurrican.web">
    <class>de.augustakom.common.service.locator.PicoServiceLocator</class>
  </servicelocator>
</services>


