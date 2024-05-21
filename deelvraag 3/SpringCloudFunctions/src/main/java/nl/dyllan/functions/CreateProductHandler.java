package nl.dyllan.functions;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.dyllan.dao.ProductDao;
import nl.dyllan.entity.Product;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.http.HttpStatusCode;

import java.util.function.Function;

@Component
public class CreateProductHandler implements Function<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

  public CreateProductHandler(ProductDao productDao){
    dynamoProductDao = productDao;
  }
  private static final ObjectMapper objectMapper = new ObjectMapper();
  private static ProductDao dynamoProductDao;

  @Override
  public APIGatewayProxyResponseEvent apply(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent) {
    try {
      String id = apiGatewayProxyRequestEvent.getPathParameters().get("id");
      String jsonPayload = apiGatewayProxyRequestEvent.getBody();
      Product product = objectMapper.readValue(jsonPayload, Product.class);
      if (!product.getId().equals(id)) {
        return new APIGatewayProxyResponseEvent()
          .withStatusCode(HttpStatusCode.BAD_REQUEST)
          .withBody("Product ID in the body does not match path parameter");
      }
      dynamoProductDao.putProduct(product);
      return new APIGatewayProxyResponseEvent()
        .withStatusCode(HttpStatusCode.CREATED)
        .withBody("Product with id = " + id + " created");
    } catch (JsonProcessingException e) {
      e.printStackTrace();
      return new APIGatewayProxyResponseEvent()
        .withStatusCode(HttpStatusCode.INTERNAL_SERVER_ERROR)
        .withBody("Internal Server Error");
    }
  };
}

