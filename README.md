# Pantheon JSON

The frontend component of the Pantheon.

Built on top of the [Pantheon IOC Server](https://github.com/asutalo/pantheon-ioc-server) to provide access to generic
CRUD endpoints.

#Features
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

#Restrictions
At the moment only simple variables (i.e. Java standard types) are supported.

#Usage
As the underlying server provider is the `ioc-server`, we need to create an instance of the _Server_ and
register our endpoints on it. However, in this case we would register the generic JSON endpoints instead 
of providing an instance of the provided _Endpoint_ class.

*NOTE* To instantiate the generic endpoints, you will need to provide an implementation of *DataService*. 
An existing implementation for interaction with _MySQL_ is available as part of the [sql-wrapper](https://github.com/asutalo/sql-wrapper).

````
class Sample {
    private static final String CARS_URI_PATH = "/cars";
    private static final String SPECIFIC_CAR_URI_PATH = CARS_URI_PATH + "/(id=\\d+)";
    
    public static void main(String[] args) throws IOException {
        SomeDataServiceImpl<Car> someDataServiceImpl = //you need to provide an implementation of it, this will be used to fetch data from the database
        EndpointFieldsProvider<Car> endpointsFieldProvider = new EndpointFieldsProvider(TypeLiteral.get(Car.class)); //temp, will get replaced with annotations
        
        Server server = new Server(8080, 150, 50, 10, TimeUnit.SECONDS, true);
    
        server.registerEndpoint(new GenericParameterisedJsonEndpoint<>(SPECIFIC_CAR_URI_PATH, someDataServiceImpl, endpointFieldsProvider));
        server.registerEndpoint(new GenericParameterlessJsonEndpoint<>(CARS_URI_PATH, someDataServiceImpl, CARS_URI_PATH, endpointFieldsProvider));
        
        server.start();
    }

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

#Coming Soon...
* Tweaking requests
  * i.e. limit, sort, filtering
* Nesting
  * Support for custom classes as variables within the domain objects

## Importing

The library is available via jitpack:
[![](https://jitpack.io/v/asutalo/pantheon-json.svg)](https://jitpack.io/#asutalo/pantheon-json)