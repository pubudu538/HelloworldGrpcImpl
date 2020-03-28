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

    public void start() throws IOException {
        /* The port on which the server should run */
        int port = 50051;
        if (server == null || server.isShutdown() || server.isTerminated()) {
            server = ServerBuilder.forPort(port)
                    .addService(new GrpcHelloServiceImpl())
                    .build().start();
        }
        System.out.println("Server started, listening on " + port);
    }

    public static void main(String[] args) throws IOException {
        new GrpcService().start();

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