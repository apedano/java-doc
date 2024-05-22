import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamFeatureExample {

    public static void main(String[] args) {
        String valuesGreaterThan20 = Stream.of(1, 222, 41, 6, 67, 109, 23, 77, 2)
                .filter(value -> value > 20)
                .map(Objects::toString)
                .collect(Collectors.joining("-"));
        System.out.println(valuesGreaterThan20);
    }

}
