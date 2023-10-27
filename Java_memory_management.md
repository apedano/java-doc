# Java Memory management

Table of content

- [Java Memory management](#java-memory-management)
  - [JVM Memory Structure](#jvm-memory-structure)
  - [Young generation memory - MinorGC](#young-generation-memory---minorgc)
  - [Old generation memory (tenured) - MajorGC](#old-generation-memory-tenured---majorgc)
  - [Metaspace (PermGen)](#metaspace-permgen)
  - [Heap](#heap)
    - [Method Area](#method-area)
    - [Run-Time Constant Pool](#run-time-constant-pool)
    - [Native method stack](#native-method-stack)
    - [Memory pools](#memory-pools)
  - [Stack](#stack)
    - [Stack and Heap usage](#stack-and-heap-usage)

Links

https://docs.oracle.com/javase/specs/jvms/se11/html/jvms-2.html

https://www.alibabacloud.com/blog/an-illustration-of-jvm-and-the-java-program-operation-principle_600307

https://deepsource.com/blog/jvm-method-invocation-control-flow

https://www.degruyter.com/document/doi/10.1515/comp-2020-0170/html (inside the JVM)

https://www.scaler.com/topics/memory-management-in-java/

https://www.dropbox.com/scl/fi/cjweg82fz3tepp0xvm9ny/Java-Memory-management.docx?dl=0&rlkey=2o7qvhkifqa5u2mueda0qkvs7

https://www.baeldung.com/cs/memory-stack-vs-heap


https://www.geeksforgeeks.org/java-virtual-machine-jvm-stack-area/
https://amanagrawal9999.medium.com/the-ultimate-stack-frame-811c12f9ebf3

Java Memory Management is divided into two major part :


* **JVM Memory Structure**
* **Working of the Garbage Collector**

## JVM Memory Structure
Memory structure is mainly made of the "**Young generation**" and "**Old generation**" depending on the scope of the garbage collector

![Java Memory](image/Java_memory_management/java_mem.drawio.png)

![1698403157572](image/Java_memory_management/1698403157572.png)


## Young generation memory - MinorGC

**Eden space** is where all just created objects are stored. *Every thread has a dedicated segment of Eden space* (**ThreadLocal Allocation Buffer**) if full, Eden has a common area where move these objects. If it becomes full a **Minor GC** (minor garbage collector) is performed and ***all the survivor objects** are moved to one of the survivor spaces*. This means that the Eden Space is emptied after GC execution.

**Survivior space** is splitted into two sections *S0* and *S1*. *Only one of this area contains objects* ('from' role), the other one is always emtpy ('to' role).
**Minor GC** analyses survivor objects too and all survived object in the 'from' area are passed to the 'to' survivor section and the 'from' is emptied. Their roles are switched for the next gc exection (*Mark and copy* approach). 

Object not deleted after a certain number of gc cycles (configurable trasholded age) are moved to old generation memory. Here there is no Mark and Copy but an approach of Mark, Sweep and Compact (reduce memory fragmentation) 

## Old generation memory (tenured) - MajorGC

Old generation memory contains long lived object survived to many round of Minor GC. When it’s full a more time consuming **Major GC** is launched.  

## Metaspace (PermGen)

Metaspace, formerly known as PermGen (Permanent Generation), is a non-heap memory area that stores **class metadata**, **constant pool information**, and **method bytecode**. It was introduced in Java 8 as a replacement for PermGen, which was removed due to memory management issues.

Unlike the heap and stack, metaspace **does not have a fixed size** and can grow dynamically. However, it is still essential to monitor its usage to avoid memory leaks and potential **OutOfMemoryError** exceptions.

## Heap

The Java Virtual Machine has a heap that is **shared among all Java Virtual Machine threads**. The heap is the run-time data area from which memory for all class instances and arrays is allocated.

If a computation requires more heap than can be made available by the automatic storage management system, the Java Virtual Machine throws an **OutOfMemoryError**.

### Method Area

The Java Virtual Machine has a method area that is shared among all Java Virtual Machine threads. It stores per-class structures such as the **run-time constant pool**, **field and method data, and the code for methods and constructors**, including the special methods used in class and interface initialization and in instance initialization (§2.9).

Although the method area is logically part of the heap, simple implementations may choose not to either garbage collect or compact it. This specification does not mandate the location of the method area or the policies used to manage compiled code. The method area may be of a fixed size or may be expanded as required by the computation and may be contracted if a larger method area becomes unnecessary. The memory for the method area does not need to be contiguous.

Caused **OutOfMemoryError** exceptions is allocation request cannot be sadisfied.

### Run-Time Constant Pool
The constant pool of a class is a sort of a key-value store containing entries for things like String constants, as well as references to all classes and methods that are referenced by the class. 
The run-time constant pool for a class or interface is constructed when the class or interface is created within the Method area.

Caused **OutOfMemoryError** exceptions is allocation request cannot be sadisfied.

### Native method stack

A native method is a method written in another programming language than Java. These methods aren't compiled to bytecode, hence the need for a different memory area. The Native Method Stack is very similar to the JVM Stack but is only dedicated to native methods.

### Memory pools
Memory pool contains collections of **immutable objects**. **String pool** is an example. It belongs to Heap or PermGen depending on JVM memory management implementaton. At **compile-time**, every string literal in the code (“” or new (“”)) is stored in the String pool. 


## Stack
Java stack memory is used for **every thread**’s execution. It stores primitive values, methods and object references stored in the heap (object useb by methods call references). Stack is implemented through a LIFO. Every time a method is invoked, a new stack frame is pushed. When the method ends, the block is unused 
and stack goes to the next block. Stack size is way lower compared to Heap. 

If the stack gets full a **StackOverflowException** is generated


### Stack and Heap usage 

```java
public class Memory { 

  public static void main(String[] args) { // Line 1 

    int i=1; // Line 2 

    Object obj = new Object(); // Line 3 

    Memory mem = new Memory(); // Line 4 

    mem.foo(obj); // Line 5 

  } // Line 9 

  private void foo(Object param) { // Line 6 

    String str = param.toString(); //// Line 7 

    System.out.println(str); 

  } // Line 8 

} 
```

Below image shows the Stack and Heap memory with reference to above program and how they are being used to store primitive, Objects and reference variables. 

![Heap Stack](image/Java_memory_management/Stack_heap.png)

Let’s see the steps of this program’s memory usage 

When the program starts, the first call encountered is the main method at **LINE 1** (called by runtime environment).  A stack for the main thread is created in memory and the `main()` method is pushed in stack 

At **LINE 2** we have a primitive variable and its value is pushed directly to the stack 

At **LINE 3** and **LINE 4** the program created new class objects. Objects are created in heap memory and references to them are pushed into the stack 

At **LINE 5** the `foo` method is call, so (as we seen before for main method) a block is created on top of the stack for the `foo` method. Since Java passes params by value, a new copy of reference to Object class will created in the `foo()` stack block 

In **LINE 7** we have a new String trough the Object’s toString() method . It will be interned into the String pool (the value itself) and in the foo() stack block will be added a reference to this string in the pool. 

At **LINE 8** `foo()` ends and the relative block in the stack becomes free 

At **LINE 9** `main()` method terminates and all the stack allocated for thread is destroyed. Since the entire program terminates, Java Runtime ends the execution releasing al the allocated memory (stack, heap...) 

