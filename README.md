# karaf.manager
Karaf Manager application interface using jolokia api to run various operations on karaf

### How to import project for editing ###

* Import as maven project in your IDE

### Pre-requisite to run the application server

Install apache-karaf-4.2.2

Run following commands to install jolokia plugin into karaf
Help url: https://jolokia.org/reference/html/protocol.html#request-response

	apache-karaf-4.2.2\bin\start
	# Make sure you have this plugin downloaded into your local repo
	karaf@root()> bundle:install -s mvn:org.jolokia/jolokia-osgi/1.6.0

Now restart the karaf application by running 

	apache-karaf-4.2.2\bin\start

Configure following properties

	server.port=8191

### Build, install and run application ###

To get started build the build the latest sources with Maven 3 and Java 8 
(or higher). 

	$ cd karaf.manager
	$ mvn clean install 

You can run this application as spring-boot app by following command:

	$ mvn spring-boot:run

Once done you can run the application by executing 

	$ java -jar target/karaf.manager-x.x.x-SNAPSHOT-exec.jar

## API endpoints description

### /karaf/read

Create a READ request to request one or more attributes from the remote j4p agent

	Method: POST, GET
	Params:
		mbean  String object name as sting which gets converted to a javax.management.ObjectName i.e. java.lang:type=Memory
		attribs String[] zero, one or more attributes to request i.e. HeapMemoryUsage
	Response:
		Json		Success result or error

### /karaf/write

API to create write request

	Method: POST, GET
	Params:
		mbean  MBean name which attribute should be set i.e. java.lang:type=ClassLoading
		attrib name of the attribute to set i.e. Verbose
		value new value i.e. true
		path optional path for setting an inner value
	Response:
		Json		Success result or error

### /karaf/execute

Api to create client request for executing a JMX operation

	Method: POST, GET
	Params:
		mbean name of the MBean to execute the request on i.e. org.apache.karaf:type=bundle,name=root
		operation operation to execute i.e. install(java.lang.String)
		args any arguments to pass (which must match the JMX operation's declared signature) i.e. mvn:com.synectiks.osgi/bundle/1.0.0-SNAPSHOT
	Response:
		Json		Success result or error

### /karaf/search

Searches the mbeans with name regex

	Method: POST, GET
	Params:
		mbeanRegex  MBean names pattern as string i.e. java.lang:*
	Response:
		Json		Success result or error

### /karaf/list

List all mbeans using a path to restrict the information returned by the list command

	Method: POST, GET
	Params:
		path path into the JSON response. The path must already be properly escaped when it contains slashes or exclamation marks. You can use escape(String) in order to escape a single path element
	Response:
		Json		Success result or error

### /karaf/version

Create and run a version request

	Method: POST, GET
	Params:
	Response:
		String		application version 
