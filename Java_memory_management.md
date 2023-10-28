# Java Memory management

Table of content

- [Java Memory management](#java-memory-management)
  - [JVM Memory Structure](#jvm-memory-structure)
  - [Young generation memory - MinorGC](#young-generation-memory---minorgc)
  - [Old generation memory (tenured) - MajorGC](#old-generation-memory-tenured---majorgc)
  - [Metaspace (PermGen)](#metaspace-permgen)
  - [Heap](#heap)
    - [Method Area](#method-area)
    - [Memory pools](#memory-pools)
      - [Run-Time Constant Pool](#run-time-constant-pool)
    - [Compiled code structure](#compiled-code-structure)
      - [The *ClassFile* Structure](#the-classfile-structure)
      - [The constant pool](#the-constant-pool)
      - [The bytecode representation](#the-bytecode-representation)
  - [Stack](#stack)
    - [Native method stack](#native-method-stack)
    - [Stack and Heap usage](#stack-and-heap-usage)
    - [The stack frame](#the-stack-frame)
      - [Local Variables array](#local-variables-array)
      - [Operand Stack](#operand-stack)
      - [Frame data](#frame-data)

Links

https://docs.oracle.com/javase/specs/jvms/se11/html/jvms-2.html

https://www.alibabacloud.com/blog/an-illustration-of-jvm-and-the-java-program-operation-principle_600307

https://deepsource.com/blog/jvm-method-invocation-control-flow //il totale

https://www.degruyter.com/document/doi/10.1515/comp-2020-0170/html (inside the JVM) //tools for jvm analysis


https://www.dropbox.com/scl/fi/cjweg82fz3tepp0xvm9ny/Java-Memory-management.docx?dl=0&rlkey=2o7qvhkifqa5u2mueda0qkvs7

https://www.baeldung.com/cs/memory-stack-vs-heap


https://www.geeksforgeeks.org/java-virtual-machine-jvm-stack-area/

https://amanagrawal9999.medium.com/the-ultimate-stack-frame-811c12f9ebf3 //example with stack frame

https://www.artima.com/insidejvm/ed2/jvm5.html
https://www.artima.com/insidejvm/ed2/jvm8.html //complete example with stack frame

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

The Java Virtual Machine has a method area that is shared among **all Java Virtual Machine threads**. It stores per-class structures such as the **run-time constant pool**, **field and method data, and the code for methods and constructors**, including the special methods used in class and interface initialization and in instance initialization These info are loaded by the classloader running in the JVM. 

Although the method area is logically part of the heap, simple implementations may choose not to either garbage collect or compact it. This specification does not mandate the location of the method area or the policies used to manage compiled code. The method area may be of a fixed size or may be expanded as required by the computation and may be contracted if a larger method area becomes unnecessary. The memory for the method area does not need to be contiguous.

Caused **OutOfMemoryError** exceptions is allocation request cannot be satisfied.

### Memory pools
Memory pool contains collections of **immutable objects**. **String pool** is an example. It belongs to Heap or PermGen depending on JVM memory management implementaton. At **compile-time**, every string literal in the code (“” or new (“”)) is stored in the String pool. 

#### Run-Time Constant Pool
The constant pool of a class is a sort of a key-value store containing entries for things like String constants, as well as references to all classes and methods that are referenced by the class. 
The run-time constant pool for a class or interface is constructed when the class or interface is **created within the Method area**.

### Compiled code structure

The code below can be made readeble out of a `.class` file generated from a compiled JVM based source code (Java, Scala, Kotlin etc..)


#### The *ClassFile* Structure
A compiled class structure is the following: 

```
ClassFile {
    u4             magic; //the magic number identifying the class file format; it has the value 0xCAFEBABE.
    u2             minor_version; //version of the class file
    u2             major_version; // M.m format 1.5 < 2.0 < 3.1
    u2             constant_pool_count; //the number of entries in the constant_pool table plus one. 
    cp_info        constant_pool[constant_pool_count-1]; //the constant pool structure, see below 
    u2             access_flags; // as specified here https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.1-200-E.1
    u2             this_class; //the reference to this class name in the cp
    u2             super_class; //the reference to the super class name in the cp
    u2             interfaces_count; //the number of direct super interfaces 
    u2             interfaces[interfaces_count]; //
    u2             fields_count;
    field_info     fields[fields_count];
    u2             methods_count;
    method_info    methods[methods_count];
    u2             attributes_count;
    attribute_info attributes[attributes_count];
}
```
Each `ux` value is of size `x` bytes, so that `u4` is four bytes long.

For details go to https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html


From this java code

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
By running the javap 
The output gives the 

```bash
  javac Memory.java
  javap -c -v Memory
```
Here we get the `ClassFile` for the Java code above

```bash
Classfile /C:/Users/Alessandro/Downloads/Memory.class
  Last modified 28 ott 2023; size 575 bytes
  SHA-256 checksum 08ba4f89e346e76810b441a9d95758a9882a0bd7e7b6f689d25969606f0e3488
  Compiled from "Memory.java"
public class Memory
  minor version: 0
  major version: 65
  flags: (0x0021) ACC_PUBLIC, ACC_SUPER
  this_class: #7                          // Memory
  super_class: #2                         // java/lang/Object
  interfaces: 0, fields: 0, methods: 3, attributes: 1

{
  
    
}
```



#### The constant pool

It is loaded by the class loader in the run time constant pool area.

Caused **OutOfMemoryError** exceptions is allocation request cannot be satisfied.

Here is the constant pool for the `Memory` class (loaded by the classloader in the RuntimeConstant pool area)

```bash
Constant pool:
   #1 = Methodref          #2.#3          // java/lang/Object."<init>":()V
   #2 = Class              #4             // java/lang/Object
   #3 = NameAndType        #5:#6          // "<init>":()V
   #4 = Utf8               java/lang/Object
   #5 = Utf8               <init>
   #6 = Utf8               ()V
   #7 = Class              #8             // Memory
   #8 = Utf8               Memory
   #9 = Methodref          #7.#3          // Memory."<init>":()V
  #10 = Methodref          #7.#11         // Memory.foo:(Ljava/lang/Object;)V
  #11 = NameAndType        #12:#13        // foo:(Ljava/lang/Object;)V
  #12 = Utf8               foo
  #13 = Utf8               (Ljava/lang/Object;)V
  #14 = Methodref          #2.#15         // java/lang/Object.toString:()Ljava/lang/String;
  #15 = NameAndType        #16:#17        // toString:()Ljava/lang/String;
  #16 = Utf8               toString
  #17 = Utf8               ()Ljava/lang/String;
  #18 = Fieldref           #19.#20        // java/lang/System.out:Ljava/io/PrintStream;
  #19 = Class              #21            // java/lang/System
  #20 = NameAndType        #22:#23        // out:Ljava/io/PrintStream;
  #21 = Utf8               java/lang/System
  #22 = Utf8               out
  #23 = Utf8               Ljava/io/PrintStream;
  #24 = Methodref          #25.#26        // java/io/PrintStream.println:(Ljava/lang/String;)V
  #25 = Class              #27            // java/io/PrintStream
  #26 = NameAndType        #28:#29        // println:(Ljava/lang/String;)V
  #27 = Utf8               java/io/PrintStream
  #28 = Utf8               println
  #29 = Utf8               (Ljava/lang/String;)V
  #30 = Utf8               Code
  #31 = Utf8               LineNumberTable
  #32 = Utf8               main
  #33 = Utf8               ([Ljava/lang/String;)V
  #34 = Utf8               SourceFile
  #35 = Utf8               Memory.java
```
The constant pool is a dictionary of indexed constant values in the form

```
cp_info {
    u1 tag;
    u1 info[];
}
```
 Those values are referred by other parts of the class file

```
this_class: #7    // Memory
```
or from the constant pool itself

```
#24 = Methodref          #25.#26 //reference to System.out.println(str);  in the format Class.NameAndType => java/io/PrintStream.println(Ljava/lang/String;)V
#25 = Class              #27 
#26 = NameAndType        #28:#29 //name and type
#27 = Utf8               java/io/PrintStream
#28 = Utf8               println
#29 = Utf8               (Ljava/lang/String;)V
```

#### The bytecode representation

Format

```
offset: instruction arg1, arg2
```
contains bytecode instructions to be executed in the stack frame 

```
public Memory();
    descriptor: ()V
    flags: (0x0001) ACC_PUBLIC
    Code:
      stack=1, locals=1, args_size=1
         0: aload_0
         1: invokespecial #1                  // Method java/lang/Object."<init>":()V
         4: return
      LineNumberTable:
        line 1: 0

  public static void main(java.lang.String[]);
    descriptor: ([Ljava/lang/String;)V
    flags: (0x0009) ACC_PUBLIC, ACC_STATIC
    Code:
      stack=2, locals=4, args_size=1
         0: iconst_1
         1: istore_1
         2: new           #2                  // class java/lang/Object
         5: dup
         6: invokespecial #1                  // Method java/lang/Object."<init>":()V
         9: astore_2
        10: new           #7                  // class Memory
        13: dup
        14: invokespecial #9                  // Method "<init>":()V
        17: astore_3
        18: aload_3
        19: aload_2
        20: invokevirtual #10                 // Method foo:(Ljava/lang/Object;)V
        23: return
      LineNumberTable:
        line 4: 0
        line 5: 2
        line 6: 10
        line 7: 18
        line 8: 23
```

The list of entries contains holes, e.g. from `2: new` to `5: dup` because the new instruction takes 



## Stack
Java stack memory is used for **every thread**’s execution. It stores primitive values, methods and object references stored in the heap (object useb by methods call references). Stack is implemented through a LIFO. Every time a method is invoked, a new stack frame is pushed. When the method ends, the block is unused 
and stack goes to the next block. Stack size is way lower compared to Heap. 

If the stack gets full a **StackOverflowException** is generated

### Native method stack

A native method is a method written in another programming language than Java. These methods aren't compiled to bytecode, hence the need for a different memory area. The Native Method Stack is very similar to the JVM Stack but is only dedicated to native methods.

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

Let’s check another code sample 

```java
class Person {  
  int id;  
  String name;  

  public Person(int id, String name) {  
    this.id = id;  
    this.name = name;  
  } 

}  

public class PersonBuilder {  
  private static Person buildPerson(int id, String name) {  

  return new Person(id, name);  
  }  

  public static void main(String[] args) {  
    int id = 23;  
    String name = "John";  
    Person person = null;  
    person = buildPerson(id, name);  
  }
} 
```


<img src="image/Java_memory_management/stack_heap_02.png" width=500>

### The stack frame
https://www.artima.com/insidejvm/ed2/jvm8.html

Every time a method is called in the bytecode, a stack frame is created and pushed to the stack.
The frame contains the follwing:

* **Local variables array**: zero based array, initialized with the reference to the heap for the executing class (`this` if it is an instance method) the method parameters and the internal variables. Each slot is 32 bit (a variable can use one or more slots)
* **Operand stack**: a stack of intermediate operand stored during method execution
* **Frame Data**: **Constant pool address**: the address to the constant pool of the class containing the invoked method

#### Local Variables array

Example:

```java
class Example3a {
    public static int runClassMethod(int i, long l, float f,
        double d, Object o, byte b) {
        return 0;
    }

    public int runInstanceMethod(char c, double d, short s,
        boolean b) {
        return 0;
    }
}
```

<img src="image/Java_memory_management/local_vars_01.gif" width=400>

The reported references are the reference to the classes stored in the heap.
The first parameter in the local variables for `runInstanceMethod()` is of type *reference*, even though no such parameter appears in the source code. This is the *hidden* `this` reference passed to every instance method. Instance methods **use this reference to access the instance data of the object upon which they were invoked**. As you can see by looking at the local variables for `runClassMethod()`, class methods **do not receive a hidden this**.

Many primitive types are stored in the JVM as *int* for `byte`, `short`, `char`, and `boolean`.

#### Operand Stack

This is not an array but a stack; the Java virtual machine uses the operand stack as a work space. Many instructions pop values from the operand stack, operate on them, and push the result.
Other than the program counter, which can't be directly accessed by instructions, the **Java virtual machine has no registers**. It is stack-based rather than register-based because its instructions take their operands from the operand stack rather than from registers.

Let's take this example that sums two ints putting the result in another local variable:

```
iload_0    // push the int (operand) in local variable 0
iload_1    // push the int (operand) in local variable 1 and pushes to the operand stack
iadd       // pop two ints, add them, push result. Pops the two int values from the operand stack, adds them, and pushes the int result back onto the operand stack
istore_2   // pop int, store into local variable 2
```
<img src="image/Java_memory_management/operand_stack_01.gif" width=400>

#### Frame data

...
https://www.artima.com/insidejvm/ed2/jvm8.html






