## uRouting ( micro Routing ) Extension
Provide an easy to use routing API ( JAXRS like ).

### What it provides to you?
- Annotation driven routing API
- A routing API optimized in compile time, by having no reflections for routing it makes undertow a lightweight execution environment
- Extensible (un)serialization mechanism ( to convert data (in)to JSON, XML, etc )
- Orthogonal exception handler

### Instaltion instructions
Just include the bellow maven dependencies to your ```pom.xml```. Note that Undertow Extensions use [tRip](https://github.com/Skullabs/tRip) as an engine to provide implementations and allow our internal modularization.

```xml
...
    <properties>
        <version.trip>0.14.1</version.trip>
        <version.undertow>1.1-SNAPSHOT</version.undertow>
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
            <artifactId>undertow-urouting</artifactId>
            <version>${version.undertow}</version>
        </dependency>
    <dependencies>
```