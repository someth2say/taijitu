# Taijitu: Background
[![Build Status](https://travis-ci.org/someth2say/taijitu.svg?branch=master)](https://travis-ci.org/someth2say/taijitu)
[![Quality Gate](https://sonarqube.com/api/badges/measure?key=org.someth2say.taijitu%3Aroot&metric=alert_status)](https://sonarqube.com/dashboard?id=org.someth2say.taijitu%3Aroot)

# The basis (and a bit of theory)
The basic idea behind TaijituGfg is that `equality` in JVM-based languages is usually limited, if not wrong, in several ways:
- EqualityCfg is not (always) a responsibility for the object/class
- EqualityCfg do not enforce equality-based contracts. 

Lets go deep into those concepts:

## Equality is not (always) a responsibility for the object/class
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

## Equality do not enforce equality-based contracts. 
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

## Types of equality
Equalities can be classified in two dimensions:
- The implemented contract: Not only basic equality (defining `Boolean equals(A,B)`) but also hash (`int hash(A)`) and comparison (`int compare(A,B)`) 
- The place of implementation: **Internal** equality, defined inside the compared instances' class (also known as *default* equality), against **external** equality,
provided by a third class (generally defined by the context). 

Let's depict each classification deeper:


### Equality by place:

#### External equality
External equality is based on the following idea: **EqualityCfg and equality-based contracts should be external to the class being compared**, so different equality 
concepts can be applied to same objects in different context.

Java developers are already familiarized with this idea, but just for a single equality contract: the `Comparator` interface. This interface provides capabilities
to compare two instances, despite their classes define any comparison methods. 

### Internal equality
The same way external equality has been defined as an external way to "compare" instances, we also introduced the concept for **internal equality*, also 
named *default equality*.
Default equality is defined inside the class itself (with no more context that the class itself). 
When equality is defined inside the class (with or without context), we have internal equality.

Java implements internal equality in a "tricky" way: Implements both equality (`equals` method) and hash (`hashCode` method) in `Object` class.
This way, **ALL** objects will, by default, provide an implementation for those equality contracts (even if there is no reasonable definition for the class!).

### Equality contracts:
EqualityCfg contracts are all those "calculations" or "operations" that can be performed onto a class' instance, that are somehow dependant on one definition for equality. 


#### Equality:
EqualityCfg (the definition for two instances being "equals") is indeed a equality contract, as it depends on its own definition.
EqualityCfg can be described as a boolean function onto two instances (`Boolean equals(A,B)`).
But I do prefer to see it as a function that divide a class' space (the space with all possible class' instances) into two sub-spaces: instances "equals" to "A", and instances "not equals" to "A":
```
+--------------------------------------------------------------------------------------------+
|                                     |      |                                               |
|               False                 | True |                  False                        | <-- Class space division based on equality to A
|                                     |      |                                               |
+--------------------------------------------------------------------------------------------+
```
This contract is defined in Java by `Object`s `equal` method.

#### Comparison:
ComparisonCfg contract in defined in Java `Comparable` by interface. 
It divides the class' space in three sections: Those "lesser" then the compared element (`compare(A,B) < 0`), those "greater" (`compare(A,B) > 0`) and those "equal" (`compare(A,B) == 0`): 
 ```
+--------------------------------------------------------------------------------------------+
|                                   |          |                                             |
|                < 0                |   == 0   |                > 0                          | <-- Class space division based on comparison to A
|                                   |          |                                             |
+--------------------------------------------------------------------------------------------+
```

The equality contract indicates that, if two equals are "equals", then their comparison should be 0. 
In other words: the "== 0" sup-space should entirely contain the "True" sup-space.

#### Hash:
This contract is defined by Java given `Object`s `hashCode` method.
This equality divides the class' instances space in Integer.MAX_INT sections.

```
+---+---+---+---+----------------------------------------------------------+-----------------+
|   |   |   |   |                                                          |                 |
| 0 | 1 | 2 | 3 | ...                                                  ... | Integer.MAX_INT | <-- Class space division based on hash for A
|   |   |   |   |                                                          |                 |
+---+---+---+---+----------------------------------------------------------+-----------------+
```
The contract defines that, if two instances are "equals", both instances should map to the same sub-space. The opposing is not mandatory. 

Despite this definition is enough for our purposes, Hash contract also sets another expectations:
 - If two instances are "not equals", they should map to different sub-spaces (in other words, hash should "disperse").
This is not always possible, hence hash based implementations should perform a secondary "equals" check to validate instance equality.
 - Hash methods computational cost should be (much) lower than equal's.   
 
Those expectations make hash useful for using creating a "short-cut" for equality: If both instances have the same hash (easy to compute), 
they have great chances to be equals (costly to compute).

Worth noting Hash the "simplest" contract, given it does not define a relation between two instances, but between a single instance and `Integer`s 
instances space. On the contrary, EqualityCfg and ComparisonCfg contracts relates between two instances for the same class.

### Equality by place:

#### External equality
External equality is based on the following idea: **EqualityCfg and equality-based contracts should be external to the class being compared**, so different equality 
concepts can be applied to same objects in different context.

In other words, equality (and other contracts) are based on equality "context", and this context is not part for the compared classes, but something 'external'.

For implementation purposes, we can define tree aspects (interfaces) for classes defining external equality:
- **Equalizer**: Classes defining context and equality method for another class instances. 

- **Hasher**: Classes defining context and hashing method for another class instances. 
In order to keep entanglement with equality contract, **Hasher** should also define equality context and methods (i.o.w. extend **Equalizer**)

- **Comparator**<sup>1</sup>.: Classes defining context and comparison method for another class instances. 
In order to keep entanglement with equality contract, **Comparator** should also define equality context and methods (i.o.w. extend **Equalizer**)

### Internal Equality
The same way external equality has been defined as an external way to "compare" instances, we also introduced the concept for "default equality".
Java (and many other JVM-languajes) define the default equality inside the class itself (with no more context that the class itself).
When equality is defined inside the class (with or without context), we have internal equality.

For implementation purposes, we can define tree aspects (interfaces) for classes defining internal equality:
- **Equalizable**: Classes defining context and equality method for its own instances. 

- **Hashable**: Classes defining context and hashing method for its own instances. 
In order to keep entanglement with equality contract, **Hasher** should also define equality context and methods (i.o.w. extend **Equalizer**)

- **Comparable**<sup>1</sup>.: Classes defining context and comparison method for its own instances. 
In order to keep entanglement with equality contract, **Comparator** should also define equality context and methods (i.o.w. extend **Equalizer**)

<sup>1</sup> Java already defines both `Comparable` and `Comparator` interface. Sadly, both suffer the same problem: they rely on the fact Java's `Object`
class already define `equals` method, so inherently believe all objects are `Equalizable` by default. 
Despite that is true (you can invoke `equals` on any instance), this debilitate the comparison contract, allowing developer to define comparison without
any equality definition.

# Final thoughts
There is a third dimension of equality categorization that we have been intentionally avoiding here.
Let's get back to the beginning, when we defined what "equals" mean:

>Let's place an example: When are two `Person`s "equal"?
> - For **administrative purposes**, two `Person`s are *the same* if they have the same ID#.
> - For a **facial recognition** system, they will be the same if they same *approximately* the same facial attributes.
> - For **themselves**, they will be the same if they share the same memories and feelings.
> - For **religion**, they will be the same if they have the same `soul` or `spirit` (even after reincarnating, they can be tha same)  

Those sentences settled the idea that equality is a concept defined outside the compared instances' class.
But let's add a new question:
> - For a **police officer**, this `Person` is *the same* than the *picture* in the ID if they share some crucial facial attributes.

This sentence raises the option that we can actually compare a `Person` with a `Picture`, two instances for different classes!

Defining equality between different classes is named **Hibrid EqualityCfg**, and allow to compare instances that, despite having common attributes, 
do not belong to the same class. 

**Hibrid EqualityCfg** opens a door to an exponential explosion for equality cases, and drops a lot of unanswered questions. And, given I still haven't found
and actual benefit it, I will let exploration for this to the reader.

 
   



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

