import java.util.Optional;

public class OptionalApiOrElseThrow {
    private static Optional<Double> getNullOrValue() {
        var randomValue = Math.random();
        return randomValue <= 0.5 ? Optional.empty() :
                Optional.of(randomValue);
    }

    /**
     * new .orElseThrow()
     */
    public static void main(String[] args) {
        Optional<Double> optionalDouble = getNullOrValue();
        var double1 = optionalDouble.orElseThrow(IllegalStateException::new);
    }
}