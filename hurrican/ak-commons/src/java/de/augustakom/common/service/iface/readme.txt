Inhalt von de.augustakom.common.service.iface:
-----------------------------------------------------

Enthält nur Interfaces, die Basis-Services abbilden.

Beispiel:
ILoadService
  public void load(Object model) throws ServiceLoadException;
  public void load(Object model, Observer obs) throws ServiceLoadException;

IStoreService
  public void store(Object model) throws ServiceStoreException;

IFindService
  public List findAll() throws ServiceFindException;
  public List findByQuery(Object query) throws ServiceFindException;

etc.

