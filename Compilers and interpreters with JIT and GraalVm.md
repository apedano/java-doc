# Compilation in Java AOT, JIT and GraalVm

## Compiled and intepreted code

Usually, a compiled code is made of two processes, the **compilation** (the translation of the high level code into machine code) and the **run** (the execution of the machine code in the executing host). The **interpreted** codes are read and executed line by line (PHP, Ruby, Python, e JavaScript)

JAVA is a bit in the middle, because the source code is first **compiled** (using `javac`)  into a class file (see [Memory management doc](Memory Management.md) the for the structure of the class file).

```bash
$ javac HelloWorld.java
# output: HelloWorld.class
```

Since the JVM is platform-neutral, it uses an interpreter to execute bytecode

```bash
$ java HelloWorld
Hello Java!
```

![Alt text](image/compilation/java_compiler_01.png)

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

## The JIT (*Just-In-Time*) compiler

Just-in-time (JIT) compilation is central to peak performance in modern virtual machines, but it comes with trade-offs.

The JDK implementation by **Oracle** is based on the open-source **OpenJDK** project. This includes the ***HotSpot virtual machine***, available since Java version 1.3.

A JIT compiler relies on the same well-known compilation techniques that an offline compiler such as the GNU Compiler Collection (GCC) uses. The primary difference is that **a just-in-time compiler runs in the same process as the application** and competes with the application for resources, because it is part of the **execution engine**. Mainly, compilation time is more of an issue for a JIT compiler than for an offline compiler, but new possibilities for optimization — such as *deoptimization* and *speculation* — open up, as well.

The JIT compiler's execution is transparent to the end user. It can, however, be observed by running the java command with diagnostic options

## The execution flow
<table>
<rd>
    <td><img src="image/compilation/bytecode_flow.png" width="200"></td>
    <td><img src="image/compilation/jit_compilation_01.png" width="600"></td>
</rd>
</table>


In the diagram above we have the `.class` files loaded when they are requested by the running application. The class loader is also responsible of **loading, linking and initializing** the class. **Verification** is also done (correctness, consistency of class links etc...), so no further verification is needed at run-time (see the [Class loader doc](<Class loaders.md>)).



https://www.cesarsotovalero.net/blog/how-the-jvm-executes-java-code.html


The loaded class is stored in the method area and the current executing code in the stack frame

A Java-based JIT compiler takes `.class` files as input rather than Java code, which is consumed by `javac` it then generates the bytecode at runtime to be executed by the **interpreter**.

It contains two conventional **JIT-compilers**: the client compiler, also called C1 and the server compiler, called opto or C2.
**C1** is designed to run faster and produce less optimized code (better fit for desktop applications since we don't want to have long pauses for the JIT-compilation), while **C2**, on the other hand, is more time consuming but produces a better-optimized code (better suited for ong-running server applications that can spend more time on the compilation).

Both C1 and C2 perform a **profiler** at execution-time, which identifies the

# PROFILER TODO

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
