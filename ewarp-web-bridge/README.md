# ewarp web bridge: Bridges HTTP requests into the Erlang code


## Web bridge

 * Uses [simple_bridge](https://github.com/nitrogen/simple_bridge) and ```servlet_bridge``` companion project on the inside
 * Servlet filters and routes incoming requests
 * Any web framework supporting simple_bridge can be used (or none at all for simple projects)

## Configuration

**TODO** web.xml fragment, configuration options

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


