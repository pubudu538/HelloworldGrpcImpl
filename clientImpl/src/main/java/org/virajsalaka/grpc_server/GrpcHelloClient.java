package org.virajsalaka.grpc_server;

import io.grpc.*;
import io.grpc.stub.MetadataUtils;
import org.virajsalaka.grpc.HelloWorldGrpc;
import org.virajsalaka.grpc.Helloworld;

import java.util.concurrent.TimeUnit;

/*
 * This class contains the gRPC client implementation for the basic passthrough scenario.
 */
public class GrpcHelloClient {

    private final HelloWorldGrpc.HelloWorldBlockingStub blockingStub;

    public GrpcHelloClient(Channel channel, String token) {
        HelloWorldGrpc.HelloWorldBlockingStub stub = HelloWorldGrpc.newBlockingStub(channel);
        Metadata metadata = new Metadata();
        metadata.put(Metadata.Key.of("api_key", Metadata.ASCII_STRING_MARSHALLER), token);
        blockingStub = MetadataUtils.attachHeaders(stub,metadata);
    }

    public String testCall(String requestText) {
        Helloworld.HelloRequest request = Helloworld.HelloRequest.newBuilder().setInput(requestText).build();
        Helloworld.HelloReply response;
        try{
            response = blockingStub.hello(request);
        } catch (StatusRuntimeException e) {
            System.out.println("RPC failed: {0} : " + e.getStatus());
            response = Helloworld.HelloReply.newBuilder().setOutput(e.getStatus().getDescription()).build();
        }
        if (response == null) {
            return null;
        }
        return response.getOutput();
    }

    public static void main(String[] args) throws InterruptedException {
        if(args.length == 0 || args.length > 3) {
            System.out.println("Please provide the required arguments");
            System.out.println("java -jar <jar-path> <text> <api_key_token> <port>");
            System.exit(0);
        }
        String input = args[0];
        String apiKeyToken = args[1];

        String targetUrl = "localhost:";
        if (args.length == 3) {
            targetUrl = targetUrl + args[2];
        } else {
            targetUrl = targetUrl + "9090";
        }
        System.out.println("Connecting to : " + targetUrl);

        ManagedChannel channel = ManagedChannelBuilder.forTarget(targetUrl).usePlaintext().build();
        try {
            GrpcHelloClient client = new GrpcHelloClient(channel, apiKeyToken);
            String output = client.testCall(input);
            if (output != null) {
                System.out.println("Output : :" + output);
            } else {
                System.out.println("No output is received by the client.");
            }
        } finally {
            channel.shutdownNow().awaitTermination(10, TimeUnit.SECONDS);
        }
    }
}