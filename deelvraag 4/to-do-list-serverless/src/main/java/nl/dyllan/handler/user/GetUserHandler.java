package nl.dyllan.handler.user;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import nl.dyllan.Utils;
import nl.dyllan.data.UserRepository;

@Named("getUser")
public class GetUserHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
  @Inject
  UserRepository userRepository;

  ObjectMapper mapper = Utils.mapper;

  @Override
  public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, Context context) {
    String username = apiGatewayProxyRequestEvent.getPathParameters().get("username");
    var possibleUser = userRepository.getUser(username);
    if (possibleUser.isPresent()) {
      try {
        return Utils.createResponse(200, mapper.writeValueAsString(possibleUser.get()));
      } catch (JsonProcessingException e) {
        return Utils.createResponse(404, "User not found");
      }
    }
    return Utils.createResponse(404, "User not found");
  }
}
