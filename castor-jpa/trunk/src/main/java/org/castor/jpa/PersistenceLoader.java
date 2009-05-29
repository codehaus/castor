package org.castor.jpa;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.PersistenceException;
import javax.persistence.spi.PersistenceUnitInfo;

import org.castor.jpa.persistence.xml.Persistence;
import org.castor.jpa.persistence.xml.PersistenceUnit;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.InputSource;

public class PersistenceLoader {
    
    private String rootLocation = null;
    
    public List<PersistenceUnitInfo> loadPersistence (URL location) {
        
        String locationString = location.toExternalForm();
        rootLocation = 
            locationString.substring(0, locationString.lastIndexOf("META-INF/"));
        
        List<PersistenceUnitInfo> infoGroups = new ArrayList<PersistenceUnitInfo>();

        Persistence persistence = null;
        Unmarshaller unmarshaller = new Unmarshaller (Persistence.class);
        try {
            persistence = (Persistence) 
                unmarshaller.unmarshal(new InputSource(location.toExternalForm()));
        } catch (MarshalException e) {
            throw new PersistenceException ("Problem unmarshalling persistence.xml file", e);
        } catch (ValidationException e) {
            throw new PersistenceException ("Problem validating persistence.xml file", e);
        }
        

        PersistenceUnit[] persistenceUnits = persistence.getPersistenceUnit();
        for (int i = 0; i < persistenceUnits.length; i++) {
            infoGroups.add(loadPersistenceUnitInfo(persistenceUnits[i]));
        }
        
        return infoGroups;
    }
    
    public PersistenceUnitInfo loadPersistenceUnitInfo(PersistenceUnit persistenceUnit) {
        CastorPersistenceUnitInfo infoGroup = new CastorPersistenceUnitInfo();
        
        infoGroup.setPersistenceUnitName(persistenceUnit.getName());
        infoGroup.setExcludeUnlistedClasses(persistenceUnit.getExcludeUnlistedClasses());
        
        String[] mappingFiles = persistenceUnit.getMappingFile();
        for (int i = 0; i < mappingFiles.length; i++) {
            infoGroup.addMappingFileName(rootLocation + mappingFiles[i]);
        }

        String[] jarFiles = persistenceUnit.getJarFile();
        for (int i = 0; i < jarFiles.length; i++) {
            String jarFileLocationString = rootLocation + jarFiles[i];
            try {
                infoGroup.addJarFileUrl(new URL(jarFileLocationString));
            } catch (MalformedURLException e) {
                throw new PersistenceException ("Problem locating " + jarFileLocationString, e);
            }
        }
   
        String[] classFiles = persistenceUnit.getClazz();
        for (int i = 0; i < classFiles.length; i++) {
            infoGroup.addManagedClassName(classFiles[i]);
        }
        
        return infoGroup;
        
    }

}
