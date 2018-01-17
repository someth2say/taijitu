# Taijitu
[![Build Status](https://travis-ci.org/someth2say/taijitu.svg?branch=master)](https://travis-ci.org/someth2say/taijitu)
[![Quality Gate](https://sonarqube.com/api/badges/measure?key=org.someth2say.taijitu%3Aroot&metric=alert_status)](https://sonarqube.com/dashboard?id=org.someth2say.taijitu%3Aroot)

TL;DR;
Equality in JVM based languages is incomplete, as well as many equality-related contracts. 
Taijitu provides an implementation for both external and internal equality contracts (as defined [here](BACKGROUND.md)) that enforce 
completeness for equality-based contracts.

Also, based on this implementation, Taitiju provides:
- Several implementations for value and composite equality contracts 
- Versatile implementations for stream equality
- Sample implementations for Equality-aware collections: HashMap, LinkedList, etc. 
- A standalone comparison implementation for several data source formats (SQL queries, CSV files...): [Taijitu CLI](cli/README.md)

## Interfaces
Interfaces (or aspects) used to implement concepts previously defined are classified in two: external and internal equalities:

### External Equality
External equalities are instances able to provide equality capabilities to a different class' instances.

##### Equalizer
The root for equality aspects structure. An `Equalizer` is an (external) object being able to compare two instances for equality, in a given context. 
`Equalizer` defines the signature for the method that (externally) compare two instances:

        boolean areEquals(EQUALIZED equalized1, EQUALIZED equalized2) { ... }

Simple, isn't it? Just defines a method for checking if objects are equals.

##### Hasher
A `Hasher` is an object that can provide a `hashCode` for other instance. The only method defined by this interface is:

    int hash(HASHED hashed);
 
Several considerations about `Hasher`:
- `Hasher` interface extends `Equalizer` interfaces. That means, all `Hasher` instances should be also `Equalizer`s. 
This enforces the fact that `hashCode` contract is based on `equals`, and forces the developer to implement both methods.

##### Comparator
Wait! Comparator interface already exists in Java!

That's true. In fact, Taijitu's `Comparator` interface directly extends from Java's `Comparator`, and add no methods.
The only difference is that Taijitu's `Comparator` also extends from `Equalizer`, forcing the developer to also implements
equality methods (and protecting the contract).

### Internal Equality
Also, the same way we defined aspects (interfaces) for external equality contracts, we can define aspects for internal equality contracts,
strengthening it to avoid miss-implementations. Parallel to external equalities, we define three interfaces:

#### `Equalizable`
Classes that define a default internal equality. 

Java language forces the signature for the `equals` method to:

    boolean equals(Object obj);

Despite this is enough for all cases, Taijitu adds a second method, restricting the class for the parameter:

    boolean equalsTo(T obj);

This methods is not absolutely required, but useful for skipping the infamous `instanceOf` checks.

#### `Hashable`
 Classes that define both internal equality AND hash.

Again, Java language forces the signature:

    int hashCode();
    
#### `Comparable`
 Classes that define both internal equality AND instance comparison.

Taijitu `Comparable` extends Java `Comparable`, for compatibility purposes. But Taijitu's comparable
extends `Equalizable`, forcing classes to define both `equals` and  `compareTo` methods.

#### Mixing equalities
Can't a class be both `Comparable` and `Hashable`?

Of course they can! Both `Comparable` and `Hashable` are interfaces, so you only need to implement both.

But Java language have a limitation in this situation: You can not reference multiple aspects (interfaces) in parameters or return types!
In other words, you can not write:

    Comparable<Person>&Hashable<Person> getComparableAndHashablePerson(); 

The solution, despite a bit clumpy, is creating an interface extending both `Comparable` and `Hashable`: `ComparableHashable`

This way, you can refer to both aspects in a single name:

    ComparableHashable<Person> getComparableAndHashablePerson();

Luckily, we only have two equality-based contracts! Else, combinations will explode exponentially!

By the way, the same mixing can be done for external equalities, obtaining the `ComparatorHasher` interface.  

#### Equality descriptions
Having this capacity when performing comparisons is great. 
But sometimes it is great (ir not required) not only be able to tell if instances are equals or not, but also provide a reason for the response.

Taijitu equality (and hence, all equality-based contracts) provide a set of methods and classes for providing this reasoning.

First thing to be done is modeling the equality reasons responses. Taijitu bases its responses in two classes:
- Unequal: Wraps two instances that have been reasoned to be not equal, as well as the equality instance used for that reasoning.
- Missing: Given two containers/collections/streams, describes an instance contained in one of them, that have no "equal" element on the other container.
- Difference: Can be an `Unequal` or a `Missing`. Understand it as a super-class for those. 
 
If no differences are found between two instances, reasoning simply will provide no Unequal/Missing instances.

Now the responses are modeled, need a method for reasoning the response, isn't it? Taijitu defines a method named `underlyinDiffs`.
This method will return a (lazy) stream of `Difference` instances, explaining the differences actually found in provided instances.

Note that sometimes (mainly for Value equalities), `underlyinDiffs` may return `null` for equal instances, instead of an empty stream. 
This is done for performance reasons, so clients do not actually need to consume an empty stream to check the simplest equalities. 

## Implementations
We have now interfaces (or aspects) defining all equality concepts as per [theory](BACKGROUND.md). 
Let's see how can we make them real.

### Equalities
For clarification purposes, Taijitu divides equalities in three levels:

- Value equalities: Those computed actually onto a single value: an Integer, a String, a Date... 
Most of the time, they define alternative equality for already-defined classes.

- Composite equalities: Is the minimal definition for an equality, as defined by a) a bunch of functions extracting values from the instance, and b) value equalities to be applied to the extracted values.     

- Stream equality: Generic implementations for comparing streams, depending on its characteristics.  


#### Value equalities
Value equalities are the simplest ones. They are just the minimal needed code needed to implement equality interfaces onto simple Java classes:
- DateThreshold: Allow to `equalize` and `compare` two dates, ignoring differences below a defined number of miliseconds.
- JavaComparable: Externalization for comparable methods already defined in the `Comparable` class.
- JavaObject: Externalization for equality methods already defined in any Java class.
- NumberThreshold: Allow to compare any `Number` class, retaining only a defined number of decimals.
- ObjectToString: Allow to compare two different instances based only on their string representation.
- StringCaseInsensitive: Allow to compare `String` instances, ignoring case.

Following table shows capabilities for each value equality:

| Equality              | Class compared | Equalizer | Hasher | Comparator |
| --------------------- | -------------- | --------- | --------- | ---------- |
| DateThreshold         | Date           | :heavy_check_mark: | :heavy_multiplication_x: | :heavy_check_mark: |
| JavaComparable        | Comparable     | :heavy_check_mark: | :heavy_check_mark: | :heavy_check_mark: |
| JavaObject            | Object         | :heavy_check_mark: | :heavy_check_mark: | :heavy_multiplication_x: |
| NumberThreshold       | Number         | :heavy_check_mark: | :heavy_check_mark: | :heavy_check_mark: |
| StringCaseInsensitive | String         | :heavy_check_mark: | :heavy_check_mark: | :heavy_check_mark: |
 
Many more value equalities may be introduced in a near future.

#### Composite equalities
A composite equality is the a minimal definition for equality, as it is delegating its computation to a list of:
- Functions extracting values form each compared instance
- Another equality (of the same type) comparing extracted values.
- A composition function for delegated equality results.

It's easier to understand with an example. 
Let's get a look at the definition for the equality, as defined in, per example, URL (with a bit of refractor, for clarity):

```Java
public class Date {
    //...
    public boolean equals(Object obj) { 
        if (!obj instanceof Date) return false;     // Casting: Make sure classes are compatible
        Date other = ((Date) obj);                  // More casting...
        int thisTime = this.getTime();              // Extracting: Get attributes actually relevant to comparison
        int otherTime = other.getTime();            // More extracting...
        boolean sameTime = (thisTime == otherTime); // Delegated comparison: Delegates to integer equality 
        return sameTime;                            // Composition: Only will return "true" if all delegated comparisons are "true"
    }
    //...
}
```

The basis of a `Composite` equality relies on the idea that every "equality" contract (even hash or comparison) can be defined given the following:
- A list of extractors: In the example, the `getTime` method.
- An equality contract to be applied to each extracted value: Here, the default integer equality
- A composition for each delegated equality result: Here an "and" for all delegated equality values.

Let's place another example: The infamous `HashMap.Node` class:
```Java
public class Node<K,V> implements Map.Entry<K,V> {
        //...
        public boolean equals(Object o) {
            if (!(o instanceof Map.Entry))
                return false;
            Map.Entry<?,?> e = (Map.Entry<?,?>)o;

            return (key==null ? e.getKey()==null : key.equals(e.getKey())) &&
               (value==null ? e.getValue()==null : value.equals(e.getValue()));
        }
        //...      
}
```
Here, the extractors are `getKey` and `getValue` methods, delegated equalities are `Object`s `equals` methods, 
and composition (again) is `AND`.

So, we can actually wrap all this to parameters for a class, something like the following:

```java
    new CompositeEqualizer<Node>.Builder()                  //1
        .addComponent(Node::getKey, JavaObject.EQUALITY)    //2
        .addComponent(Node::getValue, JavaObject.EQUALITY)  //3
        .build();                                           //1
``` 

The extractors here are `Node::getKey` and `Node::getValue`. 
The delegated equalities are both `JavaObject.EQUALITY`, that simply delegated to default equality.
The equality result composition is defaulted in the equalizer class: true if all equalities actually return `true`

Note that we are actually skipping the "class casting" section.
The reason is that the design for equality:
```java 
public interface Equalizer<EQUALIZED> {
    //...
    default boolean areEquals(EQUALIZED equalized1, EQUALIZED equalized2) {
        return underlyingDiffs(equalized1, equalized2).count() == 0;
    }
    //...
}
```
While `Object`s `equals` method accepts an `Object` (making the cast required), the method `areEquals` actually require, 
at compile time, that BOTH elements satisfy the EQUALIZED type. Hence, the cast is unnecessary.
 
Also worth commenting, that composite equalities DO NOT PERFORM ANY null-checks! It delegates responsibility to:
- Extractors: Should be able to extract from `null`, or fail as required.
- Delegated equality: Should be able to handle comparison against `null` values, or fail as required.  

As already said, result composition is hardcoded into equality classes.
Here a little summary of how composition is done. 

| Composite           | Composition result |
| ------------------- | ------------------ |
| CompositeEqualizer  | `true` iif all delegated equalities return `true`, else `false` |
| CompositeHasher     |  Multiplies the delegated hash of each extracted value by 31, then recursively add the hash for the rest |
| CompositeComparator |  Iterating extracted values, return the first delegated comparison not returning 0. Else returns 0  |
 

Finally, you may be asking: Why all this complication, if we can actually write the equalizer as we need?
 And you are right... if you actually can!
 Sometimes, the extractor methods or the delegated equalities are undefined at compile-time, and are only known at runtime.
 Think about an Excel-like table, where each row is an instance. You may choose to order the table given randomly chosen columns, and in a randomly chosen order.
 Having composite equalities, you can create the `CompositeComparator` on demand, and use standard sorting algorithms on the table.
 
#### Stream equalities
Strangely, being able to define equality for streams was the initial motivation for Taijitu.
Taijitu was initially conceived as a swiss-army-knife for comparing the results getting back from SQL queries.
The initial problems actually faced (unknown columns or column type, relative similarity, etc..) drove to the design for composite equalities (and lately, for [Taijitu CLI](./cli/README.md).

But there was still a problem: Given two streams of records, with an unknown at compile-time equality for rows, how we decide if streams are actually "equal"? 
How can I found (in the most general way) differences between them?

A lot of analysis was required, but finally we found a way to classify streams, and then face the equality problem.
Streams were classified into three kinds:

##### Positional (or basic) streams
Streams where every single provided element have a distinct meaning, and they can not be skip.

The simplest example is the stream view for an array. It is known that first element from the stream will be first element from array,
second element for the stream came from second element from array, and so on... Stream can not skip any array element, nor provide more or 
fewer elements than the size for the array.  

So comparing positional streams is just as easy as comparing each and every generated element from both streams, 
and generating the right `Difference` object when needed.
 
This is the approach taken for comparing many current classes in Java, like `String` or `AbstractList`.

Class implementing this equality is `SimpleStreamEqualizer`.

##### Sorted streams
Sorted streams (do not mix with streams with the `SORTED` characteristic), are streams that ensure all their elements will be provided in a defined order.
In other words, that, given a defined comparison, if element A is provided **before** element B, then `compare(A,B)<=0`.

Despite this may seem banal, this fact provides a huge hint for comparing two streams. Given one element provided by each stream (A,B):
- `compare(A,B)<0`, means that A is previous to B in the comparison order. Since B is the current element, 
and there was none before, we can conclude there is a missing entry in B (or an extra element in A, equivalently).
We can move forward with A's stream, looking for the element matching B, but we can't move B's stream forward.. 

- `compare(A,B)>0` shows the opposite situation, where B have an element not present in A. 

- `compare(A,B)=0` indicates both elements are actually the same (as long as comparison equality contract is satisfied). 
Nothing to report, and can move both streams forward.  

As far as I know, there is no class implementing this behaviour in Java, but `ComparableStreamEqualizer` does it in Taijitu 
(despite the name, `Comparable` this class does not require stream elements to be comparable, but just require a `Comparator` for them).

##### Mapped streams
What if there is nothing known from the streams we can actually use to perform the comparisons?
On worst case, there is only the option to keep every unmatched element from the stream, waiting for the matching element is produced
(if it is ever produced!).

But, instead of trying to match every saved element to every other element from the stream, some cases we can use a shotcurt: hashing.

Hashing creates a "signature" for the element. As for hash contract, two elements "equals" must have the same `hashCode` (the signature).
So instead of comparing each new element against every single unmatched element, we can reduce the search to only those unmatched elements
having the same `hashCode`.

And, indeed, that's what Java `HashSet` does.

So we can add the elements from the stream into a HashSet. If there already was an element with the same hashCode, then we can test equality
on both, and the result will be used to create the final equality response.

`HashingStreamEqualizer` os the class implementing this algorithm, with some minor implementation details (i.e. uses a `HashMap` instead of
a `HashSet`).

**:warning:** There is a big caveat with this implementation!
As you read, `HashingStreamEqualizer` maps all incoming elements, waiting for matching elements to be produced.
If a matching element is found, the algorithm will do nothing (if elements are equals) of generate an `Unequals` instance.
If there are no more elements in any of the streams, all unmatched elements will be reported as `Missing` instances.
Despite generated results are built lazily, in case of an infinite input, process will not terminate, or consume 
an unbounded amount of memory until process crashes.

So warning about infinite streams!

### Integrations 
Now we have the equality infrastructure, we can define equality for classes and for streams (collections). 
But how can equality be used in current programs?

Obviously, you can always invoke equality methods on the defined equality: `if (equality.areEquals(A,B))...`
But this is just the tip of the iceberg.  There are many other places where equality (and equality-based contracts) are used, but
out of reach for the developer.

In order to be able to inject equality into those places, Taijitu offer three different approaches, each one with some benefits and caveats: 

####  Equality wrappers
Say you (as a developer) need to provide an instance (i.e. an `String`) to a third party library (via an API). 
And let's say this library somehow relies on equality for the instance for doing its work.
This dependence is reasonable (because Java force all instances to have some kind of equality).
But, for business purposes, you need this library to ignore the case of the strings: should treat "Hi","HI" and "hi" as the same instance.
Unluckily, the library relies on `Object.equals` methods, and can not be instructed to rely on `String.equalsCaseInsensitive`.

Then, equality wrappers are the way to go. 

Equality wrapper are classes that:
1. Accept and keep and instance for any class (V)
2. Accept and keep and equality for that same class (E)
3. Delegate its own internal equality to provided external equality (E) onto the provided instance (V).

In other words, and equality wrapper "wrap" an instance and an equality, in order to transform external equality into internal equality.

So, in our sample situation, we can just wrap `String` instances with a `StringCaseInsensitive` equality, before sending them to the third party library.
You can use direct class instantiation...
```java
    EqualizableWrapper<String> wrappedString = new EqualizableWrapper<>(string, StringCaseInsensitive.EQUALITY);
```
... or use an static factory, that simplifies generating multiple wrappers for the same equality:
```java
    EqualizableWrapper.Factory<String> factory = new EqualizableWrapper.Factory<>(StringCaseInsensitive.EQUALITY);
    EqualizableWrapper<String> wrappedString = factory.wrapp(string);
    EqualizableWrapper<String> anotherWrapped = factory.wrapp(anotherString);
```

Having the instances wrapped, now all comparisons will go through external equality:
```java
        EqualizableWrapper.Factory<String> factory = new EqualizableWrapper.Factory<>(StringCaseInsensitive.EQUALITY);
        EqualizableWrapper<String> wrappedString = factory.wrap("Hola");
        EqualizableWrapper<String> anotherWrapped = factory.wrap("HOLA");

        assertEquals(wrappedString,anotherWrapped);              
        assertTrue(Collections.singletonList(wrappedString).contains(anotherWrapped));    
```

##### Caveats:
- New instance should be created to wrap any instance. Despite minimal, this implies both memory and time overhead.
- The instance providing internal equality is not same class for the original instance. So if class restrictions apply, this solution will not work.

####  Equality proxies
The main problem about using wrappers for 'internalize' equality is that the resulting instance does not satisfy original class anymore.
Taijitu provides a second approach for avoiding this problem: using dynamic proxies.
Dynamic proxies classes that:
1. Are completely generated on run-time. 
2. Extend (or implement) the original class
3. Can define behaviour for each method on the original class: delegate to original instance, delegate to another instance, or perform whatever the developer decides to.

So Taijitu can create instance proxies that delegate ALL methods to the original instance, but equality-related methods. Those methods are delegated to external equality.

Following code sample uses `ProxyFactory` to create a `Date` proxy, that delegates equality to the default `DateThreshold` equality.
```java
    Date now = new Date();
    Date nowProxy = ProxyFactory.proxyEqualizer(now, DateThreshold.EQUALITY, Date.class);
```
Now, we can use the `proxy` like any other `Date` instance:
```java
    assertTrue(nowProxy instanceof Date);
    Date future = new Date(now.getTime() + 400); // In future, but below default threshold
    assertEquals(nowProxy, future); 
    
    Collection<Date> dates = Arrays.asList(nowProxy);
    assertTrue(dates.contains(now));
    assertTrue(dates.contains(future));
```



##### Caveats:
- Both new class and new instances should be created. Despite classes can be cached, instance creation still have memory and time overhead.
- Final classes (i.e. String) or complex class structures (i.e. Stream) can not be proxyfied.
- Non-reflexiveness: Proxy instances have equality methods updated, while non-proxied instances retain the original methods. That means
`proxy.equals(date)` can return true, while `date.equals(proxy)` returns false.


####  Equality-aware collections



## Real life examples.
Ok, we have now all equality stuff defined... what can we do with it?
Let's place a real world example.

> Manager: We have a List of `String` objects. We need to sort them. How you do that?

> Developer: Easy. `Collections.sort`

> Manager: Oh, sorry, I forgot to mention... I want to sort the Strings, but case is not important (and you can not create new upper/lower-cased instances)

> Developer: Ok... `Collections.sort` also accepts a `Comparator`... so we can create a `CaseInsensitiveComparator` for `String`s, and provide it.

> Manager: Well, I said sort? In fact, I was thinking about sorting so we can easily remove duplicates... but maybe there is a better approach.

> Developer: Grr... We can not use a `Set`, because it will only use the default string equality (that is case sensitive). 
We can use the `CaseInsensitiveComparator` I just created to compare every pair of `String`s, but this will be really costly (O(n^2)).

> Manager: And why don't you provide this "comparator" thing to the `Set`? It will take care of de-duplicating elements, isn't it?

Manager is actually right! If you can provide an external comparator to sort a collection, why can't you provide an external `equalizer` to build a set?

But currently, you can't...

With Taijitu, you have three ways to face this situation:
1. Create a wrapper for the object to be "equalized" that actually uses the `Equalizer` as the natural equality.
2. Create a proxy object (subclass for the "equalized" class) that delegates all methods to the original instance but equals/hashCode/compareTo, that will delegate to the right `Equalizer`
3. Use an alternative collection class that allow using external equality, instead of default equality.
   
### External Equality wrappers
Taijitu offers a set of specialized wrapper for each kind of equalities.
Those wrappers use provided external equality as default internal equality, so wrappers can be used in standard Java collections.

Example: Wrapping String so we can use `StringCaseInsensitive` external equality. 
```
    Set<HashableWrapper<String>> set = new HashSet<HashableWrapper<String>>>();
    StringCaseInsensitive equality = new StringCaseInsensitive(); // Default equality implementation
    HashableWrapper.Factory<String> factory = new HashableWrapper.Factory<>(equality)); 
    set.add(factory.wrapp("hola"));
    set.add(factory.wrapp("Hola"));
    assertEquals(1,set.size());
    String unwrapped = set.iterator().next().getWrapped();
    assertTrue(equality.areEquals(unwrapped, "hola");
```

This approach allows to use external equality (of all kinds) with any collection class, and that's good.

But this does not came for free (unluckily). Objects used on the collection are not the same objects we had, but another object (wrappers). 
This implies some overhead when creating the collection (new instances created for each object) and when retrieving (objects should be unwrapped before being used).

### External Equality proxies
There is a similar approach that faces some of the wrapper problems.
Instead of creating a Wrapper object, why can't we extend the original class, 
delegating all methods to the original instance, but delegating equality methods to the equalizer?

In fact, we can. Those are the so-called *dynamic proxies*, and Taijitu relies on BiteBuddy library for creating them.

```
    Date date = new Date();
    Date otherDate = new Date(date.getTime() + 400); // Make sure dates have a small time difference...
    DateThreshold<Date> equalizer = new DateThreshold<>(1000); // ... but threshold will shallow it.
    Date proxy = ProxyFactory.proxyEqualizer(date, equalizer, Date.class);    
    assertEquals(proxy, otherDate); // proxy.equals(otherDate)==true \o/   
```

This capability allows us to keep the object's class (`Date`, in the example), while "re-writing" the equality to our needs.
Now, we can use collections without fear!

But again, that is not a perfect solution. Despite we re-wrote equality for one of the objects (`date`), the other object may stay intact.
This means that, while `proxy.equals(otherDate)` will be *true*, `otherDate.equals(proxy)` will return *false*. This is a break for *transitive*
and *symmetric* properties for `equals` contract. 

For collections, this will not be a big deal, as long as we can assure ALL objects send to the collection are actually proxies.
But asserting so is depending on the situation.  
  
 > Note: Proxy capabilities are still experimental, and there are some cases proxies can not be easily created (i.e. for Streams). Use with caution.
 
### External Equality collections
There is a third alternative, that should not be impacted by caveats from wrappers or proxies: collections and methods based on equality that
accept an external equality (the same way most of them already do with `Comparator` in Java).

Taijitu provides some basic implementation for most common Java `Collection` datastructures:
- ArrayList
- HashMap
- HashSet
- LinkedHashMap
- LinkedHashSet

Note that there are no implementations for `Tree` or `Deque` based collections. The reason is that those rely just on external comparison,
and Java already provide this functionality (via `Comparator`).

If fair to say equality collections is neither a perfect solution. Take a look at the following code:
```
    HashSet<String> hashSet = new HashSet<>(new StringCaseInsensitive());
    hashSet.add("Hello");
    assertTrue(hashSet.contains("HELLO")); // 1) This actually works as expected, returning true
    assertTrue(hashSet.iterator().next().equals("HELLO")); // 2) This assertion fails!
```
At first sight, this 1) and 2) should return the same value! But checking in detail shows one detail that changes it all: in 1), the responsible for 
performing comparisons on elements is the set (the `contains` methods, precisely); but in 2), we are **extracting** the element from the set, and then asking 
this same element to compare itself to another value -hence the responsible for comparison is the element-.

