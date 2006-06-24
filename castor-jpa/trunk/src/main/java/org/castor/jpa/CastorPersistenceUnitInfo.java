package org.castor.jpa;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.persistence.spi.ClassTransformer;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.persistence.spi.PersistenceUnitTransactionType;
import javax.sql.DataSource;

public class CastorPersistenceUnitInfo implements PersistenceUnitInfo {

    private String persistenceUnitName;
    private String persistenceProviderClassName;
    private PersistenceUnitTransactionType transactionType;
    private DataSource jtaDataSource;
    private DataSource nonJtaDataSource;
    private List<String> mappingFileNames = new ArrayList<String>();
    private List<URL> jarFileUrls = new ArrayList<URL>();
    private URL persistenceUnitRootUrl;
    private List<String> managedClassNames = new ArrayList<String>();
    private boolean excludeUnlistedClasses;
    private Properties properties;
    private ClassLoader classLoader;
    private ClassLoader newTempClassLoader;
    private List<ClassTransformer> transformers = new ArrayList<ClassTransformer>();
    
    public String getPersistenceUnitName() {
        return persistenceUnitName;
    }

    public String getPersistenceProviderClassName() {
        return persistenceProviderClassName;
    }

    public PersistenceUnitTransactionType getTransactionType() {
        return transactionType;
    }

    public DataSource getJtaDataSource() {
        return jtaDataSource;
    }

    public DataSource getNonJtaDataSource() {
        return nonJtaDataSource;
    }

    public List<String> getMappingFileNames() {
        return mappingFileNames;
    }
    
    public void addMappingFileName(String mappingFileName) {
        mappingFileNames.add(mappingFileName);
    }

    public List<URL> getJarFileUrls() {
        return jarFileUrls;
    }

    public void addJarFileUrl (URL jarFileURL) {
        jarFileUrls.add(jarFileURL);
    }
    
    public URL getPersistenceUnitRootUrl() {
        return persistenceUnitRootUrl;
    }

    public List<String> getManagedClassNames() {
        return managedClassNames;
    }

    public void addManagedClassName (String managedClassName) {
        managedClassNames.add(managedClassName);
    }
    
    public boolean excludeUnlistedClasses() {
        return excludeUnlistedClasses;
    }

    public Properties getProperties() {
        return properties;
    }

    public void addProperty (String name, String value) {
        properties.put(name, value);
    }
    
    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public void addTransformer(ClassTransformer transformer) {
        transformers.add(transformer);
    }

    public ClassLoader getNewTempClassLoader() {
        return newTempClassLoader;
    }

    public boolean isExcludeUnlistedClasses() {
        return excludeUnlistedClasses;
    }

    public void setExcludeUnlistedClasses(boolean excludeUnlistedClasses) {
        this.excludeUnlistedClasses = excludeUnlistedClasses;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public void setJtaDataSource(DataSource jtaDataSource) {
        this.jtaDataSource = jtaDataSource;
    }

    public void setNewTempClassLoader(ClassLoader newTempClassLoader) {
        this.newTempClassLoader = newTempClassLoader;
    }

    public void setNonJtaDataSource(DataSource nonJtaDataSource) {
        this.nonJtaDataSource = nonJtaDataSource;
    }

    public void setPersistenceUnitName(String persistenceUnitName) {
        this.persistenceUnitName = persistenceUnitName;
    }

    public void setPersistenceUnitRootUrl(URL persistenceUnitRootUrl) {
        this.persistenceUnitRootUrl = persistenceUnitRootUrl;
    }

    public void setTransactionType(PersistenceUnitTransactionType transactionType) {
        this.transactionType = transactionType;
    }

}
