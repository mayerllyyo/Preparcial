package co.edu.eci.mathservice;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/math")
public class MathController {

    @GetMapping("/sin")
    public MathResult sin(@RequestParam double value) {
        return new MathResult("sin", value, String.valueOf(Math.sin(value)));
    }

    @GetMapping("/cos")
    public MathResult cos(@RequestParam double value) {
        return new MathResult("cos", value, String.valueOf(Math.cos(value)));
    }

    @GetMapping("/sqrt")
    public MathResult sqrt(@RequestParam double value) {
        if (value < 0) {
            return new MathResult("sqrt", value, "Error: cannot compute square root of a negative number");
        }
        return new MathResult("sqrt", value, String.valueOf(Math.sqrt(value)));
    }

    @GetMapping("/factorial")
    public MathResult factorial(@RequestParam int value) {
        if (value < 0) {
            return new MathResult("factorial", value, "Error: factorial is not defined for negative numbers");
        }
        if (value > 20) {
            return new MathResult("factorial", value, "Error: input too large (max 20)");
        }
        return new MathResult("factorial", value, String.valueOf(computeFactorial(value)));
    }

    @GetMapping("/isprime")
    public MathResult isPrime(@RequestParam int value) {
        return new MathResult("isPrime", value, String.valueOf(computeIsPrime(value)));
    }

    private long computeFactorial(int n) {
        long result = 1;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }

    private boolean computeIsPrime(int n) {
        if (n < 2) return false;
        if (n == 2) return true;
        if (n % 2 == 0) return false;
        for (int i = 3; i * i <= n; i += 2) {
            if (n % i == 0) return false;
        }
        return true;
    }
}
