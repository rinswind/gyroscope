Gyroscope is a service layer runtime (SLR) for OSGi that aims to hide most of the dynamics of the platform providing a _stabilized_ programming environment.

# Goals #

### Constructor friendly ###
Allow the user to pass OSGi services to constructors and store them in final fields. This will in turn allow the core structure of the bundle to be composed only one time rather than be partially created and destroyed as service objects come and go. This makes it possible to combine Gyroscope with Guice or PicoContainer to further propagate the service objects throughout the bundle.

### Comprehensive ###
Make it possible for the user to never use directly the OSGi API for service tracking, service registration, service/bundle/framework event reactions. Additionally allow the seamless use the Config Admin for dynamic configuration.

### Simple ###
Try not to force a particular component or other programming model onto the user. The goal is to allow the user to program in OSGi as close to Plain Old Java as possible.

### No configuration files ###
Everything must be pure Java.

### Declarative ###
Just configure using an EDSL. Gyroscope will bootstrap your bundle and provide lifecycle callbacks for the events from the outer dynamic environment.

### Fast where it matters ###
First and foremost be safe, consistent, correct. High performance is an equally important concern only regarding the method calls to the OSGi service objects. The reactions to the events of the OSGi environment do not need to be blisteringly fast as these are rare - no one installs/uninstalls a bundle 100 times a second.

# Status #
The status is _alpha_.

The best developed part is the core piece that deals with dynamic proxies and service object interceptions & transformations. This part is usable today and can even be deployed as a stand-alone bundle that exports a ProxyFactory service to be used by all. The plans for this proxy engine is to make it a little less rigid in order to allow the implementation of optional service imports. The proxies provide only the backing-instance-hotswap functionality, and are tad slower than a synchronized method call.

The part that implements the OSGi service tracking and contains the Gyro EDSL is also usable but is less developed. There is lots to do to make things more declarative. There is lots to do in the area of permitting OSGi event reactions into objects other than the initial singleton set. The goal here is to make this into a full-blown [extender bundle](http://www.osgi.org/blog/2007/02/osgi-extender-model.html).

So far Gyro has been successfully combined with Guice. The combo can be made much more smoother by designing a specialized Gyro-Guice EDSL.

There are plans to see how Gyro combines with PicoContainer as well.