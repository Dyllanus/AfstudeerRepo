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


import java.util.Optional;

@Introspected
public class DeleteProductHandler extends MicronautRequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
  @Inject
  ProductDao productDao;

  @Inject
  JsonMapper objectMapper;

  @Override
  public APIGatewayProxyResponseEvent execute(APIGatewayProxyRequestEvent requestEvent) {

    if (!requestEvent.getHttpMethod().equals(SdkHttpMethod.DELETE.name())) {
      return new APIGatewayProxyResponseEvent()
        .withStatusCode(HttpStatusCode.METHOD_NOT_ALLOWED)
        .withBody("Only DELETE method is supported");
    }
    try {
      String id = requestEvent.getPathParameters().get("id");
      productDao.deleteProduct(id);
      return new APIGatewayProxyResponseEvent()
        .withStatusCode(HttpStatusCode.OK)
        .withBody("Product with id = " + id + " deleted");
    } catch (Exception e) {
      e.printStackTrace();
      return new APIGatewayProxyResponseEvent()
        .withStatusCode(HttpStatusCode.INTERNAL_SERVER_ERROR)
        .withBody("Internal Server Error");
    }
  }
}
