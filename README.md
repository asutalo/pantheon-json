# Pantheon JSON

The frontend component of the [Pantheon](https://github.com/asutalo/pantheon).

Built on top of the [Pantheon IOC Server](https://github.com/asutalo/pantheon-ioc-server) to provide access to generic
CRUD endpoints.

# Features
Generic `parameterised` and `parameterless` endpoints for working with your domain objects.

All responses from the generic endpoints will be formatted as JSON.

There are several annotations which can be used to manage how your domain objects are interacted with:

* @Location
  * Mandatory that there is *1* variable marked
  * Used to provide a URL to a newly saved object
* @Protected
  * Meant to mark a variable as requiring additional authorization to be fetched 
  * Incomplete - at the moment there's to way to authorize access to the variable
  * Once finished it should allow you to specify how the authorization is performed

The generic endpoints provide the following verbs:

* GenericParameterisedJsonEndpoint
  * GET
  * DELETE
  * PUT
* GenericParameterlessJsonEndpoint
  * GET
  * POST

# Restrictions
At the moment only simple variables (i.e. Java standard types) are supported.

# Usage

As the underlying server provider is the `ioc-server`, we need to create an instance of the _Server_ and register our
endpoints on it. However, in this case we would register the generic JSON endpoints instead of providing an instance of
the provided _Endpoint_ class.

*NOTE* To instantiate the generic endpoints, you will need to provide an implementation of *DataService*. This
DataService needs must be *annotated* on your data model class using _@ServedBy_ annotation provided
by [Pantheon](https://github.com/asutalo/pantheon). Additionally, the DataService also has to be registered in the _
ServiceProviderRegistry_, also provided by [Pantheon](https://github.com/asutalo/pantheon). An existing implementation
for interaction with _MySQL_ is available as part of the [pantheon-mysql](https://github.com/asutalo/pantheon-mysql).

````
class Sample {
    private static final String CARS_URI_PATH = "/cars";
    private static final String SPECIFIC_CAR_URI_PATH = CARS_URI_PATH + "/(id=\\d+)";
    
    public static void main(String[] args) throws IOException {
        DataClient mySqlClient = new DataClient...
        TypeLiteral<Car> carTypeLiteral = TypeLiteral.get(Car.class);
        MySQLService<Car> mySQLService = new MySQLService(mySqlClient, carTypeLiteral)
        
        ServiceProviderRegistry.INSTANCE().register(mySQLService);
        
        Server server = new Server(8080, 150, 50, 10, TimeUnit.SECONDS, true);
    
        server.registerEndpoint(new GenericParameterisedJsonEndpoint<>(SPECIFIC_CAR_URI_PATH, carTypeLiteral));
        server.registerEndpoint(new GenericParameterlessJsonEndpoint<>(CARS_URI_PATH, CARS_URI_PATH, carTypeLiteral));
        
        server.start();
    }

    @ServedBy(dataService = MySQLService.class)
    static class Car {
        @Location
        private Integer id;
        
        private String make;
        
        @Protected
        private String licensePlate;
    }
}
````

When the main method is executed navigate to `http://localhost:8080/cars` to interact with the *parameterless* endpoint,
or to `http://localhost:8080/cars/1` to interact with the *parameterised* endpoint.

# Coming Soon...
* Tweaking requests
  * i.e. limit, sort, filtering
* Nesting
  * Support for custom classes as variables within the domain objects

# Importing

The library is available via [![](https://jitpack.io/v/asutalo/pantheon-json.svg)](https://jitpack.io/#asutalo/pantheon-json)
