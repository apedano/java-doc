# Dynamic features of Java

https://www.cesarsotovalero.net/blog/the-dynamic-features-of-java.html#challenges-of-using-dynamic-features

The existence of dynamic features built-in within the language allows Java developers to dynamically transform their program executions at runtime. For example, using the [Java Reflection API](https://docs.oracle.com/javase/tutorial/reflect/), one can inspect and interact with otherwise static language constructs such as classes, fields, and methods, e.g., to instantiate objects, set fields and invoke methods. These dynamic language features are helpful, but their usage also hinders the accuracy of static analysis tools. This is due to the undecidability of resolving and analyzing code that is not reachable at compile time. As I mentioned in a [previous blog post](https://www.cesarsotovalero.net/blog/aot-vs-jit-compilation-in-java.html), the promising **GraalVM compiler** performs Ahead of Time Compilation (AOT) through static analysis on Java bytecode. However, the presence of dynamic features in most Java programs is a fundamental challenge for GraalVM. Consequently, recognizing these features is key to understand the current limitations of AOT. This blog post covers the fundamental dynamic features of Java and the reasons why they pose a significant challenge for GraalVM and static analysis tools in general.

## Dynamic Class Loading

Java makes possible declaring  [custom class loaders](Class loaders.md). We can use a custom `ClassLoader` to compile a `.java` source file from a file system and then load the compiled `.class` at runtime. This mechanism allows programmers to load classes from arbitrary locations, e.g., from an external file system or over the network.

```java
public class CustomClassLoader extends ClassLoader {
  
  @Override
  protected Class<?> findClass(String name) throws ClassNotFoundException {
    byte[] content;
    try {
      content = compile(this.getClass().getClassLoader(), name);
    } catch (Exception e) {
      throw new ClassNotFoundException();
    }
    return defineClass(name, content, 0, content.length);
  }

  private byte[] compile(ClassLoader classLoader, String name) throws IOException {
    String path = name.replace(".", "/");
    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    compiler.run(null, null, null, classLoader.getResource(path + ".java").getFile());
    File file = new File(classLoader.getResource(path + ".class").getFile());
    FileInputStream input = new FileInputStream(file);
    byte[] content = new byte[(int) file.length()];
    input.read(content);
    input.close();
    return content;
  }
} 
```

This can support a class being loaded outside the classpath (a file in the host file system), therefore known only at **run-time**. Also the name of the class and the method to call can be determined at run-time as shown by:

```java
@Test
public void findClass() {
  CustomClassLoader ccl = new CustomClassLoader();
  try {
    Class<?> target = ccl.findClass("dynamicClassLoading.Target");
    int magic = (Integer) target.getMethod("magic").invoke(target.newInstance());
    Assert.assertEquals(42, magic);
  } catch (Exception e) {
    fail("ClassNotFoundException");
  }
}
```

## Dynamic Proxies


[Dynamic proxies](https://www.baeldung.com/java-dynamic-proxies) allow one single class with one single method to service multiple method calls to arbitrary classes with an arbitrary number of methods. It is **like a facade**, but one that **can pretend to be an implementation of any interface**. Under the cover, it routes all method invocations to a single handler: the `invoke()` method.

A [proxy class](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/reflect/Proxy.html) is a **class created at runtime that implements a specified list of interfaces, known as proxy interfaces**. A proxy instance is an instance of a proxy class. Each proxy instance has an associated **invocation handler**, which implements the interface [`InvocationHandler`](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/reflect/InvocationHandler.html). When a method is invoked on a proxy instance, **the method invocation is encoded and dispatched to the invoke method of its invocation handler** The invocation handler processes the encoded method invocation as appropriate and the result that it returns will be returned as **the result of the method invocation on the proxy instance**.

We have the follwoing interface/class we want to proxy.
```java
public interface MyInterface {
  String foo(String s);
}

public class MyInterfaceImpl implements MyInterface {
  @Override
  public String foo(String s) {
    return s + " from foo(String)";
  }
}
```
We create an `InvocationHandler` implementation

```java
public class MyProxyInvocationHandler implements InvocationHandler {
  @Override
  public Object invoke(Object proxy, Method method, Object[] arg) {
    return target((String) arg[0]);
  }
  public String target(String s) {
    return s + " from target(String)";
  }
} 
```
And an application that creates a proxy as an instance of `MyInterface` and that a run-time intercepts the call to the proxy

```java
public class DynamicProxyApplication {
  public String execute() {
    MyInterface proxy = (MyInterface) Proxy.newProxyInstance( //it is created at run-time
        MyInterface.class.getClassLoader(), //that is resolved at run time
        new Class[]{MyInterface.class},
        new MyProxyInvocationHandler()
    );
    return proxy.foo("hello");
  }
}
```
The test confirms that the result is from the proxy and not from the `MyInterfaceImpl` class.
```java
@Test
public void testExecute() {
  DynamicProxyApplication dpa = new DynamicProxyApplication();
  Assert.assertEquals("hello from target(String)", dpa.execute());
}
```
