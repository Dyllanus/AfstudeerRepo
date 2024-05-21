package nl.dyllan.functions;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.dyllan.dao.ProductDao;
import nl.dyllan.entity.Product;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.http.HttpStatusCode;

import java.util.Optional;
import java.util.function.Function;

@Component
public class DeleteProductHandler implements Function<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
  public DeleteProductHandler(ProductDao productDao){
    dynamoProductDao = productDao;
  }

  private static ProductDao dynamoProductDao;

  @Override
  public APIGatewayProxyResponseEvent apply(APIGatewayProxyRequestEvent requestEvent) {
    if (!requestEvent.getHttpMethod().equals(HttpMethod.DELETE.name())) {
      return new APIGatewayProxyResponseEvent()
        .withStatusCode(HttpStatusCode.METHOD_NOT_ALLOWED)
        .withBody("Only DELETE method is supported");
    }
    try {
      String id = requestEvent.getPathParameters().get("id");
      dynamoProductDao.deleteProduct(id);
      return new APIGatewayProxyResponseEvent()
        .withStatusCode(HttpStatusCode.OK)
        .withBody("Product with id = " + id + " deleted");
    } catch (Exception je) {
      je.printStackTrace();
      return new APIGatewayProxyResponseEvent()
        .withStatusCode(HttpStatusCode.INTERNAL_SERVER_ERROR)
        .withBody("Internal Server Error :: " + je.getMessage());
    }
  }
}
