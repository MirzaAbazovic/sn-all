package de.mnet.hurrican.scripts.impl;

import jxl.Cell;
import jxl.Sheet;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import de.augustakom.authentication.gui.SystemConstants;
import de.augustakom.authentication.service.impl.AKAdminApplicationContextInitializer;
import de.augustakom.common.AKDefaultConstants;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.service.locator.ServiceLocator;
import de.augustakom.hurrican.service.base.exceptions.DeleteException;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.AvailabilityService;
import de.augustakom.hurrican.service.cc.ReferenceService;
import de.augustakom.hurrican.service.utils.ExcelFileHelper;

public class MigrateGeoIds {

    private static final String[] CONFIGS = new String[] {
            "classpath:/de/augustakom/hurrican/service/cc/resources/CCServices.xml",
            "classpath:/de/augustakom/hurrican/service/cc/resources/CCClientServices.xml",
            "classpath:/de/augustakom/hurrican/service/billing/resources/BillingServices.xml",
            "classpath:/de/augustakom/hurrican/service/internet/resources/InternetServices.xml",
            "classpath:/de/augustakom/hurrican/service/exmodules/tal/resources/TALServices.xml"
    };

    public static void main(String[] args) {
        MigrateGeoIds x = new MigrateGeoIds();
        ContextLoaderAndMigrator migrator = x.new ContextLoaderAndMigrator();
        try {
            migrator.doLogin();
            migrator.initServices();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        try {
            migrator.doGeoIdReplacementMigration();
        }
        catch (FindException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (DeleteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (StoreException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    class ContextLoaderAndMigrator {

        private AvailabilityService service;
        private ReferenceService referenceService;

        private ServiceLocator serviceLocator;

        /* Fuehrt den Login durch. */
        private void doLogin() {

            try {
                setSystemProperties4AuthenticationDb("HURRICAN_RUSTEBERGKA", "1nacirruh");
                AKAdminApplicationContextInitializer authInit = new AKAdminApplicationContextInitializer();
                //            ApplicationContext authContext = authInit.initializeApplicationContext(authSys);
                ClassPathXmlApplicationContext context =
                        new ClassPathXmlApplicationContext(CONFIGS);
                // register this application context under various names used for lookups
                serviceLocator = ServiceLocator.instance();
                serviceLocator.setApplicationContext(context);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void setSystemProperties4AuthenticationDb(String username, String password) {
            System.setProperty(SystemConstants.DB_NAME + SystemConstants.JDBC_USER_SUFFIX, username);
            System.setProperty(SystemConstants.DB_NAME + SystemConstants.JDBC_PASSWORD_SUFFIX, password);
            System.setProperty(AKDefaultConstants.FALLBACK_PREFIX +
                    SystemConstants.DB_NAME + SystemConstants.JDBC_USER_SUFFIX, "");
            System.setProperty(AKDefaultConstants.FALLBACK_PREFIX +
                    SystemConstants.DB_NAME + SystemConstants.JDBC_PASSWORD_SUFFIX, "");
        }

        public void doGeoIdReplacementMigration() throws FindException, DeleteException, StoreException {

            String importFile = "/de/augustakom/hurrican/service/cc/impl/REPLACED_GEOIDs.xls";
            Integer columnOldGeoId = 0;
            Integer columnNewGeoId = 1;

            ExcelFileHelper excelHelper = new ExcelFileHelper();
            Sheet sheet = excelHelper.loadExcelFile(importFile);
            Integer lastRowNo = sheet.getRows();

            //beginnend bei der zweiten Zeile
            for (Integer rowCount = 1; rowCount < lastRowNo; rowCount++) {
                Cell[] row = sheet.getRow(rowCount);

                String oldGeoId = excelHelper.getCellContent(row, columnOldGeoId);
                String newGeoId = excelHelper.getCellContent(row, columnNewGeoId);
                if (StringUtils.isNumeric(oldGeoId) && StringUtils.isNumeric(newGeoId)) {
                    service.geoIdReplacement(Long.valueOf(oldGeoId), Long.valueOf(newGeoId));
                }
            }
        }

        public void initServices() throws ServiceNotFoundException {
            service = serviceLocator.getService("de.augustakom.hurrican.service.cc.AvailabilityService", AvailabilityService.class);
            //	    	referenceService = serviceLocator.getService("ReferenceService", ReferenceService.class);
        }
    }
}
