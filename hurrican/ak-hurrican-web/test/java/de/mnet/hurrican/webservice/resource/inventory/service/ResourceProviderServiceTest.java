package de.mnet.hurrican.webservice.resource.inventory.service;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.*;

import com.google.common.collect.Lists;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.hurrican.adm.resourceinventoryservice.v1.Resource;
import de.mnet.hurrican.webservice.resource.ResourceProcessException;
import de.mnet.hurrican.webservice.resource.inventory.builder.OltChildResourceBuilder;

@Test(groups = BaseTest.UNIT)
public class ResourceProviderServiceTest extends BaseTest {

    @InjectMocks
    ResourceProviderService cut;

    @Mock
    ResourceMapper resourceMapper;

    @Mock
    OntPortResourceMapper ontPortResourceMapper;

    @BeforeMethod
    public void setUp() throws Exception {
        initMocks(this);
        cut.setResourceMappers(Lists.newArrayList(resourceMapper, ontPortResourceMapper));
    }

    @Test
    public void testUpdateResourceOntPort() throws Exception {
        final Resource in = new Resource();
        final long sessionId = -1L;

        when(ontPortResourceMapper.isResourceSupported(in)).thenReturn(true);
        when(resourceMapper.isResourceSupported(in)).thenReturn(false);
        cut.updateResource(in, sessionId);
        verify(ontPortResourceMapper).processResource(in, sessionId);
        verify(resourceMapper, never()).processResource(in, sessionId);
    }

    @Test
    public void testUpdateResourceOnt() throws Exception {
        final Resource in = new Resource();
        final long sessionId = -1L;

        when(resourceMapper.isResourceSupported(in)).thenReturn(true);
        when(ontPortResourceMapper.isResourceSupported(in)).thenReturn(false);
        cut.updateResource(in, sessionId);
        verify(resourceMapper).processResource(in, sessionId);
        verify(ontPortResourceMapper, never()).processResource(in, sessionId);
    }

    @Test(expectedExceptions = ResourceProcessException.class, expectedExceptionsMessageRegExp = "UpdateResource fuer Resource \\[id=null, inventory=null, name=null\\] ist nicht unterstuetzt!")
    public void testUpdateResourceNotSupportedEmptyResource() throws Exception {
        final Resource in = new Resource();
        final long sessionId = -1L;

        when(resourceMapper.isResourceSupported(in)).thenReturn(false);
        when(ontPortResourceMapper.isResourceSupported(in)).thenReturn(false);
        cut.updateResource(in, sessionId);
    }

    @Test(expectedExceptions = ResourceProcessException.class, expectedExceptionsMessageRegExp = "UpdateResource fuer Resource \\[id=123, inventory=inv-123, name=mdu-1\\] ist nicht unterstuetzt!")
    public void testUpdateResourceNotSupported() throws Exception {
        final Resource in =
                new OltChildResourceBuilder()
                        .withId("123")
                        .withInventory("inv-123")
                        .withName("mdu-1")
                        .build();
        final long sessionId = -1L;

        when(resourceMapper.isResourceSupported(in)).thenReturn(false);
        when(ontPortResourceMapper.isResourceSupported(in)).thenReturn(false);
        cut.updateResource(in, sessionId);
    }
}
