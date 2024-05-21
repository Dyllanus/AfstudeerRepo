package nl.dyllan.handler.board;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import nl.dyllan.Utils;
import nl.dyllan.data.BoardRepository;
import nl.dyllan.data.UserRepository;

import java.util.HashSet;
import java.util.UUID;

@Named("getAllBoards")
public class GetAllBoardsHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

  @Inject
  BoardRepository boardRepository;

  @Inject
  UserRepository userRepository;

  ObjectMapper mapper = Utils.mapper;


  @Override
  public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, Context context) {
    String owner = apiGatewayProxyRequestEvent.getHeaders().get("username");
    var posUser = userRepository.getUser(owner);
    if (posUser.isEmpty()) return Utils.createResponse(404, "User" + owner + " not found" );
    var user = posUser.get();
    var allIds = new HashSet<UUID>();
    allIds.addAll(user.getOwnerBoard());
    allIds.addAll(user.getInvolvedBoards());
    var boards = boardRepository.findAllBoardsByIds(allIds);

    try {
      return Utils.createResponse(200, mapper.writeValueAsString(boards));
    } catch (JsonProcessingException e) {
      return Utils.createResponse(400, "Error creating response");
    }
  }
}
