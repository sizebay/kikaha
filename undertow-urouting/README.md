## uRouting ( micro Routing ) Extension
Provide an easy to use routing API ( JAXRS like ).

### What it provides to you?
- Annotation driven routing API
- A routing API optimized in compile time, by having no reflections for routing it makes undertow a lightweight execution environment
- Extensible (un)serialization mechanism ( to convert data from/to JSON, XML, etc )
- Orthogonal exception handler

### Creating your first route
Consider that you need to create an REST Web Service API to create and retrieve users. Also, lets consider that we will persist it in a ```Map``` to simplify our example. To achieve this we should create the following two java classes:

```java
public class User {

	final Long id = System.currentTimeMillis();
	String name;

	public String getName(){ return name; }
	public void setName( String name ){
		this.name = name;
	}
}

@Path( "users" )
@Produces( Mimes.JSON )
@Service
public class UserResource {

	final Map<Long, User> users = new HashMap<Long, User>();

	@GET
	@CPU
	@Path( "{id}" )
	public User retrieveUserById(
			@QueryParam( "id" ) Long id ) {
		return users.get( id );
	}

	@POST
	@Path( "{id}" )
	@Consumes( Mimes.JSON )
	public void persistUser( User user ) {
		users.put( user.getId(), user );
	}
}
```
That's it!!! Now run your Undertow application with the [Undertow Maven Plugin](https://github.com/Skullabs/undertow-standalone/blob/master/undertow-maven-plugin/README.md) and see your application running. Basically, you have a route that expects a request ( via GET ) at "/users/{id}/" every time you want to retrieve a JSON representation of a user. Also, you could send a JSON representation of a new user ( via POST ) to "/users/{id}/".


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