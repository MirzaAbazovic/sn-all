package de.mnet.common.web;

import java.io.*;
import java.util.*;
import javax.servlet.http.*;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import de.augustakom.common.tools.dao.jdbc.AKBasicDataSource;

public class DbConnectionPoolServlet extends HttpServlet {
    private static final long serialVersionUID = -4780738758366868548L;

    protected WebApplicationContext getWebApplicationContext() {
        return WebApplicationContextUtils
                .getWebApplicationContext(getServletContext());
    }

    private void addDataSource(WebApplicationContext webApplicationContext, String id, List<AKBasicDataSource> dataSources) {
        try {
            AKBasicDataSource bean = (AKBasicDataSource) webApplicationContext.getBean(id);
            if (bean != null) {
                dataSources.add(bean);
            }
        }
        catch (Exception e) {
            //Nothing to do
        }
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        WebApplicationContext webApplicationContext = getWebApplicationContext();

        List<AKBasicDataSource> dataSources = new ArrayList<>();
        addDataSource(webApplicationContext, "authentication.dataSourceTarget", dataSources);
        addDataSource(webApplicationContext, "billing.dataSourceTarget", dataSources);
        addDataSource(webApplicationContext, "cc.dataSourceTarget", dataSources);
        addDataSource(webApplicationContext, "tal.dataSourceTarget", dataSources);
        addDataSource(webApplicationContext, "internet.DataSourceTarget", dataSources);
        addDataSource(webApplicationContext, "reporting.dataSourceTarget", dataSources);

        resp.setContentType("text/html");
        resp.getWriter().print(""
                + "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01//EN\"\n"
                + "\"http://www.w3.org/TR/html4/strict.dtd\">\n"
                + "<html>\n"
                + "<head>\n"
                + "  <meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\">\n"
                + "  <title>DB Connection Pool Status</title>\n"
                + "</head>\n"
                + "<body>\n");

        resp.getWriter().print(""
                + "  <h1>Connection Pools per Data Sources</h1>\n"
                + "  <pre>\n");

        for (AKBasicDataSource dataSource : dataSources) {
            String[] splits = dataSource.getUrl().split(":");
            String schema = (splits != null && splits.length > 0)? splits[splits.length - 1]: "";
            resp.getWriter().print("\nName: " + schema + ", " + dataSource.getUsername() + "\n");
            resp.getWriter().print("   Currently active connections  = " + dataSource.getNumActive() + "\n");
            resp.getWriter().print("   MaxActive connections         = " + dataSource.getMaxActive() + "\n");
            resp.getWriter().print("   Currently idle connections    = " + dataSource.getNumIdle() + "\n");
            resp.getWriter().print("   MaxIdle connections           = " + dataSource.getMaxIdle() + "\n");
            resp.getWriter().print("   MinIdle                       = " + dataSource.getMinIdle() + "\n");
            resp.getWriter().print("   MaxWait                       = " + dataSource.getMaxWait() + "\n");
            resp.getWriter().print("   DefaultAutocommit             = " + dataSource.getDefaultAutoCommit() + "\n");
            resp.getWriter().print("   DefaultReadOnly               = " + dataSource.getDefaultReadOnly() + "\n");
            resp.getWriter().print("   DefaultTransactionIsolation   = " + dataSource.getDefaultTransactionIsolation() + "\n");
            resp.getWriter().print("   InitialSize                   = " + dataSource.getInitialSize() + "\n");
            resp.getWriter().print("   ValidationQuery               = " + dataSource.getValidationQuery() + "\n");
            resp.getWriter().print("   ValidationQueryTimeout        = " + dataSource.getValidationQueryTimeout() + "\n");
            resp.getWriter().print("   TestOnBorrow                  = " + dataSource.getTestOnBorrow() + "\n");
            resp.getWriter().print("   TestOnReturn                  = " + dataSource.getTestOnReturn() + "\n");
            resp.getWriter().print("   TestWhileIdle                 = " + dataSource.getTestWhileIdle() + "\n");
            resp.getWriter().print("   NumTestsPerEvictionRun        = " + dataSource.getNumTestsPerEvictionRun() + "\n");
            resp.getWriter().print("   MinEvictableIdleTimeMillis    = " + dataSource.getMinEvictableIdleTimeMillis() + "\n");
            resp.getWriter().print("   TimeBetweenEvictionRunsMillis = " + dataSource.getTimeBetweenEvictionRunsMillis() + "\n");
            resp.getWriter().print("   MaxOpenPreparedStatements     = " + dataSource.getMaxOpenPreparedStatements() + "\n");
            resp.getWriter().print("   LogAbandoned                  = " + dataSource.getLogAbandoned() + "\n");
            resp.getWriter().print("   RemoveAbandoned               = " + dataSource.getRemoveAbandoned() + "\n");
            resp.getWriter().print("   RemoveAbandonedTimeout        = " + dataSource.getRemoveAbandonedTimeout() + "\n");
        }

        resp.getWriter().print(""
                + "  </pre>\n");

        resp.getWriter().print(""
                + "</body>\n"
                + "</html>\n");
    }
}
