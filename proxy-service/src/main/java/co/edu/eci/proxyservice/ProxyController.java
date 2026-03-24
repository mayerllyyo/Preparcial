package co.edu.eci.proxyservice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@RestController
@RequestMapping("/proxy")
public class ProxyController {

    private final ActivePassiveLoadBalancer loadBalancer;

    public ProxyController(ActivePassiveLoadBalancer loadBalancer) {
        this.loadBalancer = loadBalancer;
    }

    @GetMapping("/sin")
    public ResponseEntity<String> sin(@RequestParam double value) {
        return forwardRequest("/math/sin?value=" + value);
    }

    @GetMapping("/cos")
    public ResponseEntity<String> cos(@RequestParam double value) {
        return forwardRequest("/math/cos?value=" + value);
    }

    @GetMapping("/sqrt")
    public ResponseEntity<String> sqrt(@RequestParam double value) {
        return forwardRequest("/math/sqrt?value=" + value);
    }

    @GetMapping("/factorial")
    public ResponseEntity<String> factorial(@RequestParam int value) {
        return forwardRequest("/math/factorial?value=" + value);
    }

    @GetMapping("/isprime")
    public ResponseEntity<String> isPrime(@RequestParam int value) {
        return forwardRequest("/math/isprime?value=" + value);
    }

    @GetMapping("/status")
    public ResponseEntity<String> status() {
        String[] instances = loadBalancer.getInstances();
        String body = "{\"active\":\"" + loadBalancer.getActive() + "\","
                + "\"passive\":\"" + loadBalancer.getPassive() + "\","
                + "\"instance1\":\"" + instances[0] + "\","
                + "\"instance2\":\"" + instances[1] + "\"}";
        return ResponseEntity.ok(body);
    }

    /**
     * Forwards the request path to the active math-service instance.
     * If the active instance fails, performs a failover and retries on the passive instance.
     */
    private ResponseEntity<String> forwardRequest(String path) {
        try {
            String response = callService(loadBalancer.getActive() + path);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            // Active instance failed — failover and try passive
            loadBalancer.failover();
            try {
                String response = callService(loadBalancer.getActive() + path);
                return ResponseEntity.ok(response);
            } catch (IOException ex) {
                return ResponseEntity.status(503)
                        .body("{\"error\":\"Both math service instances are unavailable\"}");
            }
        }
    }

    private String callService(String targetUrl) throws IOException {
        URL url = new URL(targetUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(3000);
        connection.setReadTimeout(5000);
        connection.setRequestProperty("Accept", "application/json");

        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new IOException("Upstream returned HTTP " + responseCode);
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream()))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        }
    }
}
