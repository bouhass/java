/**
 * 
 */
package bouhass.java.spring.scope;

/**
 * Static content holder class to store the brand identifier for threads.
 * Can be used in following way through AOP:
 * <pre>
 * @Before("execution(public * com.masabi.justride.Service.*(..)) && args(brandId, ..)")
 * public void setBrandId(final JoinPoint jp, final String brandId) {
 * 	BrandIdContextHolder.setBrandId(brandId);
 * }
 * </pre>
 *
 * @see ThreadLocal
 */
public final class BrandIdContextHolder {
 
    /** Stores the brandIds by thread. */
    private static final ThreadLocal<String> CONTEXT_HOLDER = new ThreadLocal<String>();
 
    /**
     * Stores the brand id for the current thread.
     *
     * @param The brand identifier.
     */
    public static void setBrandId(final String brandId) {
        CONTEXT_HOLDER.set(brandId);
    }
 
    /**
     * Returns the brand identifier having been set earlier in the thread. If there hasn't been <tt>brandId</tt> set so far,
     * the method will return <tt>null</tt>
     *
     * @return The brand identifier having been set earlier. (Nullable)
     */
    public static String getBrandId() {
        return CONTEXT_HOLDER.get();
    }
 
    /** Hidden constructor of a static content holder class. */
    private BrandIdContextHolder() { /* NOP */ }
 
}