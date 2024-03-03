/**
 *
 */
package de.augustakom.hurrican.dao.cc.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.*;
import static org.testng.Assert.*;

import java.util.*;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.model.cc.IPAddress;

@Test(groups = BaseTest.UNIT)
public class IPAddressDAOImplTest extends BaseTest {

    @InjectMocks
    private IPAddressDAOImpl cut = new IPAddressDAOImpl();

    @Mock
    private SessionFactory sessionFactoryMock;
    @Mock
    private Session sessionMock;
    @Mock
    private Criteria criteriaMock;

    @BeforeMethod
    public void setup() {
        initMocks(this);
        when(sessionFactoryMock.getCurrentSession()).thenReturn(sessionMock);
    }

    private void setIPArray(IPAddress ipAddress, Long id, Long netId) {
        ipAddress.setId(id);
        ipAddress.setNetId(netId);
    }

    private void initIPArray(IPAddress[] ipArray) {
        setIPArray(ipArray[0], Long.valueOf(1L), Long.valueOf(1L));
        setIPArray(ipArray[1], Long.valueOf(2L), Long.valueOf(1L));
        setIPArray(ipArray[2], Long.valueOf(3L), Long.valueOf(1L));
        setIPArray(ipArray[3], Long.valueOf(4L), Long.valueOf(2L));
        setIPArray(ipArray[4], Long.valueOf(5L), Long.valueOf(2L));
        setIPArray(ipArray[5], Long.valueOf(6L), Long.valueOf(3L));
    }

    private Pair<List<IPAddress>, Map<Long, Set<IPAddress>>> buildExample3(IPAddress[] ipArray) {
        List<IPAddress> resultList = Arrays.asList(ipArray);
        Set<IPAddress> resultSet1 = new HashSet<IPAddress>();
        resultSet1.addAll(Arrays.asList(ipArray[0], ipArray[1], ipArray[2]));
        Set<IPAddress> resultSet2 = new HashSet<IPAddress>();
        resultSet2.addAll(Arrays.asList(ipArray[3], ipArray[4]));
        Set<IPAddress> resultSet3 = new HashSet<IPAddress>();
        resultSet3.addAll(Arrays.asList(ipArray[5]));
        Map<Long, Set<IPAddress>> expectedMap = new HashMap<Long, Set<IPAddress>>();
        expectedMap.put(resultSet1.iterator().next().getNetId(), resultSet1);
        expectedMap.put(resultSet2.iterator().next().getNetId(), resultSet2);
        expectedMap.put(resultSet3.iterator().next().getNetId(), resultSet3);
        return new Pair<List<IPAddress>, Map<Long, Set<IPAddress>>>(resultList, expectedMap);
    }

    private boolean assertExpectedMap(Map<Long, Set<IPAddress>> resultMap, Map<Long, Set<IPAddress>> expectedMap) {
        if (resultMap.size() != expectedMap.size()) {
            return false;
        }
        Set<Long> keySet = resultMap.keySet();
        if (CollectionTools.isNotEmpty(keySet)) {
            for (Long key : keySet) {
                Set<IPAddress> resultSet = resultMap.get(key);
                Set<IPAddress> expectedSet = expectedMap.get(key);
                if (resultSet.size() != expectedSet.size()) {
                    return false;
                }
                if (CollectionTools.isNotEmpty(resultSet)) {
                    for (IPAddress ipAddress : resultSet) {
                        if (!expectedSet.contains(ipAddress)) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    @DataProvider
    public Object[][] dataProviderFindAllActiveAndAssignedIPs() {
        // @formatter:off
        IPAddress[] ipArray = new IPAddress[] {new IPAddress(), new IPAddress(), new IPAddress()
            , new IPAddress(), new IPAddress(), new IPAddress()};
        initIPArray(ipArray);
        Pair<List<IPAddress>, Map<Long, Set<IPAddress>>> example3 = buildExample3(ipArray);

        return new Object[][] {
                {                       null, new HashMap<Long, Set<IPAddress>>() },
                { new ArrayList<IPAddress>(), new HashMap<Long, Set<IPAddress>>() },
                {        example3.getFirst(),                example3.getSecond() },
        };
        // @formatter:on
    }

    @Test(dataProvider = "dataProviderFindAllActiveAndAssignedIPs")
    void testFindAllActiveAndAssignedIPs(List<IPAddress> queryResultList, Map<Long, Set<IPAddress>> expectedMap) {
        when(sessionMock.createCriteria(IPAddress.class)).thenReturn(criteriaMock);
        when(criteriaMock.add(any(Criterion.class))).thenReturn(criteriaMock);
        when(criteriaMock.addOrder(any(Order.class))).thenReturn(criteriaMock);
        when(criteriaMock.list()).thenReturn(queryResultList);
        Map<Long, Set<IPAddress>> resultMap = cut.findAllActiveAndAssignedIPs();
        assertTrue(assertExpectedMap(resultMap, expectedMap));
    }

} // end
