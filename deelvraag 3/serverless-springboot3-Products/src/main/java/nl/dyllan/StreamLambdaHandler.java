package nl.dyllan;

import com.amazonaws.serverless.exceptions.ContainerInitializationException;
import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.serverless.proxy.spring.SpringBootLambdaContainerHandler;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StreamLambdaHandler implements RequestStreamHandler{


  private static final Logger logger = LoggerFactory.getLogger(StreamLambdaHandler.class);

  private static final SpringBootLambdaContainerHandler<AwsProxyRequest, AwsProxyResponse> handler;
  static {
    try {
      handler = SpringBootLambdaContainerHandler.getAwsProxyHandler(App.class);
      // For applications that take longer than 10 seconds to start, use the async builder:
      // handler = new SpringBootProxyHandlerBuilder<AwsProxyRequest>()
      //                    .defaultProxy()
      //                    .asyncInit()
      //                    .springBootApplication(Application.class)
      //                    .buildAndInitialize();
    } catch (ContainerInitializationException e) {
      // if we fail here. We re-throw the exception to force another cold start
      e.printStackTrace();
      throw new RuntimeException("Could not initialize Spring Boot application", e);
    }
  }


  @Override
  public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context)
    throws IOException {
    logger.info("entered generic stream lambda handler");
    handler.proxyStream(inputStream, outputStream, context);
  }

}