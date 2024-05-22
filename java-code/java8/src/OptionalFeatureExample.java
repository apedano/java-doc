import java.util.Optional;
import java.util.function.Function;

public class OptionalFeatureExample {
    private static final Function<Optional<String>, Long> toLongOptional = sOpt ->
            sOpt.map(Long::parseLong).orElse(null);
    public static void main(String[] args) {
        assert toLongOptional.apply(Optional.of("123456")) != null;
        assert toLongOptional.apply(Optional.empty()) == null;
    }
}
