**Overview**

This is a sample gRPC Java based implementation of a HelloWorld program. 
This is implemented basically to demonstrate the WSO2 Microgateway's gRPC API 
Concept. 

gRPC client is implemented in such a way that it accepts an api key token. 
Hence "api_key" header is added to the RPC call. 

gRPC server do not perform any authentication.

**How to execute**
- Install Java and Maven and update the PATH variables accordingly.

- Build the project using Maven.

`mvn clean install`

- To run the gRPC server,

`java -jar serverImpl/target/serverImpl-1.0-SNAPSHOT.jar <port>`

- To run the gRPC client,

`java -jar clientImpl/target/clientImpl-1.0-SNAPSHOT.jar <input-text> <api_key_token> <port>`

