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
import nl.dyllan.domain.User;
import nl.dyllan.handler.user.dto.SaveUserDto;
import software.amazon.awssdk.http.HttpStatusCode;

@Named("saveUser")
public class SaveUserHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

  @Inject
  UserRepository userRepository;

  ObjectMapper mapper = Utils.mapper;

  @Override
  public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, Context context) {
    String jsonBody = apiGatewayProxyRequestEvent.getBody();
    try {
      SaveUserDto saveUserDto = mapper.readValue(jsonBody, SaveUserDto.class);
      var possibleUser = userRepository.getUser(saveUserDto.username());
      if (possibleUser.isPresent()) {
        return Utils.createResponse(400, "User already exists. Try a different username");
      } else {
        User user = new User(saveUserDto.username());
        userRepository.save(user);
        return Utils.createResponse(201,  mapper.writeValueAsString(user));
      }
    } catch (JsonProcessingException e) {
      return new APIGatewayProxyResponseEvent()
              .withStatusCode(HttpStatusCode.BAD_REQUEST)
              .withBody("Product ID in the body does not match path parameter");
    }
  }
}