This tiny difference changes it all: it moves the comparison from the collection (the one we provided comparator to) to the elements (that we are willing to left
untouch).















taijitu is based on the following concepts:
- Comparing tables it complex enough to not fitting into a CLI interface. So everything should be provided prior usage into properties files.
- Despite it is called `taijitu`, in fact it does compare the results from queries. So the whole power of _SQL_ can be used to obtain the data to be compared.
- Inputs are named `source`and `target`, order being important to indentify the origin of the data. 
- Outputs are:
 - Entries appearing in `source` but not in `target`. Those are named `missing in target`
 - Entries appearing in `target` but not in `source`. Those are named `missing in source`
 - Entries appearing in both, but having different contents. Those are named `differences`

# The properties
When running a context, one or several properties files should be provided, in order to define a) data sources, b) context(s) to run anc c) behaviour for the application.
Properties are provided in a hierarchical manner. So you can define things like the following:
```
property=value
property.child=child_value
property.child.grandchild=granchild_value
```
Some levels for the hierarchy can be avoided, if needed.
Even this hierarchy define the _default_ values for children. This means, if the application is looking for a property like `A.B.C.D.prop=value`, and it is not defined, but `A.B.prop=value` is, `value` will be found and used.
This is pretty usefull for avoiding repeating the same value several times:
```
#Instead of writting this:
A.B.1.prop=value
A.B.2.prop=value
A.B.3.prop=value
...
#You can just write
A.B.prop=value

#and all A.B.*.prop will have the same value.

```

