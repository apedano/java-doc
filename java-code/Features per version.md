# Java features per version

https://levelup.gitconnected.com/a-comprehensive-journey-from-java-8-to-java-21-with-code-examples-of-essential-api-enhancements-6817d2ab3ba8


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
        * [Improved Process API](#improved-process-api)
        * [Collections factory methods](#collections-factory-methods)
        * [Improved Stream API](#improved-stream-api)
        * [Private Methods in Interfaces](#private-methods-in-interfaces)
    * [Java 10 features](#java-10-features)
        * [Local-Variable Type Inference (`var`)](#local-variable-type-inference-var)
        * [Optional API — new methods introduced](#optional-api--new-methods-introduced)
    * [Java 11 features](#java-11-features)
        * [HTTP/2 Client](#http2-client)
        * [New ``java.nio.file`` methods](#new-javaniofile-methods)
    * [Java 12 Features](#java-12-features)
        * [Compact Number Formatting](#compact-number-formatting)
        * [`String::indent`](#stringindent)
        * [New Methods in `java.util.Arrays`](#new-methods-in-javautilarrays)
    * [Java 13 features](#java-13-features)
    * [Java 14 features](#java-14-features)
        * [“Switch Expressions” (SE) instead of “Switch Statements” (SS)](#switch-expressions-se-instead-of-switch-statements-ss)
        * [“Yield” Statement](#yield-statement)
    * [Java 15 features](#java-15-features)
        * [Text block](#text-block)
    * [Java 16 features](#java-16-features)
        * [Patterm matching for `instanceof`](#patterm-matching-for-instanceof)
        * [Record](#record)

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

* **Thread safety** – The Date and Calendar classes are not thread safe, leaving developers to deal with the headache of
  hard-to-debug concurrency issues and to write additional code to handle thread safety. On the contrary, the new Date
  and Time APIs introduced in Java 8 are immutable and thread safe, thus taking that concurrency headache away from
  developers.
* **API design and ease of understanding** – The Date and Calendar APIs are poorly designed with inadequate methods to
  perform day-to-day operations. The new Date/Time API is ISO-centric and follows consistent domain models for date,
  time, duration and periods. There are a wide variety of utility methods that support the most common operations.
* **LocalDate and Time, ZonedDate and Time** – Developers had to write additional logic to handle time-zone logic with
  the old APIs, whereas with the new APIs, handling of time zone can be done with Local and ZonedDate/Time APIs.

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
public static void main(String[]args){
        ProcessHandle currentProcess=ProcessHandle.current();
        System.out.println("Process ID: "+currentProcess.pid());
        System.out.println("Is alive? "+currentProcess.isAlive());
        }
```

### Collections factory methods

Java 9 added new static factory methods to the collection interfaces (`List`, `Set`, `Map`, etc.), making it more
convenient to create immutable instances of these collections.

```java
List<String> colors=List.of("Red","Green","Blue");
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

## Java 10 features

### Local-Variable Type Inference (`var`)

Java 10 introduced the ability to use the `var` keyword for **local variable type inference**.
This allows developers to declare local variables without explicitly specifying the type,
letting the compiler infer it based on the assigned value.

```java
public static void main(String[]args){
        // allowed, but brings little benefit
        var b="b";
        var c=5; // int
        var d=5.0; // double
        var httpClient=HttpClient.newHttpClient();

        // one hell of an inference :)
        var list=List.of(1,2.0,"3");

        // the benefit becomes more evident with types with long names
        var reader=new BufferedReader(null);
        // vs.
        BufferedReader reader2=new BufferedReader(null);
        }
```

### Optional API — new methods introduced

```java
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
```

## Java 11 features

### HTTP/2 Client

Java 9 introduced a new lightweight `java.net.http.HttpClient`  that supports HTTP/2 and WebSocket .
This client is designed to be more efficient and flexible than the old `HttpURLConnection` API.

```java
public static void main(String[]args)throws Exception{
        HttpClient httpClient=HttpClient.newHttpClient();
        HttpRequest httpRequest=HttpRequest.newBuilder()
        .uri(new URI("https://www.google.com"))
        .GET()
        .build();

        HttpResponse<String> response=httpClient.send(httpRequest,HttpResponse.BodyHandlers.ofString());
        System.out.println("Response Code: "+response.statusCode());
        System.out.println("Response Body: "+response.body());
        }
```

### New ``java.nio.file`` methods

Java 11 introduced several new methods in the `java.nio.file.Files` package, providing additional functionality for
working
with files and directories. Some of the notable methods include:

* ``Files.readString(Path path)`` and ``Files.writeString(Path path, CharSequence content, OpenOption... options)``
* ``List<String> Files.readAllLines(Path path)``
  and ``Files.write(Path path, Iterable<? extends CharSequence> lines, OpenOption... options)``
* ``Files.newBufferedReader(Path path)`` and ``Files.newBufferedWriter(Path path, OpenOption... options)`` to work with
  char streams.
* ``Files.mismatch(Path path1, Path path2)``  compares the content of two files and returns the position of the first
  mismatched byte.
  If the files are identical, it returns -1

```java
public class FilesFeatureExample {
    static String filePath = System.getProperty("user.dir") + "/java-code/java11/resources/";
    static String file_1 = filePath + "file_1.txt";

    /**
     * Files.readString() and .writeString()
     */
    public static void main(String[] args) throws IOException {

        // reading files is much easier now
        // not to be used with huge files
        Path path = Paths.get(file_1);
        String content = Files.readString(path);
        System.out.println(content);


        Path newFile = Paths.get(filePath + "newFile.txt");
        if (!Files.exists(newFile)) {
            Files.writeString(newFile, "some str", StandardOpenOption.CREATE);
        } else {
            Files.writeString(newFile, "some str", StandardOpenOption.TRUNCATE_EXISTING);
        }
    }
}
```

## Java 12 Features

### Compact Number Formatting

New feature called “_**Compact Number Formatting**_” as part of JEP 357.
This enhancement provides a **more concise way to format large numbers in a locale-specific manner**.

The **NumberFormat** class in the `java.text` package was enhanced to support the new `Style` enum,
including the `Style.SHORT` and `Style.LONG` constants.
These styles can be used to format large numbers in a compact form based on the specified locale.

```java
public static void main(String[]args){
        // Creating a number formatter with compact style
        NumberFormat compactFormatter=NumberFormat.getCompactNumberInstance(Locale.US,NumberFormat.Style.SHORT);

        // Formatting large numbers
        System.out.println("Short Format: "+compactFormatter.format(1000));  // Output: 1K
        System.out.println("Short Format: "+compactFormatter.format(1000000));  // Output: 1M

        // Creating a number formatter with compact style (long)
        NumberFormat compactLongFormatter=NumberFormat.getCompactNumberInstance(Locale.US,NumberFormat.Style.LONG);

        // Formatting large numbers in long style
        System.out.println("Long Format: "+compactLongFormatter.format(10000000));  // Output: 10 million
        System.out.println("Long Format: "+compactLongFormatter.format(1000000000));  // Output: 1 billion
        }
```

### `String::indent`

The String class in Java 12 introduced a new method called `indent(int n)`.
This method is used to adjust the indentation of each line in a string by a specified number of spaces.

```java
String indentedString="Hello\nWorld".indent(3);
// indentedString is now "   Hello\n   World"
```

### New Methods in `java.util.Arrays`

```java
public class NewMethodsArraysFeatureExample {

    private static final Comparator<Plane> MODEL_ONLY_PLANE_COMPARATOR =
            Comparator.comparing(p -> p.model);

    private static class Plane {
        private final String name;
        private final String model;

        public Plane(String name, String model) {
            this.name = name;
            this.model = model;
        }
    }

    public static void main(String[] args) {
        var array1 = new int[]{1, 2, 3, 4, 5, 6};
        //zero based index
        var copyRange = Arrays.copyOfRange(array1, 2, 4);
        System.out.println(Arrays.toString(copyRange));
        var planes1 = new Plane[]{new Plane("Plane 1", "A320"), new Plane("Plane 2", "B738")};
        var planes2 = new Plane[]{new Plane("Plane 3", "A320"), new Plane("Plane 4", "B738")};
        System.out.println(Arrays.equals(planes1, planes2, MODEL_ONLY_PLANE_COMPARATOR)); //true
    }
}
```

## Java 13 features

Nothing much interesting happened:

* API update to ByteBuffer
* Update to localization (support for new chars and emojis)
* GC updates

## Java 14 features

### “Switch Expressions” (SE) instead of “Switch Statements” (SS)

Switch expressions, introduced as a preview feature in Java 12 and finalized in Java 13, allow developers to use switch
statements as expressions,
providing a more concise and expressive syntax.

```java
int dayOfWeek=2;
        String dayType=switch(dayOfWeek){
        case 1,2,3,4,5->"Weekday";
        case 6,7->"Weekend";
default ->throw new IllegalArgumentException("Invalid day of the week: "+dayOfWeek);
        };
```

### “Yield” Statement

The “yield” statement was introduced in Java 14 to complement switch expressions.
It **allows you to specify a value to be returned from a switch arm**,
providing more flexibility in combining both imperative and functional styles.

```java
String dayType=switch(dayOfWeek){
        case 1,2,3,4,5->{
        System.out.println("Working day");
        yield"Weekday";
        }
        case 6,7->{
        System.out.println("Weekend");
        yield"Weekend";
        }
default ->throw new IllegalArgumentException("Invalid day of the week: "+dayOfWeek);
        };
```

```java

private static void switchExpressionWithReturn(FruitType fruit){
        print("==== With return value ====");

        // or just "return switch" right away
        String text=switch(fruit){
        case APPLE,PEAR->"Common fruit";
        case PINEAPPLE->"Exotic fruit";
default ->"Undefined fruit";
        };
        print(text);
        }

/**
 * "Yield" is like "return" but with an important difference:
 * "yield" returns a value and exits the switch statement. Execution stays within the enclosing method
 * "return" exits the switch and the enclosing method
 */
// https://stackoverflow.com/questions/58049131/what-does-the-new-keyword-yield-mean-in-java-13
private static void switchWithYield(FruitType fruit){
        print("==== With yield ====");
        String text=switch(fruit){
        case APPLE,PEAR->{
        print("the given fruit was: "+fruit);
        yield"Common fruit";
        }
        case PINEAPPLE->"Exotic fruit";
default ->"Undefined fruit";
        };
        print(text);
        }
```

## Java 15 features

### Text block

Text blocks are a new kind of string literals that span multiple lines. They aim to simplify the task of writing and
maintaining strings that span several lines of source code while avoiding escape sequences.

Example without text blocks:

```java
String html="<html>\n"+
        "    <body>\n"+
        "        <p>Hello, world</p>\n"+
        "    </body>\n"+
        "</html>";
```

Example with text blocks:

```java
String html="""
              <html>
                  <body>
                      <p>Hello, world</p>
                  </body>
              </html>
              """;
```

ey features of text blocks include:

* **Multiline Strings**: Text blocks allow you to represent multiline strings more naturally, improving code
  readability.
* **Whitespace Control**: Leading and trailing whitespaces on each line are removed, providing better control over the
  indentation.
* **Escape Sequences**: Escape sequences are still valid within text blocks, allowing the inclusion of special
  characters.

## Java 16 features

### Patterm matching for `instanceof`

Java 16’s pattern matching for `instanceof` is a nifty feature that improves type checking and extraction.

* Introduces type patterns instead of just checking against a single type.
* Allows declaring a variable within the instanceof check to hold the extracted object.
* Combines type checking and casting into a single, more concise and readable expression.

```java

public class PatternMatchingForInstanceof {

    public static void main(String[] args) {

        Object o = new Book("Harry Potter", Set.of("Jon Doe"));

        // old way
        if (o instanceof Book) {
            Book book = (Book) o;
            if (book.getAuthors().size() > 0) {
                print("The book's author(s) are " + book.getAuthors());
            }
        }

        // new way
        if (o instanceof Book book && book.getAuthors().size() > 0) {
            print("The book's author(s) are " + book.getAuthors());
        }
    }
}
```

### Record

Records in Java are a special type of class **specifically designed for holding immutable data**.
They help reduce boilerplate code and improve readability and maintainability when dealing with simple data structures.

Here’s a breakdown of their key characteristics:

1. Conciseness:

Unlike traditional classes, **records require minimal code to define**.
You just specify the data fields (components) in the record declaration, and the compiler automatically
generates essential methods like:

* Constructor with parameters for each component.
* Getters for each component.
* `equals` and `hashCode` methods based on component values.
* `toString` method representing the record's state.

2. Immutability:

Record fields are declared as `final`, making the data stored within them **unmodifiable after the record is created**.
This ensures data consistency and simplifies thread safety concerns.

```java
/**
 * Record are data-only immutable classes (thus have specific use cases)
 * They are a restricted (specialized) form of a class (like enums)
 * Not suitable for objects that are meant to change state, etc.
 * <p>
 * Records are NOT:
 * - Boilerplate reduction mechanism
 * <p>
 * Records generate constructors, getters, fields; equals, hashCode, toString
 * <p>
 * Use cases:
 * - to model immutable data
 * - to hold read-only data in memory
 * - DTOs - Data Transfer Objects
 */
public class RecordsDemo {

    public static void main(String[] args) {
        Product p1 = new Product("milk", 50);
        Product p2 = new Product("milk", 50);

        print(p1.price()); // without "get" prefix
        print(p1);         // auto-generated toString() - Product[name=milk, price=50]

        print(p1 == p2);       // false    - different objects
        print(p1.equals(p2));  // true     - values of fields (milk, 50) are compared by the auto-generated equals()/hashCode()
    }
}
```

Another example

```java
/**
 * params are called "Components"
 * want more fields - must add into the signature
 * Extending not allowed, implementing interfaces IS allowed
 */
public record Product(String name, int price) {

    // static fields allowed, but not non-static
    static String country = "US";

    // constructor with all fields is generated

    // can add validation
    public Product {
        if (price < 0) {
            throw new IllegalArgumentException();
        }
    }

    // possible to override auto-generated methods like toString()
}
```

### Date time formatter API

```java
static Map<TextStyle, Locale> map=Map.of(
        TextStyle.FULL,Locale.ITALIAN,
        TextStyle.SHORT,Locale.US,
        TextStyle.NARROW,Locale.FRENCH
        );

public static void main(String[]args){

        for(var entry:map.entrySet()){

        LocalDateTime now=LocalDateTime.now();
        DateTimeFormatter formatter=new DateTimeFormatterBuilder()
        .appendPattern("yyyy-MM-dd hh:mm ")
        .appendDayPeriodText(entry.getKey())    // at night, du soir, abends, etc.
        .toFormatter(entry.getValue());

        String formattedDateTime=now.format(formatter);
        System.out.println(formattedDateTime);
        }
        }
```

### Stream API changes

* ``toList()`` collector
* ``Stream.mapMulti()`` combines `filter` and `flatMap` methods

[Java16StreamApi.java](java16%2Fsrc%2FJava16StreamApi.java)

## Java 17 features

### Sealed classes

A sealed class permits only predetermined inherited classes.

```java
sealed class Shape {
    permits Circle, Square, Triangle;
    // ... implementation details
}

class Circle extends Shape {
    // ...
}

// This will cause a compile-time error because Rectangle isn't permitted
class Rectangle extends Shape {
    // ...
}
```

* A sealed class uses “**permits**” to allow other classes to subclass it.
* A child class MUST either be final, sealed or non-sealed. (or code won’t compile)
* A permitted child class MUST extend the parent sealed class. Permitting without using the permit is now allowed.
* The classes specified by permits must be located near the superclass
    * either in the same module (if the superclass is in a named module) (see Java 9 modularity)
    * or in the same package

## Java 18 features

### UTF-8 default character set

```java
public class Utf8ByDefault {

    // https://openjdk.org/jeps/400 - Platform Default Encoding

    public static void main(String[] args) throws IOException {

        // Problem:
        // 1) On Windows, use the below FileWriter to write characters outside the ASCII table, e.g. some exotic Unicode chars, without explicitly specifying a char set
        // 2) Copy or transfer the file to a UNIX-based OS, like a Mac, and read the file using the default char encoding of the system
        // 3) Likely result - garbled output
        // Hence the problem - unpredictable behavior.
        FileWriter writer = new FileWriter("out.txt");

        // Solution before Java 18: always specify the charset, (and good luck not forgetting it!)
        FileWriter writer2 = new FileWriter("out.txt", StandardCharsets.UTF_8);

        // Solution since Java 18: UTF-8 is now default, so no need to specify the Char set

    }
}
```

### Simple Web Server

Used to serve static files (HTML, images...) in embedded applications

```java
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.SimpleFileServer;

import java.net.InetSocketAddress;

public class SimpleWebServer {
    public static void main(String[] args) throws Exception {
        String documentRoot = "/path/to/your/static/files";  // Replace with your actual directory
        int port = 8080;  // You can change the port if needed

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        SimpleFileServer fileServer = new SimpleFileServer(documentRoot);
        server.setExecutor(null);  // Use single-threaded executor
        server.createContext("/", fileServer);

        server.start();
        System.out.println("Server started on port " + port);
    }
}
```

The application can be normally compiled ed executed with ``javac`` and `java` commands

## Java 19 features

Either preview or incubator features

## Java 20 features

Either preview or incubator features

## Java 21 features

## Virtual threads

this feature introduces lightweight threads that run on top of the operating system threads,
aiming to simplify concurrent programming and improve performance for certain workloads.
In multithreading applications, a system thread spends a lot of time waiting for slow I/O operations.
The idea of the virtual thread is to assign the carrier thread a new virtual thread when the current one
is in I/O waiting state. This increases the complexity of thread management but increases performance, being
also transparent to the developer.

* **Lighter weight**: Compared to OS threads, virtual threads have significantly lower creation and context switching
  costs.
* **Improved concurrency**: More virtual threads can be efficiently managed within a limited number of OS threads,
  allowing better utilization of resources for certain workloads.
* **Simpler concurrency programming**: Virtual threads eliminate the need for complex thread management and
  synchronization, making concurrent programming easier for developers.

![virtual_thread.webp](img%2Fvirtual_thread.webp)

[VirtualThreadsFeatureExample.java](java21%2Fsrc%2FVirtualThreadsFeatureExample.java)

## Record patterns (Project Amber)

Records were introduced as a preview in Java 14 and as standard in Java 16.
Now patterns have been introduced for records as well

```java
// Prior to Java 16
if(obj instanceof String){
        String s=(String)obj;
        ...use s...
        }

// As of Java 16 with instance of patterns
        if(obj instanceof String s){
        ...use s...
        }

// As of Java 16 with records
record Point(int x, int y) {
}

    static void printSum(Object obj) {
        if (obj instanceof Point p) {
            int x = p.x();
            int y = p.y();
            System.out.println(x + y);
        }
    }

    // As of Java 21 we can extract the x and y components from the Point value directly, 
    // invoking the accessor methods on our behalf.
    static void printSum(Object obj) {
        if (obj instanceof Point(int x, int y)) {
            System.out.println(x + y);
        }
    }
```

We can also have nested records

```java
// As of Java 16
record Point(int x, int y) {
}

enum Color {RED, GREEN, BLUE}

record ColoredPoint(Point p, Color c) {
}

record Rectangle(ColoredPoint upperLeft, ColoredPoint lowerRight) {
}

    With nested
    patterns we
    can deconstruct
    such a
    rectangle with
    code that
    echoes the
    structure of
    the nested constructors:

// As of Java 21
static void printXCoordOfUpperLeftPointWithPatterns(Rectangle r){
        if(r instanceof Rectangle(ColoredPoint(Point(var x,var y),var c),
        var lr)){
        System.out.println("Upper-left corner: "+x);
        }
        }

static void printColorOfUpperLeftPoint(Rectangle r){
        if(r instanceof Rectangle(ColoredPoint(Point p,Color c),
        ColoredPoint lr)){
        System.out.println(c);
        }
        }
```

### Sequenced collections

![sequences_collections.png](img%2Fsequences_collections.png)

In JDK 21, a new set of collection interfaces are introduced.
The lack of **uniform methods for accessing first and last elements and iterating in reverse order** has been a
persistent limitation of Java’s collections framework.

|                 | Accessing the first element       | Accessing the last element  |
|-----------------|-----------------------------------|-----------------------------|
| `List`          | `list.get(0)`                     | `list.get(list.size() – 1)` |
| `Deque`         | `deque.getFirst()`                | `deque.getLast()`           |
| `SortedSet`     | `sortedSet.first()`               | `sortedSet.last()`          |
| `LinkedHashSet` | `linkedHashSet.iterator().next()` | missing                     |

This feature injects new interfaces into the existing hierarchy, offering a seamless mechanism to access the first and
last elements of a collection using built-in default methods.
Moreover, it provides support to obtain a reversed view of the collection.
for example, `SortedSet` implements one, but `HashSet` doesn't, making it cumbersome to achieve this on different data
sets.

To fix this, the `SequencedCollection` interface aids the encounter order by adding a `reverse` method as well as the
ability to get the `first` and the `last` elements.
Furthermore, there are also `SequencedMap` and `SequencedSet` interfaces.

```java
interface SequencedCollection<E> extends Collection<E> {
    // new method
    SequencedCollection<E> reversed();

    // methods promoted from Deque
    void addFirst(E);

    void addLast(E);

    E getFirst();

    E getLast();

    E removeFirst();

    E removeLast();
}
```

```java
interface SequencedSet<E> extends Set<E>, SequencedCollection<E> {

    // covariant override
    SequencedSet<E> reversed();
}
```

```java
interface SequencedMap<K, V> extends Map<K, V> {
    
    // new methods
    SequencedMap<K, V> reversed();
    SequencedSet<K> sequencedKeySet();
    SequencedCollection<V> sequencedValues();
    SequencedSet<Entry<K, V>> sequencedEntrySet();

    V putFirst(K, V);
    V putLast(K, V);

    // methods promoted from NavigableMap
    Entry<K, V> firstEntry();
    Entry<K, V> lastEntry();
    Entry<K, V> pollFirstEntry();
    Entry<K, V> pollLastEntry();
}
```


