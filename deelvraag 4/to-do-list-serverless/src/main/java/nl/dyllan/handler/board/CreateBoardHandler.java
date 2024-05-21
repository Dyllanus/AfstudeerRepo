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
import nl.dyllan.domain.Board;
import nl.dyllan.handler.board.dto.CreateBoardDto;

@Named("createBoard")
public class CreateBoardHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

  @Inject
  UserRepository userRepository;

  @Inject
  BoardRepository boardRepository;

  ObjectMapper mapper = Utils.mapper;

  @Override
  public APIGatewayProxyResponseEvent handleRequest (APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, Context context) {
    String jsonBody = apiGatewayProxyRequestEvent.getBody();
    try {
      String owner = apiGatewayProxyRequestEvent.getHeaders().get("username");
      var posUser = userRepository.getUser(owner);
      if (posUser.isEmpty()) return Utils.createResponse(404, "User" + owner + " not found" );
      var user = posUser.get();

      CreateBoardDto dto = mapper.readValue(jsonBody, CreateBoardDto.class);
      Board board = new Board(dto.title(), dto.description(), owner);
      user.addOwnerBoard(board.getId());

      boardRepository.save(board);
      userRepository.save(user);
      return Utils.createResponse(201, mapper.writeValueAsString(board));
    } catch (JsonProcessingException e) {
      return Utils.createResponse(400, "Invalid request body");
    }
  }
}
