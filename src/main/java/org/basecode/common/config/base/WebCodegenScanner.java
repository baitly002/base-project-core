package org.basecode.common.config.base;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.basecode.common.criterion.exception.BusinessException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.*;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.core.env.EnvironmentCapable;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.PatternMatchUtils;

import java.util.LinkedHashSet;
import java.util.Set;


public class WebCodegenScanner extends WebCodegenClassPathScanningCandidateComponentProvider {

    private final BeanDefinitionRegistry registry;

    private BeanDefinitionDefaults beanDefinitionDefaults = new BeanDefinitionDefaults();

    @Nullable
    private String[] autowireCandidatePatterns;

    private BeanNameGenerator beanNameGenerator = new AnnotationBeanNameGenerator();

    private ScopeMetadataResolver scopeMetadataResolver = new AnnotationScopeMetadataResolver();

    private boolean includeAnnotationConfig = true;


    protected final Log logger = LogFactory.getLog(getClass());

    private String resourcePattern = "**/*.class";

    @Nullable
    private ResourcePatternResolver resourcePatternResolver;


    /**
     * Create a new {@code ClassPathBeanDefinitionScanner} for the given bean factory.
     * @param registry the {@code BeanFactory} to load bean definitions into, in the form
     * of a {@code BeanDefinitionRegistry}
     */
    public WebCodegenScanner(BeanDefinitionRegistry registry) {
        this(registry, true);
    }



    public WebCodegenScanner(BeanDefinitionRegistry registry, boolean useDefaultFilters) {
        this(registry, useDefaultFilters, getOrCreateEnvironment(registry));
    }


    public WebCodegenScanner(BeanDefinitionRegistry registry, boolean useDefaultFilters,
                                          Environment environment) {

        this(registry, useDefaultFilters, environment,
                (registry instanceof ResourceLoader ? (ResourceLoader) registry : null));
    }


    public WebCodegenScanner(BeanDefinitionRegistry registry, boolean useDefaultFilters,
                                          Environment environment, @Nullable ResourceLoader resourceLoader) {

        Assert.notNull(registry, "BeanDefinitionRegistry must not be null");
        this.registry = registry;

        if (useDefaultFilters) {
            registerDefaultFilters();
        }
        setEnvironment(environment);
        setResourceLoader(resourceLoader);
    }


    /**
     * Return the BeanDefinitionRegistry that this scanner operates on.
     */
    @Override
    public final BeanDefinitionRegistry getRegistry() {
        return this.registry;
    }


    public void setBeanDefinitionDefaults(@Nullable BeanDefinitionDefaults beanDefinitionDefaults) {
        this.beanDefinitionDefaults =
                (beanDefinitionDefaults != null ? beanDefinitionDefaults : new BeanDefinitionDefaults());
    }

    /**
     * Return the defaults to use for detected beans (never {@code null}).
     * @since 4.1
     */
    public BeanDefinitionDefaults getBeanDefinitionDefaults() {
        return this.beanDefinitionDefaults;
    }

    /**
     * Set the name-matching patterns for determining autowire candidates.
     * @param autowireCandidatePatterns the patterns to match against
     */
    public void setAutowireCandidatePatterns(@Nullable String... autowireCandidatePatterns) {
        this.autowireCandidatePatterns = autowireCandidatePatterns;
    }


    public void setBeanNameGenerator(@Nullable BeanNameGenerator beanNameGenerator) {
        this.beanNameGenerator = (beanNameGenerator != null ? beanNameGenerator : new AnnotationBeanNameGenerator());
    }


    public void setScopeMetadataResolver(@Nullable ScopeMetadataResolver scopeMetadataResolver) {
        this.scopeMetadataResolver =
                (scopeMetadataResolver != null ? scopeMetadataResolver : new AnnotationScopeMetadataResolver());
    }


    public void setScopedProxyMode(ScopedProxyMode scopedProxyMode) {
        this.scopeMetadataResolver = new AnnotationScopeMetadataResolver(scopedProxyMode);
    }

    /**
     * Specify whether to register annotation config post-processors.
     * <p>The default is to register the post-processors. Turn this off
     * to be able to ignore the annotations or to process them differently.
     */
    public void setIncludeAnnotationConfig(boolean includeAnnotationConfig) {
        this.includeAnnotationConfig = includeAnnotationConfig;
    }


