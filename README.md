
# Stonehorse.Grit, persistent data structures for Java

Grit is a set of immutable containers with mutation as expression. That is, read only versions of Vector, Map and Set, where mutation return new versions rather than updating the collection in place. These new versions share structure with previous, by utilizing path copying, rather than copying the whole collection during mutation. These collections always remain untouched, which simplifies programming a lot. A panacea for immutable composites. These are the same algorithms as found in many functional languages.

Mutating methods inherited from Java Collection API will unconditionally throw UnsupportedOperationException. These implement the mandatory requirements of Java Collections as mutating methods are optional. These can safely be used where read only collections are acceptable.

```java
PersistentVector<String> vector = Vectors.vector();
final PersistentVector<?> empty = vector;
vector = vector.with("something");
PersistentVector<String> another = vector.with("else");
```

In above code everythings remains untouched. The empty reference will always be empty, vector will always have something, while another always will have something else. The reference named vector did however not stay consistent throughout the snippet, as it referred two immutables. This would not be possible if it was final. There are no variables here, only immutable values. Empty will always refer to an empty vector, no matter what.

Immutables reduce complexity. Collections start to behave similar to e.g. Strings, and becomes easier to reason about. You can safely make assumptions about stable state and safely pass your collections to any, even less trusted libraries, as these can't be altered at unexpected moments. State won't change until you are prepared. It is the panacea for writing immutable composites.


Currently `PersistentVector`, `PersistentMap` and `PersistentSet` are provided. [Doc](https://stefanvstein.github.io/stonehorse.grit/index.html)

A talk [Simpler Java](https://stefanvstein.github.io/stonehorse.grit/SimplerJava.pdf) made at Javaforum

## Installation

Grit can be found in the central maven repo, just add the follwing to your pom.xml, or similar depending on your build, and maven should download it for you.

```xml
<dependencies>
  <dependency>
    <groupId>com.github.stefanvstein</groupId>
    <artifactId>grit</artifactId>
    <version>0.1</version>
  </dependency>
</dependencies>
```

This is not dependent on anything else than its sister project [Stonehorse.Candy](https://github.com/stefanvstein/stonehorse.candy), Java 8, and JUnit (for test)

## Usage

The factory classes [Vectors](https://stefanvstein.github.io/stonehorse.grit/stonehorse/grit/Vectors.html), [Maps](https://stefanvstein.github.io/stonehorse.grit/stonehorse/grit/Maps.html), [Sets](https://stefanvstein.github.io/stonehorse.grit/stonehorse/grit/Sets.html) are your starting points, while [PersistentVector](https://stefanvstein.github.io/stonehorse.grit/stonehorse/grit/PersistentVector.html), [PersistentMap](https://stefanvstein.github.io/stonehorse.grit/stonehorse/grit/PersistentMap.html), [PersistentSet](https://stefanvstein.github.io/stonehorse.grit/stonehorse/grit/PersistentSet.html) are the abstract interfaces representing the structures. These extend Java Collections interfaces List, Map, and Set, but act as read only versions of these. Similar to wrapping them in Collections.unmodifiableCollection from a Java Collection perspective.

### With or without

The `with` and `without` are the most basic methods used to mutate. These methods returns new versions of the collections rather than changing them. These basic methods add and remove elements on the most effective position, which is dependent on collection type.

```java
PersistentVector<Integer> list = Vectors.vector().with(1).with(2);
```
..will give you a vector of `[1, 2]`

The following expression
```java
list.without()
```
...would return a vector `[1]` as the second element, the most effective, would be removed. Vectors naturally mutate at the end.

The `without` method for Set, is applied with the elements that should be removed.

There are `withAll` methods for adding many elements at once, i.e. from Iterables or other Maps.


### Strict higher order

There are higher order mutations. That is, mutating methods that take function as argument. Map has `whenMissing(K key, Supplier<V> valueSupplier)` where the value is calculated only when the key was missing, etc.

Then there are the classic higher order transformations: `map`, `filter`, `fold` and `reduce`. These are strict and return a collection of the same type.

### null are values

Grit recognizes null as legal value. As null is of subtype of every type that will ever be, null should always be legal value of the type currently stored in collection. This is somewhat different than Java Collection that don't always accept null.

### Java 8 and Compatibility

There are other Persistent Collections for Java out there. This one is trying to be very compatible with Java Collections, in order to be easy to use. This is also devised with Java 8 in mind, and have higher order methods fit for lambdas, and integration with Streams.

There are exceptions in compatibility with similar JDK collections, as these collections always accept null as valid value, while some JDK ones don't.

### Performance characteristics 

Mutation of persistent collection does not necessarily mean copying whole collections. Small collections are cheap to copy. Larger collections share structure internally with its descendants to prevent excessive copying up on mutation. All of these collections are trees of some type. The only thing copied during a mutation is the branch from root to altered leaf, while everything else is shared. The Vector is a very shallow and highly branching tree which has O log32(n), which is very similar characteristics to O(1).

### on the shoulder of giants

These algorithms are similar to those used in functional languages like Clojure and Scala. This project started out of curiosity of how the Persistent Vector in Clojure was implemented, and ended with a total rewrite, both to get to know all the nuts and bolts, as well as to achieve higher Java Collection compatibility. So don't blame Clojure for issues found here as this is a total rewrite. This is implemented with simple code that should be easy to follow rather than focusing on being performant. If you like working with this, I highly encourage you to go for Clojure, as it will leverage these ideas much further. On the other hand, sometimes you don't get to choose.

 <div align="right">
Choose immutability and see where it takes you

 /Stefan von Stein
</div> 

