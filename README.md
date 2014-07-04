# Undertow Standalone Extensions

Undertow Standalone Extensions was developed with two core ideas in mind:
- provide a set of extensions to structure and initialize your application without writing a bunch of bootstraping lines of code
- provides a lightweight micro container (written over the powerful undertow core), as alternative environment to to run web applications.

## Who is it for?
Undertow Standalone Extensions is for you if one of the bellow statements are true for your needs:
- A full featured Web Container (like JBoss, Wildfly, GlassFish, etc) is considered overwhelming for my software needs
- Servlet API is a boring development environment
- My SaaS application is changing to a micro-services architecture
- I want to create a software adopting the micro-services architecture principle
- I want to do something impressively fast on the web
- I want to have some fun developing a web software in JVM environment

## Which extensions are available on current version

### Standalone
Automations on Undertow Bootstrap to make easier to embed it on your application. It:
- Automate routing registering, avoiding manual ( and repetitive ) definitions of HttpHandle routes
- Provide deployment hooks to make developers able to be notified when Undertow is starting or shutting down
- Provide a request hook mechanism that, similarly to Servlet Filters, allow developers to intercept and change a the HttpServerExchange

See this module [documentation](https://github.com/Skullabs/undertow-standalone/tree/master/undertow-standalone) for more information.

### uRouting ( micro Routing )
Provide an easy to use routing API ( JAXRS like ). It provides:
- Annotation driven routing API
- A routing API optimized in compile time, by having no reflections for routing it makes undertow a lightweight execution environment
- Extensible (un)serialization mechanism ( to convert data (in)to JSON, XML, etc )
- Orthogonal exception handler
- and many more...

See this module [documentation](https://github.com/Skullabs/undertow-standalone/tree/master/undertow-urouting) for more information.

### Hazelcast integration
Provide tight integration with the leading in-memory data-grid implementation ( [Hazelcast](http://hazelcast.org/) ). This module provides:
- Easy-to-use Hazelcast Instance configuration ( as a Cluster Node )
- Easy-to-use Hazelcast Instance configuration ( as a Client for Cluster Nodes )
- Full integrated with all _distributed data structures_ provided by Hazelcast
- Easier configuration environment with easy fallback to XML Based Configuration
- Easier configuration environment with easy programmatic configuration ( for ClientConfig and Config )
- and many more...

See this module [documentation](https://github.com/Skullabs/undertow-standalone/tree/master/undertow-hazelcast) for more information.

## Useful resources to getting started
``` TODO ```

## Useful troubleshooting resources
``` TODO ```

## Contributors
[Ricardo Mattiazzi Baumgarter](https://github.com/ladraum)
[Miere Liniel Teixeira](https://github.com/miere)

Be a contributor and join our team. Undertow Standalone need your help to provide the best to the community. Even simple tasks like testing the micro container, finding typos in docs or reporting improvements feedbacks will be welcome.

## Community / Support
* [GitHub Issues](https://github.com/Skullabs/undertow-standalone/issues)
* Google Group: yet not created

### License
Undertow Standalone Extensions is [Apache 2.0 licensed](http://www.apache.org/licenses/LICENSE-2.0.html).
