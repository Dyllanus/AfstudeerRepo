package nl.dyllan.handler;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.function.aws.MicronautRequestHandler;
import io.micronaut.json.JsonMapper;
import jakarta.inject.Inject;
import nl.dyllan.dao.ProductDao;
import nl.dyllan.entity.Product;
import software.amazon.awssdk.http.HttpStatusCode;
import software.amazon.awssdk.http.SdkHttpMethod;


@Introspected
public class CreateProductHandler extends MicronautRequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
  @Inject
  ProductDao productDao;

  @Inject
  JsonMapper objectMapper;
  @Override
  public APIGatewayProxyResponseEvent execute(APIGatewayProxyRequestEvent requestEvent) {
    if (!requestEvent.getHttpMethod().equals(SdkHttpMethod.PUT.name())) {
      return new APIGatewayProxyResponseEvent()
        .withStatusCode(HttpStatusCode.METHOD_NOT_ALLOWED)
        .withBody("Only PUT method is supported");
    }
    try {
      String id = requestEvent.getPathParameters().get("id");
      String jsonPayload = requestEvent.getBody();
      Product product = objectMapper.readValue(jsonPayload, Product.class);
      if (!product.id().equals(id)) {
        return new APIGatewayProxyResponseEvent()
          .withStatusCode(HttpStatusCode.BAD_REQUEST)
          .withBody("Product ID in the body does not match path parameter");
      }
      productDao.putProduct(product);
      return new APIGatewayProxyResponseEvent()
        .withStatusCode(HttpStatusCode.CREATED)
        .withBody("Product with id = " + id + " created");
    } catch (Exception e) {
      e.printStackTrace();
      return new APIGatewayProxyResponseEvent()
        .withStatusCode(HttpStatusCode.INTERNAL_SERVER_ERROR)
        .withBody("Internal Server Error");
    }
  }
}
