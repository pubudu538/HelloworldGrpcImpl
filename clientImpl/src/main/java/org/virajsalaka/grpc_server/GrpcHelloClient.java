package org.virajsalaka.grpc_server;

import io.grpc.*;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContext;
import io.grpc.stub.MetadataUtils;
import org.virajsalaka.grpc.HelloWorldGrpc;
import org.virajsalaka.grpc.Helloworld;
import java.io.File;

import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLException;

/*
 * This class contains the gRPC client implementation for the basic passthrough scenario.
 */
public class GrpcHelloClient {

    private final HelloWorldGrpc.HelloWorldBlockingStub blockingStub;

    public GrpcHelloClient(Channel channel, String token) {
        HelloWorldGrpc.HelloWorldBlockingStub stub = HelloWorldGrpc.newBlockingStub(channel);
        Metadata metadata = new Metadata();
        metadata.put(Metadata.Key.of("api_key", Metadata.ASCII_STRING_MARSHALLER), token);
        blockingStub = MetadataUtils.attachHeaders(stub, metadata);
    }

    public String testCall(String requestText) {
        Helloworld.HelloRequest request = Helloworld.HelloRequest.newBuilder().setInput(requestText).build();
        Helloworld.HelloReply response;
        try {
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

    // todo: add target host
    public static void main(String[] args) throws InterruptedException, SSLException {
        if (args.length == 0 || args.length > 4) {
            System.out.println("Please provide the required arguments");
            System.out.println("** Use the following command for HTTP server");
            System.out.println("java -jar <jar-path> <text> <api_key_token> <port>");
            System.out.println("** Use the following command for HTTPS server");
            System.out.println("java -jar <jar-path> <text> <api_key_token> <port> <cert_file_path>");
            System.exit(0);
        }
        String input = args[0];

        String apiKeyToken = args[1];

        String targetUrl = "localhost:" + (args.length >= 3 ? args[2] : "9090");
        String certPath = args.length == 4 ? args[3] : "";

        System.out.println("Connecting to : " + targetUrl);

        ManagedChannel channel;
        if (certPath != "") {
            File certFile = new File(certPath);
            SslContext sslContext = GrpcSslContexts.forClient().trustManager(certFile).build();
            channel = NettyChannelBuilder.forTarget(targetUrl)
                    .sslContext(sslContext)
                    .build();
        } else {
          channel = ManagedChannelBuilder.forTarget(targetUrl).usePlaintext().build();
            
        }

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