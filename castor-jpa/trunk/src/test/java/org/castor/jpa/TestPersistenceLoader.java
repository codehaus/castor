package org.castor.jpa;

import java.net.URL;
import java.util.List;

import javax.persistence.spi.PersistenceUnitInfo;

import org.castor.jpa.PersistenceLoader;

import junit.framework.TestCase;

public class TestPersistenceLoader extends TestCase {

    public void testLoading() throws Exception {
        URL resourceLocation =
                getClass().getClassLoader().getResource(
                        "META-INF/persistence.xml");
        assertNotNull(resourceLocation);

        PersistenceLoader persistenceLoader = new PersistenceLoader();
        List<PersistenceUnitInfo> persistenceGroups =
                persistenceLoader.loadPersistence(resourceLocation);
        assertNotNull(persistenceGroups);
        assertFalse(persistenceGroups.isEmpty());
        assertEquals(1, persistenceGroups.size());

        PersistenceUnitInfo infoGroup = persistenceGroups.iterator().next();
        assertNotNull(infoGroup);
        assertEquals("castor", infoGroup.getPersistenceUnitName());

        List mappingFiles = infoGroup.getMappingFileNames();
        assertNotNull(mappingFiles);
        assertFalse(mappingFiles.isEmpty());
        assertEquals(1, mappingFiles.size());

        String mappingFile = (String) mappingFiles.get(0);
        assertNotNull(mappingFile);
        assertTrue(mappingFile.endsWith("mapping.xml"));

    }

    public void testAnotherLoading() throws Exception {
        URL resourceLocation =
                getClass().getClassLoader().getResource(
                        "META-INF/persistence-both.xml");
        assertNotNull(resourceLocation);

        PersistenceLoader persistenceLoader = new PersistenceLoader();
        List<PersistenceUnitInfo> persistenceGroups =
                persistenceLoader.loadPersistence(resourceLocation);
        assertNotNull(persistenceGroups);
        assertFalse(persistenceGroups.isEmpty());
        assertEquals(1, persistenceGroups.size());

        PersistenceUnitInfo infoGroup = persistenceGroups.iterator().next();
        assertNotNull(infoGroup);
        assertEquals("OrderManagement", infoGroup.getPersistenceUnitName());

        List mappingFiles = infoGroup.getMappingFileNames();
        assertNotNull(mappingFiles);
        assertFalse(mappingFiles.isEmpty());
        assertEquals(1, mappingFiles.size());

        String mappingFile = (String) mappingFiles.get(0);
        assertNotNull(mappingFile);
        assertTrue(mappingFile.endsWith("ormap2.xml"));

        List<URL> jarFileUrls = infoGroup.getJarFileUrls();
        assertNotNull(jarFileUrls);
        assertFalse(jarFileUrls.isEmpty());
        assertEquals(1, mappingFiles.size());

        URL jarFileUrl = jarFileUrls.get(0);
        assertNotNull(jarFileUrl);
        assertTrue(jarFileUrl.toExternalForm().endsWith("MyPartsApp.jar"));

        List managedClassNames = infoGroup.getManagedClassNames();
        assertNotNull(managedClassNames);
        assertFalse(managedClassNames.isEmpty());
        assertEquals(2, managedClassNames.size());

        String managedClassName = (String) managedClassNames.get(0);
        assertNotNull(managedClassName);
        assertEquals("com.widgets.Order", managedClassName);

        managedClassName = (String) managedClassNames.get(1);
        assertNotNull(managedClassName);
        assertEquals("com.widgets.Customer", managedClassName);

    }

}
