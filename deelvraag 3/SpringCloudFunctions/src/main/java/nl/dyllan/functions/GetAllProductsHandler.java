package nl.dyllan.functions;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.dyllan.dao.ProductDao;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.http.HttpStatusCode;

import java.util.function.Function;

@Component
public class GetAllProductsHandler implements Function<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
  public GetAllProductsHandler(ProductDao productDao){
    dynamoProductDao = productDao;
  }
  private static final ObjectMapper objectMapper = new ObjectMapper();
  private static ProductDao dynamoProductDao;

  @Override
  public APIGatewayProxyResponseEvent apply(APIGatewayProxyRequestEvent requestEvent) {
    if (!requestEvent.getHttpMethod().equals(HttpMethod.GET.name())) {
      return new APIGatewayProxyResponseEvent()
        .withStatusCode(HttpStatusCode.METHOD_NOT_ALLOWED)
        .withBody("Only GET method is supported");
    }
    try {
      return new APIGatewayProxyResponseEvent()
        .withStatusCode(HttpStatusCode.OK)
        .withBody(objectMapper.writeValueAsString(dynamoProductDao.getAllProduct()));
    } catch (JsonProcessingException je) {
      je.printStackTrace();
      return new APIGatewayProxyResponseEvent()
        .withStatusCode(HttpStatusCode.INTERNAL_SERVER_ERROR)
        .withBody("Internal Server Error");
    }
  }
}