Let's depict the details for each section.

## Database definitions
First thing to do is define what databases should be used to retrieve data. At least, one database should be defined.
A database is defined by providing four properties:`connectionString`, `driver`, `username` and `password`.
All database properties should be prefixed by the `database` root, and **followed by an identifying name**.
```
database.test.connectionString=jdbc:h2:mem:test
database.test.driver=org.h2.Driver
database.test.password=password
database.test.username=user
```
_Note: This way of storing database passwords is not secure. Cryptographic capabilities are to come in next releases._
Be aware that database identifying name should be unique.

## Comparison definitions
Properties defining a context are prefixed (obviously) by the root `context`.

Each context should define the following entries:

- `context.test_name.disabled=false`: If `true`, just skips the context when running.

- `context.test_name.fields = field1, field2...` : Defines the list of fieldValues that are related to the context somehow.
This field have many purposes, like creating the header for reports, or expanding `*` in SQL queries.

- `context.test_name.fields.compare = field1, field2`: Defines wich fields should be compared looking for differences in contents.

- `context.test_name.fields.key = field1, field2`: List of fields that should be used as keys.
Keys are used for matching entries from both queries: entries will be considered the same if all fields in `key` property match.
_Note: Currently there are no `fields.source.key` nor `fields.target.key` entries defined. This means both queries (source and target) should have the same key field names.
This may change in future releases._

