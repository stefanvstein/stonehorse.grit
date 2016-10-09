
# Stonehorse.Grit, persistent data structures for Java

Grit is a collection of persistent collections. These are immutable containers with mutation as expression. 

These collections provide mutation by returning modified collections rather than mutating them in place. Collection always remain untouched. Mutating methods inherited from Java Collection API will unconditionally throw UnsupportedOperationException. These implement the mandatory requirements of Java Collections as mutating methods are optional and can safely be used where read only collections are acceptable. 

As an example: consider adding something to an ordinary java ArrayList
```java
list.add("something");
```
The ArrayList is updated in place and the state prior to mutation is lost for all referring to it. 

The corresponding PersistentVector throws up on add, but instead provides the `with`-method that returns the altered list:
```java
list = list.with("something");
```
The `with` does not mutate the list in place and all referencing it still sees the list as prior to mutation. Instead the reference list will refer a new value, list with "something" due to the assignment, as `with` returned a new value. 

If you want to remember the state prior to mutation, you simply assign the new list to some other reference, or final value if you prefer.
```java
another = list.with("something")
```
The list reference will remain the same as prior to mutation. Anyone referring to the list and wants the mutation will have to go and fetch the new reference, when ever they are prepared to accept changes.

This technique reduces code complexity, and is commonly used in functional programming languages. Collections start to behave similar to e.g. Strings and becomes easier to reason about. You can safely make assumptions about stable state and safely pass your collections to less trusted libraries as these won't be able to alter them at unexpected moments, via secretly saved references. State won't change until you are prepared. It is a panacea for writing your immutable composites.


Currently `PersistentVector`, `PersistentMap` and `PersistentSet` are provided. [JavaDoc](https://stefanvstein.github.io/stonehorse.grit/index.html)

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

This is not dependent on anything else than its sister project [Stonehorse.Candy](https://github.com/stefanvstein/stonehorse.candy), Java 8, and JUnit

## Usage

The factory classes [Vectors](https://stefanvstein.github.io/stonehorse.grit/stonehorse/grit/Vectors.html), [Maps](https://stefanvstein.github.io/stonehorse.grit/stonehorse/grit/Maps.html), [Sets](https://stefanvstein.github.io/stonehorse.grit/stonehorse/grit/Sets.html) are your starting points, while [PersistentVector](https://stefanvstein.github.io/stonehorse.grit/stonehorse/grit/PersistentVector.html), [PersistentMap](https://stefanvstein.github.io/stonehorse.grit/stonehorse/grit/PersistentMap.html), [PersistentSet](https://stefanvstein.github.io/stonehorse.grit/stonehorse/grit/PersistentSet.html) are the abstract interfaces representing the structures. These extend Java Collections interfaces List, Map, and Set, but act as read only versions of them. Similar to wrapping them in Collections.unmodifiableCollection from Java Collection perspective.

### With or without

The `with` and `without` are the most basic methods used to mutate. As explained above these methods returns new versions of the collections rather than changing them. These basic methods add and remove elements on the most effective position, which is dependent on collection type. For a vector the most effective position is at the end. While for a set it is unknown.

```java
PersistentList<Integer> list = Vector.vector().with(1).with(2);
```
..will give you a vector of `[1, 2]`

The expression 
```java
list.without()
```
...would return a vector `[1]` as the second element, the most effective, would be removed. Vectors naturally mutate at the end.

The `without` method for Set, is applied with the elements that should be removed. 

There are `withAll` methods for adding many elements at once, i.e. from Iterables or other Maps. 


### Strict higher order

There are higher order mutations. That is, mutating methods that take function as argument. Map has `whenMissing(K key, Supplier<V> valueSupplier)` where the value is calculated only when the key was missing, etc.

Then there are the classic higher order transformations: `map`, `filter`, `fold` and `reduce`, which here are strict and return a collection of the same type.

### null are values

Grit recognizes null as legal value. As null is of subtype of every type that will ever be, null should always be legal value of the type currently stored in collection. This is somewhat different than Java Collection that don't always accept null.

### Java 8 and Compatibility

There are other Persistent Collections for Java out there. This one is trying to be very compatible with Java Collections, in order to be easy to use. This is also devised with Java 8 in mind, and have higher order methods fit for lambdas, and integration with Streams.

There are exceptions in compatibility, as these always accept null.

### Performance characteristics 

Mutation of persistent collection does not necessarily mean copying whole collections. Small collections are cheap to copy. Larger collections share structure internally with its descendants to prevent excessive copying up on mutation. All of these collections are trees of some type. The only thing copied during a mutation is the branch from root to altered leaf, while everything else is shared. The Vector is a very shallow and highly branching tree which has O log32(n), which is very similar characteristics to O(1).

### on the shoulder of giants

These algorithms are similar to those used in functional languages like Clojure and Scala. This project started out of curiosity of how the Persistent Vector in Clojure was implemented, and ended with a total rewrite, both to get to know all the nuts and bolts, as well as to achieve higher Java Collection compatibility. So don't blame Clojure for issues found here as this is a total rewrite. This is implemented with simple code that should be easy to follow rather than focusing on being performant. If you like working with this, I highly encourage you to go for Clojure, as it will leverage these ideas much further. On the other hand, sometimes you don't get to choose.

 <div align="right">
_Choose immutability and see where it takes you_

 /Stefan von Stein
</div> 

