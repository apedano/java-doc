# Compilation in Java AOT, JIT and GraalVm

## Compiled and intepreted code

Usually, a compiled code is made of two processes, the **compilation** (the translation of the high level code into machine code) and the **run** (the execution of the machine code in the executing host). The **interpreted** codes are read and executed line by line (PHP, Ruby, Python, e JavaScript)

JAVA is a bit in the middle, because the source code is first **compiled** (using `javac`)  into a class file (see [Memory management doc](Memory Management.md) the for the structure of the class file).

```bash
$ javac HelloWorld.java
# output: HelloWorld.class
```

Since the JVM is platform-neutral, it uses an **interpreter** to execute bytecode

```bash
$ java HelloWorld
Hello Java!
```

![Alt text](image/compilation/java_compiler_01.png)

<img src="image/compilation/compilation_01.png" width="500">

## The *JVM* structure

<img src="image/compilation/jvm_architecture.png" width="500">

### ClassLoader

See [Class loader doc](Class%20loaders.md)

### ClassLoader

See [Memory management doc](Memory Management.md)

Besides loading, the ClassLoader also performs linking and initialization. That includes:

* Verifying the bytecode for any security breaches
* Allocating memory for static variables
* Replacing symbolic memory references with the original references
* Assigning original values to static variables
* Executing all static code blocks

### Execution engine

The execution engine makes use of the [Native method interface (JNI)](https://www.baeldung.com/jni) to call native libraries and applications.

#### The JIT compiler

Improves performance by compiling bytecode to native code for repeated method calls. More details later

#### Garbage collector

See [Memory management doc](Memory Management.md)

#### Interpreter

<img src="image/compilation/java_interpreter_01.png" width="600">

Generally speaking, the interpreter scans the class file being executed (in the current stack frame - see [Memory management doc](Memory Management.md)), maintaining a **conversion table** between the class file instructions (platform independent) and the corresponding machine istructions (platform dependent).
The machine code is then transferred to the host cores for execution.



## The execution flow
<table>
<rd>
    <td><img src="image/compilation/bytecode_flow.png" width="200"></td>
</rd>
</table>

### Class loader :loading - linking - (static) initializing

In the diagram above we have the `.class` files loaded when they are requested by the running application. The class loader is also responsible of **loading, linking and initializing** the class. **Verification** is also done (correctness, consistency of class links etc...), so no further verification is needed at run-time (see the [Class loader doc](<Class loaders.md>)).

### Instantiating 

A new class instance is explicitly created when evaluation of a class instance creation expression is performed (`new` operator)

An **implicit instantiation** happens in the following conditions:

* Loading a class or interface that contains a **string literal** or a ** text block** may create a `new String` object
* Execution of an operation that causes **boxing conversion** may create a new object of a wrapper class. (Passing a primitive to method as a wrapper class)
* Execution of a **string concatenation** operation may create a `new String` object
* Evaluation of a **method reference expression** or a **lambda expression** may create a **new object of a functional interface**.

Here’s an example of creating a new instance of the class Point:

```java
Point magicPoint = new Point(42, 42);
```
#### Instantiation steps

* Memory is **allocated on the heap** to hold the new object
* The **class’s constructor is called** to initialize the new object
* The **reference to the new object is returned**

### Finalizing (instance)

Finalization is the process of **cleaning up the resources** held by an object (network sockets, IO streams...) and **preparing it for garbage collection**. The class `Object` defines a method finalize that **is called by the garbage collector when an object is about to be reclaimed**.

The JVM defines a finalize method in the Object class, which can be **overridden by subclasses to perform class specific cleanup actions** before the object is garbage collected. 

```java
public class TempFile {
  private File file;
  public TempFile(String filename) {
    file = new File(filename);
  }
  @Override
  protected void finalize() throws Throwable {
    // Delete the file when the TempFile object is garbage collected
    file.delete();
    super.finalize();
  }
}
```
### Unloading (class)

Unloading refers to the process of **removing a class or interface from the runtime state of the JVM** (e.g., its defining **class loader may be reclaimed by the garbage collector**).
The classloader can be unloaded together with all classes defined by it if the following conditions are met

* Classloader is unreachable
* there are no method stack frames of the methods of classes defined by the classloader
* there are no instances of the classes defined by the classloader

The most difficult problem to solve efficiently is to determine if instances of the classes exist.

### Program exit

Program exit refers to the process of **terminating the execution of a program**. This means that **all threads** that are not daemon threads **are terminated**, or some thread invokes the **exit method of the `Runtime` class**. This method halts the JVM and exit with a specified exit code. However, the use of this method is restricted by a security manager. If a security manager is present and it does not allow the program to exit, the exit method will throw a `SecurityException`.

## The JIT (*Just-In-Time*) compiler

The JDK implementation by **Oracle** is based on the open-source **OpenJDK** project. This includes the ***HotSpot virtual machine***, available since Java version 1.3.

A JIT compiler relies on the same well-known compilation techniques that an offline compiler such as the GNU Compiler Collection (GCC) uses. The primary difference is that **a just-in-time compiler runs in the same process as the application** and competes with the application for resources, because it is part of the **execution engine**. Mainly, compilation time is more of an issue for a JIT compiler than for an offline compiler, but new possibilities for optimization — such as *deoptimization* and *speculation* — open up, as well.

The JIT compiler's execution is transparent to the end user. It can, however, be observed by running the java command with diagnostic options.

Just-in-time (JIT) compilation is central to peak performance in modern virtual machines, but it comes with trade-offs. When one method is called **multiple times**, every time interpretation is required.

### HotSpot's JIT execution model

<img src="image/compilation/jit_compilation_01.png" width="600">

In practice, the HotSpot JVM's execution model is the result of four observations taken together:

Most code is only executed uncommonly, so getting it compiled would waste resources that the JIT compiler needs. **Only a subset of methods is run frequently**. The interpreter is ready right away to execute any code.
**Compiled code is much faster but producing it is resource hungry**, and it is only available after the compilation process is over which takes time.
The resulting execution model could be summarized as follows:

1. **Code starts executing interpreted with no delay**. The interpreter reads the bytecode from the Method Cache (Method area) and sends it to the JIT compiler. **it maintains a per-method count of the number of times a method is entered**. Because hot methods usually have loops, it also collects the number of times a branch back to the start of a loop is taken. On method entry, the interpreter adds the two numbers and if the result crosses a threshold, it enqueues the method for compilation.  
2. A compiler **thread running concurrently** with threads executing Java code then processes the compilation request. While compilation is in progress, **interpreted execution continues**, including for methods in the process of being JIT'ed. Once the compiled code is available it is saved in the **Code Cache**, **the interpreter branches off to it**.

<img src="image/compilation/hot_path_01.png" width="600">

As depicted in the image above, the code can also be **deoptimized** (invalidated in the code cache), for instance because the profiling has labelled the compiled code not valid anymore.

So, the trade-off is roughly between the *fast-to-start-but-slow-to-execute* **interpreter** and the *slow-to-start-but-fast-to-execute* **compiled code**. How slow-to-start that compiled code is, is under the virtual machine designer's control to some extent: The **compiler can be designed to optimize less** (in which case code is available sooner but doesn't perform as well) **or more** (leading to faster code at a later time). A practical design that leverages this observation is to have a multi-tier system.