- `context.test_name.source.query = select * from test` and `context.test.test1.target.query = select * from test`: Sets the queries to be used to obtain `source` and `target` data.
_Warning: All fields in `fields` property should be provided by those queries! Else there may be dragons._
_Note: Setting `setup.queryOptimization` property to `true`  will allow the application to replace the `*` in "select * from" by the lists of fields in `fields` property.
Some DBMS perform much better if `*` is not used. _

- `context.test_name.source.database = test` and `context.test_name.target.database = test`: Sets the database used for `source` and `target`.
Should refer to database identifiers as defined in `database` section.

## Behaviour configuration
Some extra features may be defined as per execution basis in properties files.

### Query sqlParameters
In order to set a common value to several queries, that also can be changed easily, query sqlParameters are introduced.
A query parameter is defined with the `parameter` keyword:
```
sqlParameters.testDate = 20120907
```
And then, it can be used in any query, by using the parameter name between curly brackets:
```
context.test_name.source.query = select * from test where date='{testDate}'
```

## Program behaviour
The following properties set-up general properties about how application will behave:

- `setup.fetchSize = 1024`: This define number of entries that will be retrieved from database every time more data is needed.
Smaller numbers mean many retrievals from database will be done, impacting performance. High numbers mean a lot of data will be fetched before being processed, impacting memory consumption.

