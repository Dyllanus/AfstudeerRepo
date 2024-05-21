package nl.dyllan;

import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.function.aws.runtime.AbstractMicronautLambdaRuntime;
import nl.dyllan.handler.CreateProductHandler;
import nl.dyllan.handler.DeleteProductHandler;
import nl.dyllan.handler.GetAllProductsHandler;
import nl.dyllan.handler.GetProductByIdHandler;

import java.net.MalformedURLException;

public class FunctionLambdaRuntime extends AbstractMicronautLambdaRuntime<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent, APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent>
{
  public static void main(String[] args) {
    try {
      new FunctionLambdaRuntime().run(args);

    } catch (MalformedURLException e) {
      e.printStackTrace();
    }
  }

  @Override
  @Nullable
  protected RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> createRequestHandler(String... args) {
    return switch (System.getenv("LAMBDA_FUNCTION")){
      case "CreateProduct" -> new CreateProductHandler();
      case "DeleteProduct" -> new DeleteProductHandler();
      case "GetAllProducts" -> new GetAllProductsHandler();
      case "GetProductById" -> new GetProductByIdHandler();
      default -> throw new IllegalArgumentException("Invalid function environment");
    };
  }
}