package de.mnet.framework;

import javax.inject.*;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("singleton")
public class MockedWitaBsiProtokollService {
    @Inject
    MockedBsiProtokollEintragService bsiProtokollEintragService;
    @Inject
    MockedMwfEntityDao entityDao;
    @Inject
    MockedTaskDao taskDao;

    public MockedBsiProtokollEintragService getBsiProtokollEintragService() {
        return bsiProtokollEintragService;
    }

    public void setBsiProtokollEintragService(MockedBsiProtokollEintragService bsiProtokollEintragService) {
        this.bsiProtokollEintragService = bsiProtokollEintragService;
    }

    public MockedMwfEntityDao getEntityDao() {
        return entityDao;
    }

    public void setEntityDao(MockedMwfEntityDao entityDao) {
        this.entityDao = entityDao;
    }

    public MockedTaskDao getTaskDao() {
        return taskDao;
    }

    public void setTaskDao(MockedTaskDao taskDao) {
        this.taskDao = taskDao;
    }
}
