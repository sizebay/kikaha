## Undertow Maven Plugin
Provide an easy to use development environment to run Undertow applications and their extensions.

### Instaltion instructions
Just include the bellow maven dependencies to your ```pom.xml```. Note that Undertow Extensions use [tRip](https://github.com/Skullabs/tRip) as an engine to provide implementations and allow our internal modularization.

```xml
...
    <properties>
        <version.trip>0.14.1</version.trip>
        <version.undertow>1.1-SNAPSHOT</version.undertow>
    </properties>
...
    <build>
        <plugins>
            <plugin>
                <groupId>io.skullabs.undertow</groupId>
                <artifactId>undertow-maven-plugin</artifactId>
                <version>${version.undertow}</version>
            </plugin>
        </plugins>
    </build>
```

Now you are able to change to the current work directory of your project and run the Undertow Plugin goals.
```console
$ cd my-simple-project
$ mvn clean install undertow:run
jul 03, 2014 6:06:05 PM io.skullabs.undertow.standalone.HttpHandlerDeploymentHook onDeploy
INFORMAÃıES: Looking for HttpHandler routes...
jul 03, 2014 6:06:05 PM io.skullabs.undertow.standalone.DefaultDeploymentContext register
INFORMAÃıES: Registering route: GET:/users/{id}/.
jul 03, 2014 6:06:05 PM io.skullabs.undertow.standalone.DefaultDeploymentContext register
INFORMAÃıES: Registering route: POST:/users/{id}/.
jul 03, 2014 6:06:05 PM io.skullabs.undertow.standalone.UndertowServer createResourceManager
INFORMAÃıES: Exposing resource files at .\webapp
jul 03, 2014 6:06:05 PM org.xnio.Xnio <clinit>
INFO: XNIO version 3.3.0.Beta1
jul 03, 2014 6:06:05 PM org.xnio.nio.NioXnio <clinit>
INFO: XNIO NIO Implementation Version 3.3.0.Beta1
jul 03, 2014 6:06:05 PM io.skullabs.undertow.standalone.UndertowServer start
INFORMAÃıES: Server started in 220ms.
jul 03, 2014 6:06:05 PM io.skullabs.undertow.standalone.UndertowServer start
INFORMAÃıES: Server is listening at 0.0.0.0:9000
```