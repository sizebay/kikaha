## Hazelcast Extension
Provide tight integration with the leading in-memory data-grid implementation ( [Hazelcast](http://hazelcast.org/) ).

### What it provides to you?
- Easy-to-use Hazelcast Instance configuration ( as a Cluster Node )
- Easy-to-use Hazelcast Instance configuration ( as a Client for Cluster Nodes )
- Full integrated with all _distributed data structures_ provided by Hazelcast
- Easier configuration environment with easy fallback to XML Based Configuration
- Easier configuration environment with easy programmatic configuration ( for ClientConfig and Config )
- and many more...

### Instaltion instructions
Just include the bellow maven dependencies to your ```pom.xml```. Note that Kikaha use [tRip](https://github.com/Skullabs/tRip) as an engine to provide implementations and allow our internal modularization.

```xml
...
    <properties>
        <version.trip>1.0-SNAPSHOT</version.trip>
        <version.undertow>1.0-SNAPSHOT</version.undertow>
    </properties>
...
    <dependencies>
        <!-- tRip Processor for IoC and Modularization.
        	 Needed only during source compilation. You don't need it during runtime. -->
        <dependency>
            <groupId>io.skullabs.trip</groupId>
            <artifactId>trip-processor</artifactId>
            <version>${version.trip}</version>
            <scope>provided</scope>
        </dependency>
        <!-- Undertow Module -->
        <dependency>
            <groupId>io.skullabs.undertow</groupId>
            <artifactId>undertow-hazelcast</artifactId>
            <version>${version.undertow}</version>
        </dependency>
    <dependencies>
```