- `setup.outputFolder = "/temp"`: By default, program output will be dump to the current path. This behaviour can be changed by setting the this property to a folder path.
_Note that, inside output folder (default or set), a new folder will be created, named after the system date in `yyyyMMdd` format, and all output will be stored there._
_Warning: This same property may be used by plugins to identify the folder where data should be stored._

- `setup.consoleLog = DEBUG`: Sets the log level for console. Default is `INFO`.

- `setup.fileLog = INFO`:If set to any level but `OFF`, log file will be created in `setup.outputFolder` with appropiate log data, given the provided log level.

 _Note: Accepted log levels are OFF, FATAL, ERROR, WARN, INFO, DEBUG, TRACE and ALL, from more silent to more verbose._

- `setup.precisionThreshold = 0`: When comparing numeric entries, some threshold will be permitted. If the unequal between two numeric fieldValues is _below_ threshold, no unequal will be assumed.

- `setup.queryOptimization  = true`: If set to true, queries starting by "select * from..." will have the `*` replaced by the list of fields, as provided in `context.test_name.fields`.
This may sometimes improve performance, and avoid typos when copying the list of fields.

- `setup.plugins   = org.someth2say.taijitu.ui.plugins.taijituWriter,org.someth2say.taijitu.ui.plugins.taijituSQLProvider`: Comma separated list of `thread plugins`.
Each hook will perform different actions before and after each context is executed. Those can be understand as plug-ins for the context architecture.
 Currently, only those two hooks are provided:
 - `org.someth2say.taijitu.ui.plugins.taijituWriter`: Write the context results into XLS or CSV files, as per configuration properties.
 Also can dump context statistics in those same formats.
 - `org.someth2say.taijitu.ui.plugins.taijituSQLProvider`: Generate a SQL text file, with the needed SQL sentences for repairing found differences.
 Will generate INSERT statements for missing entries, and UPDATE statements for differences.
 
- `setup.threads = 2`: Number of concurrent comparisons that will run anytime.
This is usefull for speeding up very big comparisons, at cost of 1) huge memory consumption and 2) interpolation for log entries.
Use with caution.

## Plugin configuration
Some hooks (plugins) require extra configuration to work. Those same can also be provided through property files:
Each plug-in is identified by a single keyword, that should be used as root for its properties.

### taijituWriter (keyword `output`)
- `output.format.csv = false`: If true, CSV files will be generated with differences and missing entries.
- `output.format.xsl = false`: If true, XSL files will be generated with differences and missing entries.
- `output.stats      = false`: If true, stats files will be generated. Format depend on previous configuration entries.
_Note: All `output` entries default to `false`, so no output will be generated (even if thread hooks are explicitly set!) if none is set to `true`._

### taijituSQLProvider (keyword `sqlProvider`)
- `sqlProvider.outputFile = taijitu.sql`: Sets the path and name for the file where generated queries will be stored.

