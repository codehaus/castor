package org.exolab.castor.xml.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.exolab.castor.mapping.ClassDescriptor;
import org.exolab.castor.mapping.MappingLoader;
import org.exolab.castor.xml.ResolverException;

/**
 * JDO-specific {@link ClassDescriptorResolver} instance that provides
 * functionality to find or "resolve" {@link ClassDescriptor}s from a given
 * class (name).
 * 
 * @see JDOClassDescriptorResolver
 */
public class JDOClassDescriptorResolverImpl implements
        JDOClassDescriptorResolver {

    /**
     * A key ({@link Class}) value ({@link ClassDescriptor}) pair to cache
     * already resolved JDO class descriptors.
     */
    private Map _classDescriptorCache = new HashMap();

    /**
     * A {@link MappingLoader} instance which this
     * {@link ClassDescriptorResolver} instance turns to to load
     * {@link ClassDescriptor} instances as defined in a Castor JDO mapping
     * file.
     */
    private MappingLoader _mappingLoader;

    /**
     * List of manually added domain <code>Class</code>es.
     */
    protected List _classes = new LinkedList();

    /**
     * List of manually added package names.
     */
    protected List _packages = new LinkedList();

    /**
     * JDO-specific {@link ClassDescriptor} resolution commands.
     */
    private Map _commands = new HashMap();

    /**
     * Creates an instance of this class, with no classed manually added.
     */
    public JDOClassDescriptorResolverImpl() {
        super();
        registerCommand(new ClassResolutionByMappingLoader());
        registerCommand(new ClassResolutionByFile());
        registerCommand(new ClassResolutionByCDR());
    }

    /**
     * Registers a {@link ClassDescriptorResolutionCommand} used to resolve
     * {@link ClassDescriptor}s.
     * 
     * @param command
     *            to register.
     */
    private void registerCommand(final ClassDescriptorResolutionCommand command) {
        // //TODO: temporal implementation - following 2 lines need to be
        // revised!
        // ClassLoaderNature clNature = new ClassLoaderNature(command);
        // clNature.setClassLoader(getClass().getClassLoader());
        _commands.put(command.getClass().getName(), command);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.exolab.castor.xml.util.JDOClassDescriptorResolver#resolve(java.lang.String)
     */
    public ClassDescriptor resolve(final String type) throws ResolverException {
        try {
            if (getMappingLoader().getClassLoader() != null) {
                return resolve(getMappingLoader().getClassLoader().loadClass(
                        type));
            }
            return resolve(Class.forName(type));
        } catch (ClassNotFoundException e) {
            throw new ResolverException("Problem loading class " + type);
        }
    }

    /**
     * Returns the ClassDescriptor for the given class using the following
     * strategy.<br>
     * <br>
     * <ul>
     * <li>Lookup the class descriptor cache
     * <li>Call {@link ClassResolutionByMappingLoader} command
     * <li>Call {@link ClassResolutionByFile} command
     * </ul>
     * 
     * @param type
     *            the Class to find the ClassDescriptor for
     * @exception ResolverException
     *                Indicates that the given {@link Class} cannot be resolved.
     * @return the ClassDescriptor for the given class, null if not found
     */
    public ClassDescriptor resolve(final Class type) throws ResolverException {
        if (type == null) {
            return null;
        }

        ClassDescriptor classDesc = null;

        // 1) consult with cache
        classDesc = resolveByCache(type);
        if (classDesc != null) {
            return classDesc;
        }

        classDesc = lookup(ClassResolutionByMappingLoader.class.getName())
                .resolve(type);
        if (classDesc != null) {
            _classDescriptorCache.put(type, classDesc);
            return classDesc;
        }

        // 3) load ClassDescriptor from file system
        classDesc = lookup(ClassResolutionByFile.class.getName()).resolve(type);
        if (classDesc != null) {
            _classDescriptorCache.put(type, classDesc);
            return classDesc;
        }

        // 4) load ClassDescriptor from file system through CDR file
        // (as added via addPackage())
        classDesc = (lookup(ClassResolutionByCDR.class.getName()))
                .resolve(type);
        if (classDesc != null) {
            _classDescriptorCache.put(type, classDesc);
            return classDesc;
        }

        // TODO: consider for future extensions
        // String pkgName = getPackageName(type.getName());
        //
        // //-- check package mapping
        // Mapping mapping = loadPackageMapping(pkgName, type.getClassLoader());
        // if (mapping != null) {
        // try {
        // MappingLoader mappingLoader = mapping.getResolver(BindingType.JDO);
        // classDesc = mappingLoader.getDescriptor(type);
        // }
        // catch(MappingException mx) {}
        // if (classDesc != null) {
        // associate(type, classDesc);
        // return classDesc;
        // }
        // }

        return classDesc;
    }

    /**
     * Look up the given command in the command map.
     * 
     * @param commandName
     *            The command.
     * @return A {@link ClassDescriptorResolutionCommand}, null if not found.
     */
    private ClassDescriptorResolutionCommand lookup(final String commandName) {
        return (ClassDescriptorResolutionCommand) _commands.get(commandName);
    }

    /**
     * Resolves a {@link ClassDescriptor} by a cache lookup.
     * 
     * @param type
     *            type to look up.
     * @return a {@link ClassDescriptor} if found, null if not.
     */
    private ClassDescriptor resolveByCache(final Class type) {
        ClassDescriptor classDesc = null;
        classDesc = (ClassDescriptor) _classDescriptorCache.get(type);
        return classDesc;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.exolab.castor.xml.util.JDOClassDescriptorResolver#getMappingLoader()
     */
    public MappingLoader getMappingLoader() {
        return _mappingLoader;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.exolab.castor.xml.ClassDescriptorResolver
     *      #setMappingLoader(org.exolab.castor.mapping.MappingLoader)
     */
    public void setMappingLoader(final MappingLoader mappingLoader) {
        this._mappingLoader = mappingLoader;
        for (Iterator iterator = _commands.values().iterator(); iterator
                .hasNext();) {
            ClassDescriptorResolutionCommand command = (ClassDescriptorResolutionCommand) iterator
                    .next();
            if (command.hasNature(MappingLoaderNature.class.getName())) {
                new MappingLoaderNature(command)
                        .setMappingLoader(mappingLoader);
            }
            if (command.hasNature(ClassLoaderNature.class.getName())) {
                new ClassLoaderNature(command).setClassLoader(_mappingLoader
                        .getClassLoader());
            }
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.exolab.castor.xml.util.JDOClassDescriptorResolver#addClass(java.lang.Class)
     */
    public void addClass(final Class domainClass) {
        _classes.add(domainClass);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.exolab.castor.xml.util.JDOClassDescriptorResolver#addPackage(java.lang.String)
     */
    public void addPackage(final String packageName) {
        this._packages.add(packageName);
        for (Iterator iterator = _commands.values().iterator(); iterator
                .hasNext();) {
            ClassDescriptorResolutionCommand command = (ClassDescriptorResolutionCommand) iterator
                    .next();
            if (command.hasNature(PackageBasedCDRResolutionNature.class.getName())) {
                new PackageBasedCDRResolutionNature(command)
                        .addPackageName(packageName);
            }
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.exolab.castor.xml.util.JDOClassDescriptorResolver#descriptorIterator()
     */
    public Iterator descriptorIterator() {
        List allDescriptors = new ArrayList();
        allDescriptors.addAll(_mappingLoader.getDescriptors());
        for (Iterator iterator = _classes.iterator(); iterator.hasNext();) {
            allDescriptors.add(lookup(ClassResolutionByFile.class.getName())
                    .resolve((Class) iterator.next()));
        }
        ClassResolutionByCDR cdrNature = (ClassResolutionByCDR) 
            lookup(ClassResolutionByCDR.class.getName());
        for (Iterator iterator = _packages.iterator(); iterator.hasNext();) {
            String packageName = (String) iterator.next();
            Map descriptors = cdrNature.getDescriptors(packageName);
            for (Iterator descriptorIterator = descriptors.entrySet().iterator(); descriptorIterator
                    .hasNext();) {
                Entry entry = (Entry) descriptorIterator.next();
                allDescriptors.add(entry.getValue());
            } 
        }
        return allDescriptors.iterator();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.exolab.castor.xml.util.JDOClassDescriptorResolver#getClassLoader()
     */
    public ClassLoader getClassLoader() {
        return _mappingLoader.getClassLoader();
    }
}
