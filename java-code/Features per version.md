# Java features per version

<!-- TOC -->
* [Java features per version](#java-features-per-version)
  * [Java 8 features](#java-8-features)
    * [Lambda expressions](#lambda-expressions)
    * [Method reference](#method-reference)
    * [Stream API](#stream-api)
    * [Optional classes](#optional-classes)
    * [New Date and Time API](#new-date-and-time-api)
    * [Default methods](#default-methods)
    * [Parallel stream](#parallel-stream)
    * [Collectors](#collectors)
  * [Java 9 features](#java-9-features)
<!-- TOC -->

## Java 8 features

### Lambda expressions

Enables functional programming by allowing the use of anonymous functions.
Concise syntax for writing functional interfaces.

### Method reference

Provides a shorthand notation for lambda expressions.
Allows referring to methods or constructors using the `::` operator.

```java
public class LambdaExpressions {

    private static final Function<Long, String> valueOfLambda = s -> {
        System.out.println("The input is " + s);
        return s.toString();
    };

    private static final Function<Long, String> valueOfMethodReference = Object::toString;

    public static void main(String[] args) {
        System.out.println(LambdaExpressions.valueOfLambda.apply(123L));
        System.out.println(LambdaExpressions.valueOfMethodReference.apply(456L));
    }
}
```

### Stream API

Introduces a new abstraction called Stream for processing sequences of elements.
Supports functional-style operations on streams like filter, map, reduce, etc.

```java
public class StreamFeatureExample {
    /*
         output: 222-41-67-109-23-77
    */
    public static void main(String[] args) {
        String valuesGreaterThan20 = Stream.of(1, 222, 41, 6, 67, 109, 23, 77, 2)
                .filter(value -> value > 20)
                .map(Objects::toString)
                .collect(Collectors.joining("-"));
        System.out.println(valuesGreaterThan20);
    }
}
```

### Optional classes

A container object that may or may not contain a non-null value.
Helps to handle null checks more effectively and avoids NullPointerExceptions.

```java
public class OptionalFeatureExample {
    private static final Function<Optional<String>, Long> toLongOptional = sOpt ->
            sOpt.map(Long::parseLong).orElse(null);
    public static void main(String[] args) {
        assert toLongOptional.apply(Optional.of("123456")) != null;
        assert toLongOptional.apply(Optional.empty()) == null;
    }
}
```

### New Date and Time API

* **Thread safety** – The Date and Calendar classes are not thread safe, leaving developers to deal with the headache of hard-to-debug concurrency issues and to write additional code to handle thread safety. On the contrary, the new Date and Time APIs introduced in Java 8 are immutable and thread safe, thus taking that concurrency headache away from developers.
* **API design and ease of understanding** – The Date and Calendar APIs are poorly designed with inadequate methods to perform day-to-day operations. The new Date/Time API is ISO-centric and follows consistent domain models for date, time, duration and periods. There are a wide variety of utility methods that support the most common operations.
* **LocalDate and Time, ZonedDate and Time** – Developers had to write additional logic to handle time-zone logic with the old APIs, whereas with the new APIs, handling of time zone can be done with Local and ZonedDate/Time APIs.

https://www.baeldung.com/java-8-date-time-intro

### Default methods

Allows interfaces to have method implementations.
Helps in evolving interfaces without breaking existing implementations.

```java
interface TestInterface1 {
    // default method
    default void show() {
        System.out.println("Default TestInterface1");
    }
}
```

### Parallel stream

Allows parallel processing of streams using the `parallel()` method.
Enhances performance on multi-core systems for certain types of operations.

### Collectors

Introduces a set of utility methods in the `Collectors` class for common reduction operations,
such as `toList()`, `toSet()`, `joining()`, etc.

## Java 9 features

### Improved Process API

Enhancements to the `Process` API, providing better control over native processes. 
The new `ProcessHandle` class allows developers to interact with processes and obtain information about them.

```java
/*
        Process ID: 14184
        Is alive? true
     */
public static void main(String[] args) {
  ProcessHandle currentProcess = ProcessHandle.current();
  System.out.println("Process ID: " + currentProcess.pid());
  System.out.println("Is alive? " + currentProcess.isAlive());
}
```

### Collections factory methods

Java 9 added new static factory methods to the collection interfaces (`List`, `Set`, `Map`, etc.), making it more convenient to create immutable instances of these collections.

```java
List<String> colors = List.of("Red", "Green", "Blue");
```

### Improved Stream API

The Stream API was enhanced with several new methods, such as `takeWhile`, `dropWhile`, and `ofNullable`, 
which improve the flexibility and functionality of working with streams.

[ImprovedStreamAPIFeatureExample.java](java9%2Fsrc%2FImprovedStreamAPIFeatureExample.java)

### Private Methods in Interfaces

```java
// Interface with private method
public interface PrivateMethodInterface {
  default void publicMethod() {
    // Public method can call private method
    privateMethod();
  }

  private void privateMethod() {
    System.out.println("Private method in interface");
  }
}
```

### HTTP/2 Client

Java 9 introduced a new lightweight `java.net.http.HttpClient`  that supports HTTP/2 and WebSocket . 
This client is designed to be more efficient and flexible than the old `HttpURLConnection` API.

```java
public static void main(String[] args) throws Exception {
    HttpClient httpClient = HttpClient.newHttpClient();
    HttpRequest httpRequest = HttpRequest.newBuilder()
            .uri(new URI("https://www.google.com"))
            .GET()
            .build();

    HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
    System.out.println("Response Code: " + response.statusCode());
    System.out.println("Response Body: " + response.body());
}
```

