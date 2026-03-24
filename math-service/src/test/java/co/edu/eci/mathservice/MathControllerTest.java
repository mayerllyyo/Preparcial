package co.edu.eci.mathservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MathControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void sinReturnsResult() {
        MathResult result = restTemplate.getForObject(
                "http://localhost:" + port + "/math/sin?value=0", MathResult.class);
        assertThat(result).isNotNull();
        assertThat(result.function()).isEqualTo("sin");
        assertThat(result.result()).isEqualTo("0.0");
    }

    @Test
    void cosReturnsResult() {
        MathResult result = restTemplate.getForObject(
                "http://localhost:" + port + "/math/cos?value=0", MathResult.class);
        assertThat(result).isNotNull();
        assertThat(result.result()).isEqualTo("1.0");
    }

    @Test
    void sqrtReturnsResult() {
        MathResult result = restTemplate.getForObject(
                "http://localhost:" + port + "/math/sqrt?value=4", MathResult.class);
        assertThat(result).isNotNull();
        assertThat(result.result()).isEqualTo("2.0");
    }

    @Test
    void sqrtNegativeReturnsError() {
        MathResult result = restTemplate.getForObject(
                "http://localhost:" + port + "/math/sqrt?value=-1", MathResult.class);
        assertThat(result).isNotNull();
        assertThat(result.result()).startsWith("Error");
    }

    @Test
    void factorialReturnsResult() {
        MathResult result = restTemplate.getForObject(
                "http://localhost:" + port + "/math/factorial?value=5", MathResult.class);
        assertThat(result).isNotNull();
        assertThat(result.result()).isEqualTo("120");
    }

    @Test
    void isPrimeReturnsTrue() {
        MathResult result = restTemplate.getForObject(
                "http://localhost:" + port + "/math/isprime?value=7", MathResult.class);
        assertThat(result).isNotNull();
        assertThat(result.result()).isEqualTo("true");
    }

    @Test
    void isPrimeReturnsFalse() {
        MathResult result = restTemplate.getForObject(
                "http://localhost:" + port + "/math/isprime?value=4", MathResult.class);
        assertThat(result).isNotNull();
        assertThat(result.result()).isEqualTo("false");
    }
}
