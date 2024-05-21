package nl.dyllan;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.dyllan.dao.DynamoProductDao;
import nl.dyllan.dao.ProductDao;
import nl.dyllan.entity.Product;
import software.amazon.awssdk.http.HttpStatusCode;

import java.io.IOException;

public class PutProduct implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

  ProductDao productDao = new DynamoProductDao();

  ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public APIGatewayProxyResponseEvent handleRequest (APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, Context context) {
      try {
        String id = apiGatewayProxyRequestEvent.getPathParameters().get("id");;
        String jsonPayload = apiGatewayProxyRequestEvent.getBody();
        Product product = objectMapper.readValue(jsonPayload, Product.class);
        if (!product.getId().equals(id)) {
          return new APIGatewayProxyResponseEvent()
            .withStatusCode(HttpStatusCode.BAD_REQUEST)
            .withBody("Product ID in the body does not match path parameter");
        }
        productDao.putProduct(product);
        return new APIGatewayProxyResponseEvent()
          .withStatusCode(HttpStatusCode.CREATED)
          .withBody("Product with id = " + id + " created");
      } catch (IOException e) {
        e.printStackTrace();
        return new APIGatewayProxyResponseEvent()
          .withStatusCode(HttpStatusCode.INTERNAL_SERVER_ERROR)
          .withBody("Internal Server Error");
      }
  }
}
