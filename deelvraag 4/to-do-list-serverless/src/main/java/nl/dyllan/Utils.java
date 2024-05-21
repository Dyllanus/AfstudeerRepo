package nl.dyllan;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import software.amazon.awssdk.http.HttpStatusCode;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class Utils {
  public static final ObjectMapper mapper = new ObjectMapper();

  static {
    SimpleModule awtModule = new SimpleModule("AWT Color Module");
    awtModule.addSerializer(Color.class, new ColorJsonSerializer());
    awtModule.addDeserializer(Color.class, new ColorJsonDeserializer());
    mapper.registerModule(awtModule);
    mapper.registerModule(new JavaTimeModule());
  }

  public static APIGatewayProxyResponseEvent createResponse(int statusCode, String body) {
    return createResponse(statusCode, body, new HashMap<>());
  }

  public static APIGatewayProxyResponseEvent createResponse(int statusCode,
                                                            String body, Map<String, String> headers) {
    return new APIGatewayProxyResponseEvent()
            .withBody(body)
            .withStatusCode(statusCode)
            .withHeaders(headers);
  }

}
