# ewarp: Erlang WAR packager

Package Erlang apps as [Java WAR](http://en.wikipedia.org/wiki/WAR_%28Sun_file_format%29) file with an embedded [Erjang](http://erjang.org) runtime. The WAR file can be run on any standard Java servlet container, such as [Tomcat](http://tomcat.apache.org/) or [Jetty](http://jetty.codehaus.org/jetty/). Deployment in the cloud (PaaS) is easy using a WAR file. Popular options are [Amazon Elastic Beanstalk](http://aws.amazon.com/elasticbeanstalk/) and [CloudFoundry](http://www.cloudfoundry.com/). The Erlang app does not necessarily need to be Web-aware, the servlet container simply acts as a convenient and standardized runtime container, which is provided by many (cloud) hosting providers. 

This project is currently work in progress.

## Planned feature set:

 * Single script call to create a WAR file from a simple Erlang app. Possibly integration with [rebar](https://github.com/basho/rebar)
 * Many customization options
 * Automatic cluster management in cloud environments via customizable NodeFinders. Example: all nodes in a Elastic Beanstalk environment can be found via the Amazon EC2 and Beanstalk APIs and are automatically made known
 * Full access to the Java world and additional libraries/frameworks
 * Java-Erlang web bridge, possibly using [SimpleBridge](https://github.com/nitrogen/simple_bridge) or [EWGI](https://github.com/skarab/ewgi)
 * Triggering of Erlang functions on startup/shutdown/specified web calls
 * running Erlang apps in an OSGi container

## code structure

* ewarp-core

	Core module containing the runtime handling, node discovery, ...
* ewarp-web

	Web runtime adapter to start Erjang in a Java Servlet container, e.g. Tomcat
* ewarp-example-web

	Sample application
* ewarp-web-bridge (planned)

	Bridges HTTP requests into the Erlang code
* ewarp-aws (planned)

	Node discovery and advertising in a Amazon EC2/Elastic Beanstalk environment
* ewarp-osgi (planned)

	Runtime adapter to start Erjang in an OSGi environment, e.g. Eclipse Equinox or Apache Felix


## How to build

**Note**: as this is work in progress, there are not yet full instructions...

1. Get and build Erjang from GitHub:

		# get source code
		git clone https://github.com/trifork/erjang.git
		cd erjang
		# modify erjang_cfg.properties to point to your Erlang installation,
		vi erjang_cfg.properties
		# then build Erjang and a jar containing the Erlang runtime
		ant jar otpjar

This creates `erjang-0.1.jar` as well as `otp-<OTPVERSION>.jar`

2. Get and build ewarp and the sample application from GitHub:
Build using [Gradle](http://www.gradle.org/), which is downloaded automatically, when called as ./gradlew

		# get source code
		git clone https://github.com/jetztgradnet/ewarp.git
		cd ewarp
		# modify gradle.properties to specify Erjang and OTP versions
		vi gradle.properties
		# build ewarp and the sample project
		./gradlew

This produces both the ewarp jar files as well as 

3. Upload the created .WAR-file (`ewarp-example-web/build/libs/ewarp-example-web-0.1.war`) to your Servlet container (e.g. Tomcat or Jetty)
The Gradle-based build allows to run the WAR file inline using the following command:

		./gradlew jettyRun


## How to use

See project [ewarp-example-web](https://github.com/jetztgradnet/ewarp/tree/master/ewarp-example-web) for an example application. It can be build from the ewarp-example-web directory using `./gradlew war`.

Basically, create a web application, with `erjang-0.1.jar`, `ewarp-core-0.1.jar`, `ewarp-web-0.1.jar`, and a jar file with your prefered OTP version, e.g. `otp-R13B04.jar` contained in `WEB-INF/lib`.

In order to create and start the Erjang runtime, `erjangx.ewarp.web.ErjangContextListener` should be registered as ServletContextListener. Additionally, the servlet erjangx.ewarp.web.stats.ErjangStatusServlet may be registered to provide some runtime information.

Example of a `WEB-INF/web.xml`:

		<?xml version="1.0" encoding="UTF-8"?>
		<web-app xmlns="http://java.sun.com/xml/ns/javaee"
			xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
			xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd"
			version="2.5">
			<display-name>Erlang Web Application</display-name>
			<listener>
				<listener-class>erjangx.ewarp.web.ErjangContextListener</listener-class>
			</listener>
			<servlet>
				<servlet-name>status</servlet-name>
				<servlet-class>erjangx.ewarp.web.stats.ErjangStatusServlet</servlet-class>
			</servlet>
			<servlet-mapping>
				<servlet-name>status</servlet-name>
				<url-pattern>/status/*</url-pattern>
			</servlet-mapping>
	
			<servlet-mapping>
				<servlet-name>status</servlet-name>
				<url-pattern>/status</url-pattern>
			</servlet-mapping>
		</web-app>

## How to include your Erlang app

Create a release, e.g. using [rebar](https://github.com/basho/rebar). Then package this release into a Jar file with a ant build script similar to the [one from Erjang](https://github.com/trifork/erjang/blob/master/build.xml):

		<project name="erjang" default="all">
			<property file="erjang.properties" />
			<target name="otpjar">
				<jar jarfile="otp-${erjang.otp.version}.jar" basedir="${erjang.otp.root}">
					<exclude name="**/*.so" />
					<exclude name="**/*.dll" />
					<exclude name="**/*.a" />
					<exclude name="**/*.erl" />
					<exclude name="**/bin/beam" />
					<exclude name="**/bin/beam.smp" />
					<exclude name="lib/megaco-*/**" />
					<exclude name="lib/wx-*/**" />
					<exclude name="lib/hipe-*/**" />
					<exclude name="lib/jinterface-*/**" />
					<exclude name="lib/erl_interface-*/**" />
					<exclude name="lib/ic-*/java_src/**" />
					<exclude name="usr/include/**" />
					<exclude name="**/examples/**" />
				</jar>
			</target>
		</project>

Create a file `erjang.properties` to specify OTP version and root dir, which is the one containing your release:

		erjang.otp.version=R13B04
		erjang.otp.root=rel/myapp

Create the application jar file using `ant otpjar` and place the created jar file in your app's `WEB-INF/lib` directory.

## How to use in Eclipse

When developing using Eclipse and WTP, the best way is to let Gradle generate Eclipse projects for the projects:

		cd ewarp
		gradle eclipse

These can now be imported into Eclipse using `File -> Import...`.

Create a Web Project using `File -> New...`, choose `Dynamic Web Project`. Add the ewarp projects as (web) dependencies to your project. Now you can create a Server (e.g. with Tomcat 6.0) and run and debug your project.

