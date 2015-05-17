# Alchemy rest client generator demo

A demo jersey RESTful Web Services project with a simple authentication and ping service that demonstrates

  - Use of [Alchemy Rest Client Generator] to auto-generate a java client 
  - Use of [Alchemy Rest Client Generator] to unit test the werbservices
  - Basic http authentiation from the generated client
  - Transparent exception marshing and unmarshalling at the client
  - Use of [Alchemy Inject] to load Guice modules for testing and production

The code is organized into three modules
 
  - common - Contains transfer objects, json mapper initialization and exception marshlling an demarshalling code.
  - client - mostly auto generated client. In addition there is some code to configure the client and guice bindings for auto-generated code.  
  - root - the jersey webservices with glue work to integrate guice dependency injection with jersey 

## Contributing

Please refer to [Contribution Guidlines][Contrib] if you are not familiar with contributing to open source projects. 

The gist for making a contibution is

1. [Fork]
2. Create a topic branch - `git checkout -b <your branch>`
3. Make your changes 
4. Push to your branch - `git push origin <your branch>`
5. Create an [Issue] with a link to your branch

### Setting up eclipse
Run
```
gradle eclipse
```

Import alchemy inject to eclipse using File > Import > Existing Projects into Workspace

The project has been setup to auto format the code via eclipse save actions. Please try not to disturb this.

## Copyright and license

Code and documentation copyright 2015 [Strand Life Sciences]. Code released under the [Apache License 2.0]. Docs released under Creative Commons.

[Alchemy Rest Client Generator]:https://github.com/strandls/alchemy-rest-client-generator/
[Alchemy Inject]:https://github.com/strandls/alchemy-inject/
[Apache License 2.0]:http://www.apache.org/licenses/LICENSE-2.0.html  
[Strand Life Sciences]:http://www.strandls.com/
[Fork]: http://help.github.com/forking/
[Issues]: https://github.com/strandls/alchemy-rest-client-demo/issues
[Contrib]: https://guides.github.com/activities/contributing-to-open-source/
