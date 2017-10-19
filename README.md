# taijitu
[![Build Status](https://travis-ci.org/someth2say/taijitu.svg?branch=master)](https://travis-ci.org/someth2say/taijitu)
[![Quality Gate](https://sonarqube.com/api/badges/measure?key=org.someth2say.taijitu%3Aroot&metric=alert_status)](https://sonarqube.com/dashboard?id=org.someth2say.taijitu%3Aroot)

TL;DR;
Utility for comparing the contents returned from different SQL queries, and provide differences and missing entries.

# The basis
taijitu is based on the following concepts:
- Comparing tables it complex enough to not fitting into a CLI interface. So everything should be provided prior usage into properties files.
- Despite it is called `taijitu`, in fact it does compare the results from queries. So the whole power of _SQL_ can be used to obtain the data to be compared.
- Inputs are named `source`and `target`, order being important to indentify the origin of the data. 
- Outputs are:
 - Entries appearing in `source` but not in `target`. Those are named `missing in target`
 - Entries appearing in `target` but not in `source`. Those are named `missing in source`
 - Entries appearing in both, but having different contents. Those are named `differences`

# The properties
When running a runtime, one or several properties files should be provided, in order to define a) data sources, b) runtime(s) to run anc c) behaviour for the application.
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
Properties defining a runtime are prefixed (obviously) by the root `runtime`.

Each runtime should define the following entries:

- `runtime.test_name.disabled=false`: If `true`, just skips the runtime when running.

- `runtime.test_name.fields = field1, field2...` : Defines the list of columns that are related to the runtime somehow.
This field have many purposes, like creating the header for reports, or expanding `*` in SQL queries.

- `runtime.test_name.fields.compare = field1, field2`: Defines wich fields should be compared looking for differences in contents.

- `runtime.test_name.fields.key = field1, field2`: List of fields that should be used as keys.
Keys are used for matching entries from both queries: entries will be considered the same if all fields in `key` property match.
_Note: Currently there are no `fields.source.key` nor `fields.target.key` entries defined. This means both queries (source and target) should have the same key field names.
This may change in future releases._

- `runtime.test_name.source.query = select * from test` and `runtime.test.test1.target.query = select * from test`: Sets the queries to be used to obtain `source` and `target` data.
_Warning: All fields in `fields` property should be provided by those queries! Else there may be dragons._
_Note: Setting `setup.queryOptimization` property to `true`  will allow the application to replace the `*` in "select * from" by the lists of fields in `fields` property.
Some DBMS perform much better if `*` is not used. _

- `runtime.test_name.source.database = test` and `runtime.test_name.target.database = test`: Sets the database used for `source` and `target`.
Should refer to database identifiers as defined in `database` section.

## Behaviour configuration
Some extra features may be defined as per execution basis in properties files.

### Query parameters
In order to set a common value to several queries, that also can be changed easily, query parameters are introduced.
A query parameter is defined with the `parameter` keyword:
```
parameters.testDate = 20120907
```
And then, it can be used in any query, by using the parameter name between curly brackets:
```
runtime.test_name.source.query = select * from test where date='{testDate}'
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

- `setup.precisionThreshold = 0`: When comparing numeric entries, some threshold will be permitted. If the difference between two numeric columns is _below_ threshold, no difference will be assumed.

- `setup.queryOptimization  = true`: If set to true, queries starting by "select * from..." will have the `*` replaced by the list of fields, as provided in `runtime.test_name.fields`.
This may sometimes improve performance, and avoid typos when copying the list of fields.

- `setup.plugins   = org.someth2say.taijitu.plugins.taijituWriter,org.someth2say.taijitu.plugins.taijituSQLProvider`: Comma separated list of `thread plugins`.
Each hook will perform different actions before and after each runtime is executed. Those can be understand as plug-ins for the runtime architecture.
 Currently, only those two hooks are provided:
 - `org.someth2say.taijitu.plugins.taijituWriter`: Write the runtime results into XLS or CSV files, as per configuration properties.
 Also can dump runtime statistics in those same formats.
 - `org.someth2say.taijitu.plugins.taijituSQLProvider`: Generate a SQL text file, with the needed SQL sentences for repairing found differences.
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