    /**
     * Perform a scan within the specified base packages.
     * @param basePackages the packages to check for annotated classes
     * @return number of beans registered
     */
    public int scan(String... basePackages) {
        int beanCountAtScanStart = this.registry.getBeanDefinitionCount();

        doScan(basePackages);

        // Register annotation config processors, if necessary.
        if (this.includeAnnotationConfig) {
            AnnotationConfigUtils.registerAnnotationConfigProcessors(this.registry);
        }

        return (this.registry.getBeanDefinitionCount() - beanCountAtScanStart);
    }

    /**
     * Perform a scan within the specified base packages,
     * returning the registered bean definitions.
     * <p>This method does <i>not</i> register an annotation config processor
     * but rather leaves this up to the caller.
     * @param basePackages the packages to check for annotated classes
     * @return set of beans registered if any for tooling registration purposes (never {@code null})
     */
    protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
        Assert.notEmpty(basePackages, "At least one base package must be specified");
        Set<BeanDefinitionHolder> beanDefinitions = new LinkedHashSet<>();
        for (String basePackage : basePackages) {
            Set<BeanDefinition> candidates = findCandidateComponents(basePackage);
            for (BeanDefinition candidate : candidates) {
                ScopeMetadata scopeMetadata = this.scopeMetadataResolver.resolveScopeMetadata(candidate);
                candidate.setScope(scopeMetadata.getScopeName());
                String beanName = this.beanNameGenerator.generateBeanName(candidate, this.registry);
                if (candidate instanceof AbstractBeanDefinition) {
                    postProcessBeanDefinition((AbstractBeanDefinition) candidate, beanName);
                }
                if (candidate instanceof AnnotatedBeanDefinition) {
                    AnnotationConfigUtils.processCommonDefinitionAnnotations((AnnotatedBeanDefinition) candidate);
                }
//                    if (checkCandidate(beanName, candidate)) {
//                        BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(candidate, beanName);
//                        definitionHolder =
//                                AnnotationConfigUtils.applyScopedProxyMode(scopeMetadata, definitionHolder, this.registry);
//                        beanDefinitions.add(definitionHolder);
//                        registerBeanDefinition(definitionHolder, this.registry);
//                    }
            }
        }
        return beanDefinitions;
    }

    /**
     * Apply further settings to the given bean definition,
     * beyond the contents retrieved from scanning the component class.
     * @param beanDefinition the scanned bean definition
     * @param beanName the generated bean name for the given bean
     */
    protected void postProcessBeanDefinition(AbstractBeanDefinition beanDefinition, String beanName) {
        beanDefinition.applyDefaults(this.beanDefinitionDefaults);
        if (this.autowireCandidatePatterns != null) {
            beanDefinition.setAutowireCandidate(PatternMatchUtils.simpleMatch(this.autowireCandidatePatterns, beanName));
        }
    }

    /**
     * Register the specified bean with the given registry.
     * <p>Can be overridden in subclasses, e.g. to adapt the registration
     * process or to register further bean definitions for each scanned bean.
     * @param definitionHolder the bean definition plus bean name for the bean
     * @param registry the BeanDefinitionRegistry to register the bean with
     */
    protected void registerBeanDefinition(BeanDefinitionHolder definitionHolder, BeanDefinitionRegistry registry) {
        BeanDefinitionReaderUtils.registerBeanDefinition(definitionHolder, registry);
    }



    protected boolean checkCandidate(String beanName, BeanDefinition beanDefinition) throws IllegalStateException {
        if (!this.registry.containsBeanDefinition(beanName)) {
            return true;
        }
        BeanDefinition existingDef = this.registry.getBeanDefinition(beanName);
        BeanDefinition originatingDef = existingDef.getOriginatingBeanDefinition();
        if (originatingDef != null) {
            existingDef = originatingDef;
        }
        if (isCompatible(beanDefinition, existingDef)) {
            return false;
        }
        throw new BusinessException("Annotation-specified bean name '" + beanName +
                "' for bean class [" + beanDefinition.getBeanClassName() + "] conflicts with existing, " +
                "non-compatible bean definition of same name and class [" + existingDef.getBeanClassName() + "]");
    }

    /**
     * Determine whether the given new bean definition is compatible with
     * the given existing bean definition.
     * <p>The default implementation considers them as compatible when the existing
     * bean definition comes from the same source or from a non-scanning source.
     * @param newDefinition the new bean definition, originated from scanning
     * @param existingDefinition the existing bean definition, potentially an
     * explicitly defined one or a previously generated one from scanning
     * @return whether the definitions are considered as compatible, with the
     * new definition to be skipped in favor of the existing definition
     */
    protected boolean isCompatible(BeanDefinition newDefinition, BeanDefinition existingDefinition) {
        return (!(existingDefinition instanceof ScannedGenericBeanDefinition) ||  // explicitly registered overriding bean
                (newDefinition.getSource() != null && newDefinition.getSource().equals(existingDefinition.getSource())) ||  // scanned same file twice
                newDefinition.equals(existingDefinition));  // scanned equivalent class twice
    }


    /**
     * Get the Environment from the given registry if possible, otherwise return a new
     * StandardEnvironment.
     */
    private static Environment getOrCreateEnvironment(BeanDefinitionRegistry registry) {
        Assert.notNull(registry, "BeanDefinitionRegistry must not be null");
        if (registry instanceof EnvironmentCapable) {
            return ((EnvironmentCapable) registry).getEnvironment();
        }
        return new StandardEnvironment();
    }