### Multi-tiered execution

HotSpot has a **three-tiered** system consisting of:
* **Tier 1** - interpreter
* **Tier 2** - C1 - quick compiler 
* **Tier 3** - C2 - optimizing compiler 
 
Each tier represents a different trade-off between the delay of execution and the speed of execution. 

<img src="image/compilation/tiered_compiler_01.png" width="600">

Java code starts execution in the interpreter. As the method goes through the different tiers, each tier gathers information about the method execution; this information is called **Profiling Data** (PD). Then, when a method becomes **warm**, it's enqueued for compilation by the quick compiler. Execution switches to that compiled code when it's ready. If a method executing in the second tier becomes **hot**, then it's enqueued for compilation by the optimizing compiler. Execution continues in the second-tier compiled code until the faster code is available. Code compiled at the second tier has to identify when a method becomes hot, so it also has to increment invocation and back-branch counters.

<img src="image/compilation/tiered_compiler_02.png" width="600">

### Compilation levels

Even though the JVM works with only one *interpreter* and two *JIT compilers*, there are five possible levels of compilation. The reason behind this is that the C1 compiler can operate on three different levels. The difference between those three levels is in the amount of profiling done. 

<table>
<tr>
    <td><b>Level<b></td>
    <td><b>Description<b></td>
</tr>
<tr>
    <td>none (<b>0</b>)</td>
    <td>Interpreter gathering full PD</td>
</tr>
<tr>
    <td>simple (<b>1</b>)</td>
    <td>C1 compiler with no profiling </td>
</tr>
<tr>
    <td>limited profile (<b>2</b>)</td>
    <td>C1 compiler with light profiling gathering some PD </td>
</tr>
<tr>
    <td>full profile (<b>3</b>)</td>
    <td>C1 compiler with full profiling gathering full PD </td>
</tr>
<tr>
    <td>full optimization (<b>4</b>)</td>
    <td>C2 compiler with no profiling </td>
</tr>
</table>

#### Compilation levels configuration

* *–XX:-TieredCompilation* flag. The compilation will not switch levels. As a result, we’ll need to select which JIT compiler to use: C1 or C2. Thus CPU consumption will go down. However as side-effect your application’s performance can degrade

* *-XX:TieredStopAtLevel=N* If CPU spike is caused because of c2 compiler threads alone, you can turn-off c2 compilation alone. You can pass ‘-XX:TieredStopAtLevel=3’. When you pass this ‘-XX:TieredStopAtLevel’ argument with value 3, then only c1 compilation will be enabled and c2 compilation will be disabled.





AOT vs. JIT Compilation in Java

here are two ways of compiling a Java application: using **Just in Time Compilation** (**JIT**) or **Ahead of Time Compilation** (**AOT**).
The first is the default mode, and it is used by the Java Hotspot Virtual Machine to translate bytecode into machine code at runtime. The latter is supported by the novel GraalVM compiler and allows statically compiling bytecode directly into machine code at build time.

https://www.geeksforgeeks.org/jvm-works-jvm-architecture/

https://www.tutorialspoint.com/java_virtual_machine/java_virtual_machine_jit_compiler.htm

https://foojay.io/today/what-does-a-modern-jvm-look-like-and-how-does-it-work/

https://developers.redhat.com/articles/2021/06/23/how-jit-compiler-boosts-java-performance-openjdk#

https://developers.redhat.com/articles/2021/11/18/runtime-profiling-openjdks-hotspot-jvm#peeking_at_profile_data

https://www.cesarsotovalero.net/blog/aot-vs-jit-compilation-in-java.html
https://www.linkedin.com/pulse/ahead-of-time-compilation-vs-just-in-time-java-comparative-raj#:~:text=Startup%20Time%3A%20AOT%20compilation%20offers,code%20is%20up%20and%20running.

https://www.dropbox.com/scl/fi/kumbqichotl1v1zavb2m2/Java-compiler-and-interpreters-with-JIT.docx?dl=0&rlkey=61c7oqrza5arolrfa4fy36wkp

https://www.graalvm.org/latest/reference-manual/java/compiler/
