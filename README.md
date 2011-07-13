# ewarp: Erlang WAR packager

Package Erlang apps as [Java WAR](http://en.wikipedia.org/wiki/WAR_%28Sun_file_format%29) file with an embedded [Erjang](http://erjang.org) runtime. The WAR file can be run on any standard Java servlet container, such as [Tomcat](http://tomcat.apache.org/) or [Jetty](http://jetty.codehaus.org/jetty/). Deployment in the cloud (PaaS) is easy using a WAR file. Popular options are [Amazon Elastic Beanstalk](http://aws.amazon.com/elasticbeanstalk/) and [CloudFoundry](http://www.cloudfoundry.com/). The Erlang app does not necessarily need to be Web-aware, the servlet container simply acts as a convenient and standardized runtime container, which is provided by many (cloud) hosting providers. 

This project is currently work in progress.

## Planned feature set:

 * Single script call to create a WAR file from a simple Erlang app. Possibly integration with rebar
 * Many customization options
 * Automatic cluster management in cloud environments via customizable NodeFinders. Example: all nodes in a Elastic Beanstalk environment can be found via the Amazon EC2 and Beanstalk APIs and are automatically made known
 * Full access to the Java world and additional libraries/frameworks
 * Java-Erlang web bridge
 * Triggering of Erlang functions on startup/shutdown/specified web calls


## How to use

**Note**: as this is work in progress, there are not yet full instructions...

1. Get and build Erjang from GitHub:

		git clone https://github.com/trifork/erjang.git
		cd erjang
		ant jar otpjar

2. Get and build ewarp from GitHub:
Build using [Gradle](http://www.gradle.org/), which is downloaded automatically, when called as ./gradlew

		git clone https://github.com/jetztgradnet/ewarp.git
		cd ewarp
		./gradlew war

3. Upload the created .WAR-file (`build/libs/ewarp-0.1.war`) to your Servlet container (e.g. Tomcat or Jetty)
The Gradle-based build allows to run the WAR file inline using the following command:

		./gradlew jettyRun