//    /**
//     * Scan the class path for candidate components.
//     * @param basePackage the package to check for annotated classes
//     * @return a corresponding Set of autodetected bean definitions
//     */
//    public Set<BeanDefinition> findCandidateComponents(String basePackage) {
////        if (this.componentsIndex != null && indexSupportsIncludeFilters()) {
////            return addCandidateComponentsFromIndex(this.componentsIndex, basePackage);
////        }
////        else {
//            return scanCandidateComponents(basePackage);
////        }
//    }
//    private boolean indexSupportsIncludeFilter(TypeFilter filter) {
//        if (filter instanceof AnnotationTypeFilter) {
//            Class<? extends Annotation> annotation = ((AnnotationTypeFilter) filter).getAnnotationType();
//            return (AnnotationUtils.isAnnotationDeclaredLocally(Indexed.class, annotation) ||
//                    annotation.getName().startsWith("javax."));
//        }
//        if (filter instanceof AssignableTypeFilter) {
//            Class<?> target = ((AssignableTypeFilter) filter).getTargetType();
//            return AnnotationUtils.isAnnotationDeclaredLocally(Indexed.class, target);
//        }
//        return false;
//    }
//
//    private ResourcePatternResolver getResourcePatternResolver() {
//        if (this.resourcePatternResolver == null) {
//            this.resourcePatternResolver = new PathMatchingResourcePatternResolver();
//        }
//        return this.resourcePatternResolver;
//    }
//
//    private Set<BeanDefinition> scanCandidateComponents(String basePackage) {
//        Set<BeanDefinition> candidates = new LinkedHashSet<>();
//        try {
//            String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
//                    resolveBasePackage(basePackage) + '/' + this.resourcePattern;
//            Resource[] resources = getResourcePatternResolver().getResources(packageSearchPath);
//            boolean traceEnabled = logger.isTraceEnabled();
//            boolean debugEnabled = logger.isDebugEnabled();
//            for (Resource resource : resources) {
//                if (traceEnabled) {
//                    logger.trace("Scanning " + resource);
//                }
//                if (resource.isReadable()) {
//                    try {
//                        MetadataReader metadataReader = getMetadataReaderFactory().getMetadataReader(resource);
//                        if (isCandidateComponent(metadataReader)) {
//                            ScannedGenericBeanDefinition sbd = new ScannedGenericBeanDefinition(metadataReader);
//                            sbd.setResource(resource);
//                            sbd.setSource(resource);
//                            if (isCandidateComponent(sbd)) {
//                                if (debugEnabled) {
//                                    logger.debug("Identified candidate component class: " + resource);
//                                }
//                                candidates.add(sbd);
//                            }
//                            else {
//                                if (debugEnabled) {
//                                    logger.debug("Ignored because not a concrete top-level class: " + resource);
//                                }
//                            }
//                        }
//                        else {
//                            if (traceEnabled) {
//                                logger.trace("Ignored because not matching any filter: " + resource);
//                            }
//                        }
//                    }
//                    catch (Throwable ex) {
//                        throw new BeanDefinitionStoreException(
//                                "Failed to read candidate component class: " + resource, ex);
//                    }
//                }
//                else {
//                    if (traceEnabled) {
//                        logger.trace("Ignored because not readable: " + resource);
//                    }
//                }
//            }
//        }
//        catch (IOException ex) {
//            throw new BeanDefinitionStoreException("I/O failure during classpath scanning", ex);
//        }
//        return candidates;
//    }
}
