package bouhass.java.spring.scope;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.Scope;

public class BrandScope implements Scope {
 
    /** Cached beans by brandIds and bean ids. */
    private final Map<String, Map<String, Object>> brands = new HashMap<String, Map<String, Object>>();
 
    /** Brand configuration service to retrieve the brand configuration to an identifier. (NonNull) */
    protected IBrandConfigService brandConfigService;
 
    /**
     * @param brandConfigService Brand configuration service to retrieve the brand configuration to an identifier. (NonNull)
     */
    @Autowired
    public void setBrandConfigService(final IBrandConfigService brandConfigService) {
        this.brandConfigService = brandConfigService;
    }
 
    /** {@inheritDoc} */
    @Override
    public Object get(final String name, final ObjectFactory<?> objectFactory) {
        /* Retrieving the current brand identifier. */
        final String brandId = BrandIdContextHolder.getBrandId();
 
        /* Retrieving the related brand configuration. */
        final BrandConfig brandConfig = getBrandConfig(brandId);
 
        /* Retrieving (or creating) the brand beans map. */
        final Map<String, Object> beans = getBeans(brandId);
 
        /* Retrieving (or creating) the desired object. */
        Object bean;
        synchronized (beans) {
            bean = beans.get(name);
            if (bean == null) {
                bean = loadBean(name, objectFactory, brandConfig);
                beans.put(name, bean);
            }
        }
 
        return bean;
    }
 
    /**
     * Returns the brand configuration to the brandId.
     *
     * @param brandId The brand identifier. (Nullable)
     * @return The brand configuration.
     * @throws IllegalStateException If the brandId is not registered in the brandConfigService.
     */
    private BrandConfig getBrandConfig(final String brandId) {
        final BrandConfig brandConfig = brandId == null ? null : brandConfigService.getBrandConfig(brandId);
 
        if (brandConfig == null) {
            //Do not return null. The documentation says: the desired object (never null)
            throw new IllegalStateException("The requested brand is not supported. BrandId: " + brandId);
        }
        return brandConfig;
    }
 
    /**
     * <p>Returns the beans map to the brand identifier.
     * If the map is not generated so far, the method will generate a new one and stores into the cache.</p>
     *
     * <p>This method is thread safe.</p>
     *
     * @param brandId The brand identifier. (NonNull)
     * @return The bean map storing the beans of the context of the brand.
     */
    private Map<String, Object> getBeans(final String brandId) {
 
        Map<String, Object> beans;
        synchronized (brands) {
            beans = brands.get(brandId);
            if (beans == null) {
                beans = new HashMap<String, Object>();
                brands.put(brandId, beans);
            }
        }
        return beans;
    }
 
    /**
     * Loads the desired bean. Overriding this method the object creation can be changed.
     *
     * @param name The name of the bean.
     * @param objectFactory The {@link ObjectFactory} to create the specified bean.
     * @param brandConfig The configuration.
     * @return The desired bean. (never null)
     */
    protected Object loadBean(final String name, final ObjectFactory<?> objectFactory, final BrandConfig brandConfig) {
        if (name.endsWith("brandConfig")) {
            return brandConfig;
        }
        final Object bean = objectFactory.getObject();
        return bean;
    }
 
    /** {@inheritDoc} */
    @Override
    public String getConversationId() {
        return BrandIdContextHolder.getBrandId();
    }
 
    /** {@inheritDoc} */
    @Override
    public Object remove(final String name) {
        /* Retrieving the current brand identifier. */
        final String brandId = BrandIdContextHolder.getBrandId();
 
        if (brandId != null) {
            /* Retrieving (or creating) the brand beans map. */
            final Map<String, Object> beans = getBeans(brandId);
 
            return beans.remove(name);
        }
        return null;
    }
 
    /** {@inheritDoc} */
    @Override
    public void registerDestructionCallback(final String name, final Runnable callback) {
        // we should not need that as the brand scope should never die
    }
 
    /** {@inheritDoc} */
    @Override
    public Object resolveContextualObject(final String key) {
        throw new UnsupportedOperationException("The resolveContextualObject(String) is not implemented in BrandScope. Key: " + key);
    }
}