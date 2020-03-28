package org.virajsalaka.grpc_server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import org.virajsalaka.grpc.HelloWorldGrpc;
import org.virajsalaka.grpc.Helloworld;

import java.io.IOException;

/*
 * This class contains the gRPC client implementation for the basic passthrough scenario.
 */
public class GrpcService {
    private Server server;
    int port = 50051;

    public void start(int port) throws IOException {
        if (server == null || server.isShutdown() || server.isTerminated()) {
            server = ServerBuilder.forPort(port)
                    .addService(new GrpcHelloServiceImpl())
                    .build().start();
        }
        System.out.println("Server started, listening on " + port);
    }

    public static void main(String[] args) throws IOException {
        int port ;
        if (args.length == 1)  {
            String portVal = args[0];
            port = Integer.parseInt(portVal);
        } else {
            System.out.println("If you need to change the default port, provide that as a commandline argument");
            port = 50051;
        }
        new GrpcService().start(port);

        while(true) {

        }
    }
}
class GrpcHelloServiceImpl extends HelloWorldGrpc.HelloWorldImplBase{
    @Override
    public void hello(Helloworld.HelloRequest request, StreamObserver<Helloworld.HelloReply> responseObserver) {
        String input = request.getInput();
        String output = "Hello " + input;
        Helloworld.HelloReply reply = Helloworld.HelloReply.newBuilder().setOutput(output).build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }
}