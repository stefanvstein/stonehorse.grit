
# Stonehorse.Grit, persistent data structures for Java

Grit is a collection of persistent collections. These are immutable containers where mutation are expressions. 

These collections provide mutation by returning modified collections rather than mutating them in place, while the source always remain untouched. Mutating methods inherited from Java Collection API will unconditionally throw UnsupportedOperationException. These implement the mandatory requirements of Java Collections, as mutating methods are optional. 

Consider adding something to an java ArrayList
```java
list.add("something");
```
The list is updated in place and the state prior to mutation is lost. The corresponding PersistentVector throws up on add, but provides the with method:
```java
list=list.with("something");
```
The with method does not mutate the list. Instead list will have the new value, "something" due to the assignment. you want to remember the state prior to mutation, you simply assign some other variable, or final value if you prefer.
```java
another = list.with("something")
```
The list will remain the same as prior to mutation. This makes programming much less complex. Collections start to behave similar to e.g. Strings and things start to be less magic and easier to reason about.




Currently Vector, Map and Set are provided. [JavaDoc](https://stefanvstein.github.io/stonehorse.grit/index.html)

It is very early, and interface may still change

## Installation

Currently running github as maven repo. Add the following to your pom

Add a the repository
```xml
<repositories>
    <repository>
        <id>stefanvstein-snapshots</id>
        <url>https://github.com/stefanvstein/mvn/raw/master/snapshots</url>
        <snapshots>
           <enabled>true</enabled>
           <updatePolicy>daily</updatePolicy>
        </snapshots>
    </repository>
</repositories>
```

and dependency:
```xml
<dependencies>
  <dependency>
    <groupId>stonehorse</groupId>
    <artifactId>grit</artifactId>
    <version>0.1-SNAPSHOT</version>
  </dependency>
</dependencies>
```
to your pom

## Usage

PersistentVector, PersistentMap, PersistentSet are the abstract interfaces that you use. The factory classes Vectors, Maps, Sets are your starting point.

### With or without


### Strict higher order

### Performance characteristics 
Small collections are cheap to copy, while larger share structure internally to prevent excessive copying up on mutation.

### null are values

### Java 8 and Compatibility

### on the shoulder of gigants

 <div align="right">
_Choose immutability and see where it takes you_

 /Stefan von Stein
</div> 

