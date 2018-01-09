# taijitu
[![Build Status](https://travis-ci.org/someth2say/taijitu.svg?branch=master)](https://travis-ci.org/someth2say/taijitu)
[![Quality Gate](https://sonarqube.com/api/badges/measure?key=org.someth2say.taijitu%3Aroot&metric=alert_status)](https://sonarqube.com/dashboard?id=org.someth2say.taijitu%3Aroot)

TL;DR;
Equality in JVM based languages is incomplete, as well as many equality-related contracts. 
Taijitu provides an implementation for both external and internal equality contracts that enforce 
completeness for equality-based contracts.

Also, based on this implementation, Taitiju provides:
- A standalone comparison implementation for several data source formats (SQL queries, CSV files...)
- Several implementations for value and composite equaality contracts
- Versatile implementations for stream equality


# The basis (and a bit of theory)
The basic idea behind Taijitu is that `equality` in JVM-based languages is usually limited, if not wrong, in several ways:
- Equality is not (always) a responsibility for the object/class
- Equality do not enforce equality-based contracts. 

Lets go deep into those concepts:

#### Equality is not (always) a responsibility for the object/class
There is a simple question you can ask yourself to understand this concept: 
**When two instances are 'equals'?**

That simple question generated hundred discussions. Some people talk about object identity and key fields. 
Some others, refer to object interchangeability or equivalence. Others introduce object references and reference trees.

Who is right? Who is wrong? *They all are both right and wrong.*

Wait, what?! How can they be at the same time right and wrong? The answer is `context`. Depending on the context you are
interpreting the objects, equality have one meaning or another.

Let's place an example: When are two `Person`s "equal"?
- For **administrative purposes**, two `Person`s are *the same* if they have the same ID#.
- For a **facial recognition** system, they will be the same if they same *approximately* the same facial attributes.
- For **themselves**, they will be the same if they share the same memories and feelings.
- For **religion**, they will be the same if they have the same `soul` or `spirit` (even after reincarnating, they can be tha same)  

As you can see, equality is based not on the object itself, but the context defines the equality.

> Note: Understanding equality is context-based does not mean objects can not define their own equality (where the context
is the object itself). This is the so-called *default equality*, and this is the one actually implemented in most JVM-based languages.

#### Equality do not enforce equality-based contracts. 
Probably the following paragraph will be familiar for you:

     The general contract of hashCode is:
     
     - Whenever it is invoked on the same object more than once during an execution of a Java application, the hashCode method must consistently return the same integer, provided no information used in equals comparisons on the object is modified. This integer need not remain consistent from one execution of an application to another execution of the same application.
     - If two objects are equal according to the equals(Object) method, then calling the hashCode method on each of the two objects must produce the same integer result.
     - It is not required that if two objects are unequal according to the equals(java.lang.Object) method, then calling the hashCode method on each of the two objects must produce distinct integer results. However, the programmer should be aware that producing distinct integer results for unequal objects may improve the performance of hash tables. 

Yes, this is the contract defined for `hashCode` in Java. Nothing wrong with it, but this contract can just be ignored.
You can write down your `hashCode` implementation for your class, completely ignoring the `equals` implementation (if any!).
Probably, forget implementing `hashCode`, or faulty implementations are one of the most common errors for Java developers.

Another equality-based contract is the one for Java `Comparable` class:

      The natural ordering for a class C is said to be consistent with equals if and only if e1.compareTo(e2) == 0 has the same boolean value as e1.equals(e2) for every e1 and e2 of class C. 

The same applies here: implementing `Comparable` interface just provides the methods, not the contract.

### External equality
Now we understand the problems with equality-contracts... how we can face it?
Taijitu is based on the following idea: **Equality and equality-based contracts should be external to the class being compared**, so different equality 
concepts can be applied to same objects in different context.

For implementing this idea, Taijitu define the following interfaces (aspects):
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

##### Internal Equality
The same way external equality has been defined as an external way to "compare" instances, we also introduced the concept for "default equality".
Java (and many other JVM-languajes) define the default equality inside the class itself (with no more context that the class itself).
When equality is defined inside the class (with or without context), we have internal equality.

Also, the same way we defined aspects (interfaces) for external equality contracts, we can define aspects for internal equality contracts,
strengthening it to avoid miss-implementations. Parallel to external equalities, we define three interfaces:

- `Equalizable`: Classes that define a default internal equality. 

Java language forces the signature for the `equals` method to:


    boolean equals(Object obj);

Despite this is enough for all cases, Taijitu adds a second method, restricting the class for the parameter:

    boolean equalsTo(T obj);

This methods is not absolutely required, but useful for skipping the infamous `instanceOf` checks.

- `Hashable`: Classes that define both internal equality AND hash.

Again, Java language forces the signature:


    int hashCode();
    

- `Comparable`: Classes that define both internal equality AND instance comparison.

Taijitu `Comparable` extends Java `Comparable`, for compatibility purposes. But Taijitu's comparable
extends `Equalizable`, forcing classes to define both `equals` and  `compareTo` methods.

##### Mixing equalities
One last idea, before getting our feet wet.

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

## Real life examples.
Ok, we have now the external and internal equality defined... what can we do with it?
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
There is a third alternative, that should not be impacted by caveats from wrappers or proxies: that all collections and methods based on equality
accept an external equality (the same way most of them already do with `Comparator` in Java).















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

