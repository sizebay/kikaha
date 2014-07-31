## Standalone Extension
Automations on Undertow bootstrap routines to make easier to embed it on your application.

### What it provides to you?
- Automate routing registering, avoiding manual ( and repetitive ) definitions of HttpHandle routes
- Provide deployment hooks to make developers able to be notified when Undertow is starting or shutting down
- Provide a request hook mechanism that, similarly to Servlet Filters, allow developers to intercept and change a request

### Motivation
Undertow is an amazing web development plataform. It is the current Wildfly
core engine, it has a simple and powerful internal design, and is [one of most fastest web implementations ever](http://www.techempower.com/benchmarks/#section=data-r8&hw=i7&test=plaintext).
Nevertheless, it has no standard way to struture and embed your project. Every time you intent to use Undertow embedded in your project you should create a bunch of code lines to bootstrap your web application.

### Creating routes
Here is the default hello world example provided by Undertow documentation.

```java
public class HelloWorldServer {

    public static void main(final String[] args) {
        Undertow server = Undertow.builder()
                .addHttpListener(8080, "localhost")
                .setHandler(new HttpHandler() {
                    @Override
                    public void handleRequest(final HttpServerExchange exchange) throws Exception {
                        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
                        exchange.getResponseSender().send("Hello World");
                    }
                }).build();
        server.start();
    }
}
```

Note that UndertowBuilder expects a root HttpHandler to handle all requests your applications will be able to handle. When more than one route is needed to be deployed a inattentive developer may turn its powerful application code into a mess spaghetti code.

The Standalone extension aim to save you from the pain of creating its countless lines of code
to create your routes. All the effort needed to create a route on your application is:
- implement the ```io.undertow.server.HttpHandler``` interface
- inform which URL and Http Method it is expected to handle through ```io.skullabs.undertow.standalone.api.WebResource``` annotation
- annotated it as a singleton service through ```trip.api.Service```

```java
@Service
@WebResource( value="/", method="GET")
public class HelloWorldHandler implements HttpHandler {

    @Override
    public void handleRequest( HttpServerExchange exchange ) throws Exception {
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
        exchange.getResponseSender().send( "Hello World" );
    }

}
```

### Starting up an application
There is an special main class ( ```io.skullabs.undertow.standalone.Main``` ) on Undertow Standalone module that helps you to start you embedded application. You could point it out no your MANIFEST.MF to create a runnable jar, or pass it in the command line to start your application manually.

```console
$ java -cp ${YOUR_CUSTOM_CLASSPATH} io.skullabs.undertow.standalone.Main
```

If you already has your own main class, you should also consider to start Undertow itself as shown bellow:
```java

    public void startUndertow(){
        String[] args = new String[]{};
        io.skullabs.undertow.standalone.Main.main( args );
    }

```

As occurs with Undertow core, this modulo is totally OpenSource. If any of above approaches doesn't fit your need, you may consider to extends ```io.skullabs.undertow.standalone.UndertowServer``` class and improve it by your self.

### Listening to deployments
It's possible to listen deployments events like ```onDeploy``` and ```onUndeploy```.

```java
@Service
public class TraceDeploymentHook implements DeploymentHook {

	// ...

	@Override
	public void onDeploy( DeploymentContext context ) {
		log.info( "On deploy" );
	}

	@Override
	public void onUndeploy( DeploymentContext context ) {
		log.info( "On undeploy" );
	}
}

```

The ```io.skullabs.undertow.standalone.api.DeploymentContext``` class contains a set of methods that helps you to interact with ```io.skullabs.undertow.standalone.UndertowServer``` before and after the service is running. Lets take a look on its main functionalities.

#### DeploymentContext#register
It registers a route on Undertow, where:
- __uri__: defines which uri this route should be listening to.
- __method__: the Http Method this route should be listening to. Default: GET.
- __handler__: the ```io.undertow.server.HttpHandler``` implementation of route.
```java
	DeploymentContext register( String uri, HttpHandler handler );
	DeploymentContext register( String uri, String method, HttpHandler handler );
```

#### DeploymentContext#register
It registers a RequestHook implementation that will be executed after the routing matching.
```java
	DeploymentContext register( RequestHook hook );
```

#### DeploymentContext#fallbackHandler
Register a Fallback HttpHandler. It is used when no routing was matched in a request. By default, it is an instance of ```io.undertow.server.handlers.resource.FileResourceManager```. This way, when no routing is matched it tries to find a resource on File System.
```java
	DeploymentContext fallbackHandler( HttpHandler fallbackHandler );
```

#### DeploymentContext#attribute
DeploymentContext are able to hold attributes. This attributes are useful to provide data to ```RequestHooks```.
```java
	<T> DeploymentContext attribute( Class<T> clazz, T object );
	DeploymentContext attribute( String key, Object object );
	<T> T attribute( Class<T> clazz );
	Object attribute( String key );
```

### Intercepting requests
Undertow internally has a simple chain of responsibility where it store its routing design and provide an [easy to use DSL](http://undertow.io/documentation/core/built-in-handlers.html). Even so, intercepting requests in a default way isn't an easy task to do. Its HttpHandle implementations used to expect you manually set which is the next handler it should call when a subsequent action must be taken.

Undertow Standalone Extension provide an easy RequestHook API which can be used to intercept requests, change
the HttpServerExchange state or even interrupt the request.

```java
@Service
public class TraceRequestHook implements RequestHook {

    // ...

    @Override
    public void execute( RequestHookChain chain, HttpServerExchange exchange ) {
        // Tracing the request
        String message = exchange.getRequestMethod().toString() + ":" + exchange.getRequestPath();
        log.info( message );

        // Allowing the next chain of hooks to do its job.
        // If no other RequestHook are available, it will execute the Default Handler
        chain.executeNext();
	}
}

```

Every request hook is disposed in a chain of responsibility. If a ```RequestHook``` does not call ```chain.executeNext()```
it will interrupt the request lifecycle. It means that the hook is able to do what it want with the request. RequestHook's are also useful to plug external frameworks like [RestEasy](http://www.jboss.org/resteasy) or a custom template engine like [Mustache](http://mustache.github.io/).

### Instaltion instructions
Just include the bellow maven dependencies to your ```pom.xml```. Note that Undertow Extensions use [tRip](https://github.com/Skullabs/tRip) as an engine to provide implementations and allow our internal modularization.

```xml
...
    <properties>
        <version.trip>1.0-SNAPSHOT/version.trip>
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
            <artifactId>undertow-standalone</artifactId>
            <version>${version.undertow}</version>
        </dependency>
    <dependencies>
```
