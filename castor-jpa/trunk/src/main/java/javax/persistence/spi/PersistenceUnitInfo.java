package javax.persistence.spi;

import java.net.URL;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;
/**
 * Interface implemented by the container and used by the persistence provider when creating an EntityManagerFactory.
 */
public interface PersistenceUnitInfo {
    /**
     * @return The name of the persistence unit. Corresponds to the <name> element in the persistence.xml file.
     */
    public String getPersistenceUnitName();
    /**
     * @return The fully qualified name of the persistence provider implementation class. Corresponds to the <provider> element in the
     *         persistence.xml file.
     */
    public String getPersistenceProviderClassName();
    /**
     * @return The transaction type of the entity managers created by the EntityManagerFactory. The transaction type corresponds to the
     *         transaction-type attribute in the persistence.xml file.
     */
    public PersistenceUnitTransactionType getTransactionType();
    /**
     * @return The JTA-enabled data source to be used by the persistence provider. The data source corresponds to the <jta-data-source>
     *         element in the persistence.xml file or is provided at deployment or by the container.
     */
    public DataSource getJtaDataSource();
    /**
     * @return The non-JTA-enabled data source to be used by the persistence provider for accessing data outside a JTA transaction. The
     *         data source corresponds to the named <non-jta-data-source> element in the persistence.xml file or provided at deployment or
     *         by the container.
     */
    public DataSource getNonJtaDataSource();
    /**
     * @return The list of mapping file names that the persistence provider must load to determine the mappings for the entity classes. The
     *         mapping files must be in the standard XML mapping format, be uniquely named and be resource-loadable from the application
     *         classpath. Each mapping file name corresponds to a <mapping-file> element in the persistence.xml file.
     */
    public List<String> getMappingFileNames();
    /**
     * @return The list of JAR file URLs that the persistence provider must examine for managed classes of the persistence unit. Each jar
     *         file URL corresponds to a named <jar-file> element in the persistence.xml file.
     */
    public List<URL> getJarFileUrls();
    /**
     * @return The URL for the jar file or directory that is the root of the persistence unit. (If the persistence unit is rooted in the
     *         WEB-INF/classes directory, this will be the URL of that directory.)
     */
    public URL getPersistenceUnitRootUrl();
    /**
     * @return The list of the names of the classes that the persistence provider must add it to its set of managed classes. Each name
     *         corresponds to a named <class> element in the persistence.xml file.
     */
    public List<String> getManagedClassNames();
    /**
     * @return Whether classes in the root of the persistence unit that have not been explicitly listed are to be included in the set of
     *         managed classes. This value corresponds to the <exclude-unlisted-classes> element in the persistence.xml file.
     */
    public boolean excludeUnlistedClasses();
    /**
     * @return Properties object. Each property corresponds to a <property> element in the persistence.xml file
     */
    public Properties getProperties();
    /**
     * @return ClassLoader that the provider may use to load any classes, resources, or open URLs.
     */
    public ClassLoader getClassLoader();
    /**
     * Add a transformer supplied by the provider that will be called for every new class definition or class redefinition that gets loaded
     * by the loader returned by the PersistenceInfo.getClassLoader method. The transformer has no effect on the result returned by the
     * PersistenceInfo.getTempClassLoader method. Classes are only transformed once within the same classloading scope, regardless of how
     * many persistence units they may be a part of.
     * 
     * @param transformer A provider-supplied transformer that the Container invokes at class-(re)definition time
     */
    public void addTransformer(ClassTransformer transformer);
    /**
     * Return a new instance of a ClassLoader that the provider may use to temporarily load any classes, resources, or open URLs. The scope
     * and classpath of this loader is exactly the same as that of the loader returned by PersistenceInfo.getClassLoader. None of the
     * classes loaded by this class loader will be visible to application components. The container does not use or maintain references to
     * this class loader after returning it to the provider.
     * 
     * @return Temporary ClassLoader with same visibility as current loader
     */
    public ClassLoader getNewTempClassLoader();
}
