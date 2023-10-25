# APPENDIX_01_Java_compilers
## AOT vs. JIT Compilation in Java

## The Java compiler
**_Compiling_** a program means **transforming source code** from a high-level programming language, such as Java or Python, **into machine code**. Machine code are low-level instructions tailored to execute in a particular microprocessor.
Compilers perform several optimizations during the machine code generation phase:
* _value numbering_: code analysis to remove redundant code, by determining when two computations in a program are equivalent and eliminating one of them
* _class analysis_: 
* _constant inlining_: 
* _loop unrolling_
* _partial evaluation_

There are two ways of compiling a Java application: using **Just in Time Compilation** (**JIT**) or **Ahead of Time Compilation** (**AOT**). 
The first is the default mode, and it is used by the Java Hotspot Virtual Machine to translate bytecode into machine code at runtime. The latter is supported by the novel GraalVM compiler and allows statically compiling bytecode directly into machine code at build time.

Java has two main compilers

### The C1 - client compiler
The client compiler, also called C1, is a **JIT** compiler **optimized for faster start-up time**. 
It is a fast, lightly optimizing bytecode compiler that performs some value numbering, inlining, and class analysis. It uses a simple CFG-oriented SSA “high” IR, a machine-oriented “low” IR, a linear scan register allocation, and a template-style code generator.


freestar
Historically, we used C1 for short-lived applications and applications where start-up time was an important non-functional requirement.

https://www.cesarsotovalero.net/blog/aot-vs-jit-compilation-in-java.html
https://www.linkedin.com/pulse/ahead-of-time-compilation-vs-just-in-time-java-comparative-raj#:~:text=Startup%20Time%3A%20AOT%20compilation%20offers,code%20is%20up%20and%20running.