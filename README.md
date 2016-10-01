# Stonehorse.Grit, simpler java

Grit is a collection of persistent collections. That is immutable collections where mutation are expressions. Inherited Java Collection methods that mutate will unconditionally throw UnsupportedOperationException, while these collections provide methods that return new altered instances of the collections.

Consider adding something to an java ArrayList
```java
list.add("something");
```
The list is updated in place and the state prior to mutation is lost. The corresponding PersistentVector throws up on add, but provides with:
```java
list=list.with("something");
```
The with method does not mutate the list. Instead list will have the new value due to assignment. If you want to remember the state prior to mutation, you simply assign some other variable, or final value if you prefer.
```java
final List another = list.with("something")
```
The list will remain the same as prior to mutation. This makes programming much less complex. Collections start to behave similar to Strings, but share structure internally to prevent excessive copying up on mutation

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


 <div align="right">
 /Stefan von Stein
</div> 
