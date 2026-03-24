package co.edu.eci.proxyservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ActivePassiveLoadBalancerTest {

    @Autowired
    private ActivePassiveLoadBalancer loadBalancer;

    @BeforeEach
    void resetToInitialState() {
        // Ensure active is always index 0 before each test
        String[] instances = loadBalancer.getInstances();
        if (!loadBalancer.getActive().equals(instances[0])) {
            loadBalancer.failover();
        }
    }

    @Test
    void initialActiveIsInstance1() {
        String active = loadBalancer.getActive();
        assertThat(active).isNotNull().isNotBlank();
        assertThat(active).isEqualTo(loadBalancer.getInstances()[0]);
    }

    @Test
    void failoverSwitchesActiveToPassive() {
        String initialActive = loadBalancer.getActive();
        String initialPassive = loadBalancer.getPassive();

        loadBalancer.failover();

        assertThat(loadBalancer.getActive()).isEqualTo(initialPassive);
        assertThat(loadBalancer.getPassive()).isEqualTo(initialActive);
    }

    @Test
    void instancesAreConfigured() {
        String[] instances = loadBalancer.getInstances();
        assertThat(instances).hasSize(2);
        assertThat(instances[0]).isNotBlank();
        assertThat(instances[1]).isNotBlank();
    }
}
