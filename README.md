
# Stonehorse.Grit, persistent data structures for Java

Grit is a collection of persistent collections. These are immutable containers with mutation as expression. 

These collections provide mutation by returning modified collections rather than mutating them in place. The source always remain untouched. Mutating methods inherited from Java Collection API will unconditionally throw UnsupportedOperationException. These implement the mandatory requirements of Java Collections, as mutating methods are optional and can safely be used where read only collections are acceptable. 

As an example: consider adding something to an ordinary java ArrayList
```java
list.add("something");
```
The ArrayList is updated in place and the state prior to mutation is lost for all refering to it. 

The corresponding PersistentVector throws up on add, but instead provides the with-method that returns the altered list:
```java
list=list.with("something");
```
The with-method does not mutate the list and all referencing it still sees the list as prior to mutation. Instead the reference list will referre a new value, list with "something" due to the assignment. 

If you want to remember the state prior to mutation, you simply assign the new list to some other reference, or final value if you prefer.
```java
another = list.with("something")
```
The list reference will remain the same as prior to mutation. Anyone referring to the list and wants the mutation will have to go and fetch the new reference, when prepared to accept changes.

This technique reduces code complexity. Collections start to behave similar to e.g. Strings and becomes easier to reason about. You can safely make assumptions about stable state and safely pass your collections to less trusted libraries as these won't be able to save and alter them at unexpected moments. State won't change until you are prepared. It is a panacea for writing immutable composites.


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

The factory classes [Vectors](https://stefanvstein.github.io/stonehorse.grit/stonehorse/grit/Vectors.html), [Maps](https://stefanvstein.github.io/stonehorse.grit/stonehorse/grit/Maps.html), [Sets](https://stefanvstein.github.io/stonehorse.grit/stonehorse/grit/Sets.html) are your starting points, while [PersistentVector](https://stefanvstein.github.io/stonehorse.grit/stonehorse/grit/PersistentVector.html), [PersistentMap](https://stefanvstein.github.io/stonehorse.grit/stonehorse/grit/PersistentMap.html), [PersistentSet](https://stefanvstein.github.io/stonehorse.grit/stonehorse/grit/PersistentSet.html) are the abstract interfaces representing the structures. These extend Java Collections interfaces List, Map, and Set, but act as read only versions of them. Similar to wrapping them in Collections.unmodifiableCollection from Java Collection perspective.

### With or without

With and Without are the basic methods used to mutate, and as explained above these methods returns new versions of the collections rather than changeing them. These basic methods add and remove elements on the most effective position, which is dependent on collection type. For a vector the most effective possition is at the end. While for a set it is unknown.

```java
PersistentList<Integer> list=Vector.vector().with(1).with(2);
```
..will give you a vector of [1, 2]

The expression 
```java
list.without()
```
...would return a vector [1] as the second element, the most effective, would be removed.


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

