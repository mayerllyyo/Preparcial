package co.edu.eci.proxyservice;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Active-Passive load balancer.
 * Starts with instance 0 (active). On failure, switches to instance 1 (passive) and retries.
 * If the active instance recovers, it stays as active on next attempt.
 */
@Component
public class ActivePassiveLoadBalancer {

    private final String[] instances;
    private final AtomicInteger activeIndex = new AtomicInteger(0);

    public ActivePassiveLoadBalancer() {
        String url1 = System.getenv("MATH_SERVICE_1_URL");
        String url2 = System.getenv("MATH_SERVICE_2_URL");

        if (url1 == null || url1.isBlank()) {
            url1 = "http://localhost:8080";
        }
        if (url2 == null || url2.isBlank()) {
            url2 = "http://localhost:8081";
        }

        this.instances = new String[]{normalizeUrl(url1), normalizeUrl(url2)};
    }

    private String normalizeUrl(String url) {
        return url.stripTrailing().replaceAll("/$", "");
    }

    /** Returns the URL of the currently active instance. */
    public String getActive() {
        return instances[activeIndex.get()];
    }

    /** Returns the URL of the currently passive (backup) instance. */
    public String getPassive() {
        return instances[1 - activeIndex.get()];
    }

    /**
     * Switches active and passive roles.
     * Called when the active instance fails.
     */
    public void failover() {
        activeIndex.set(1 - activeIndex.get());
    }

    /** Expose the instance URLs for health/info endpoints. */
    public String[] getInstances() {
        return instances.clone();
    }
}
