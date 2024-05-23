import java.util.List;
import java.util.stream.Stream;

public class Java16StreamApi {

    private void mapMultiExample() {
        // We want to return a stream of string values
        // and the uppercase variant
        // if the original element has the letter o.
        assert Stream.of("Java", "Groovy", "Clojure")
                .mapMulti((language, downstream) -> {
                    if (language.contains("o")) {
                        downstream.accept(language);
                        downstream.accept(language.toUpperCase());
                    }
                })
                .toList()
                .equals(List.of("Groovy", "GROOVY", "Clojure", "CLOJURE"));

        // Same logic implemented with flatMap.
        assert Stream.of("Java", "Groovy", "Clojure")
                .filter(language -> language.contains("o"))
                .flatMap(language -> Stream.of(language, language.toUpperCase()))
                .toList()
                .equals(List.of("Groovy", "GROOVY", "Clojure", "CLOJURE"));


        // Helper record to store a name and set of language names.
        record Developer(String name, List<String> languages) {}

        // Number of sample developers that work with different languages.
        var hubert = new Developer("mrhaki", List.of("Java", "Groovy", "Clojure"));
        var java = new Developer("java", List.of("Java"));
        var clojure = new Developer("clojure", List.of("Clojure"));
        var groovy = new Developer("groovy", List.of("Groovy"));

        record Pair(String name, String language) {}

        // Let's find all developers that have Java in their
        // set of languages and return a new Pair
        // object with the name of the developer and a language.
        assert Stream.of(hubert, java, clojure, groovy)
                // We can explicitly state the class that will be
                // in the downstream of the compiler cannot
                // deduct it using a <...> syntax.
                .<Pair>mapMulti((developer, downstream) -> {
                    var languages = developer.languages();
                    if (languages.contains("Java")) {
                        for (String language : developer.languages()) {
                            downstream.accept(new Pair(developer.name(), language));
                        }
                    }
                })
                .toList()
                .equals(List.of(new Pair("mrhaki", "Java"),
                        new Pair("mrhaki", "Groovy"),
                        new Pair("mrhaki", "Clojure"),
                        new Pair("java", "Java")));

        // Same logic using filter and flatMap.
        assert Stream.of(hubert, java, clojure, groovy)
                .filter(developer -> developer.languages().contains("Java"))
                .flatMap(developer -> developer.languages()
                        .stream()
                        .map(language -> new Pair(developer.name(), language))
                )
                .toList()
                .equals(List.of(new Pair("mrhaki", "Java"),
                        new Pair("mrhaki", "Groovy"),
                        new Pair("mrhaki", "Clojure"),
                        new Pair("java", "Java")));


        // We want to expand each number to itself and its square root value
        // and we muse mapMultiToInt here.
        var summaryStatistics = Stream.of(1, 2, 3)
                .mapMultiToInt((number, downstream) -> {
                    downstream.accept(number);
                    downstream.accept(number * number);
                })
                .summaryStatistics();

        assert summaryStatistics.getCount() == 6;
        assert summaryStatistics.getSum() == 20;
        assert summaryStatistics.getMin() == 1;
        assert summaryStatistics.getMax() == 9;
    }

    public static void main(String[] args) {
        //toList
        List<Integer> ints = Stream.of(1, 2, 3)
                .filter(n -> n < 3)
                .toList();  // instead of .collect(Collectors.toList())
        //map multi
        // <R> Stream<R> mapMulti(BiConsumer<T, Consumer<R>> mapper)


    }
}
