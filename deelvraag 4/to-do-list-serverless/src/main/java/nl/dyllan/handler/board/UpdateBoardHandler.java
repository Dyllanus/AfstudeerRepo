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
import nl.dyllan.handler.board.dto.CreateBoardDto;

import java.util.UUID;

@Named("updateBoard")
public class UpdateBoardHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {


  @Inject
  BoardRepository boardRepository;

  @Inject
  UserRepository userRepository;

  ObjectMapper mapper = Utils.mapper;

  @Override
  public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, Context context) {
    String owner = apiGatewayProxyRequestEvent.getHeaders().get("username");
    String id = apiGatewayProxyRequestEvent.getPathParameters().get("boardId");
    String body = apiGatewayProxyRequestEvent.getBody();
    CreateBoardDto boardDto;

    try {
      boardDto = mapper.readValue(body, CreateBoardDto.class);
    }catch (JsonProcessingException e){
      return Utils.createResponse(400, "Invalid request body");
    }

    var posUser = userRepository.getUser(owner);
    if (posUser.isEmpty()) return Utils.createResponse(404, "User" + owner + " not found" );

    var posBoard = boardRepository.findById(UUID.fromString(id));
    if (posBoard.isEmpty()) return Utils.createResponse(404, "Board " + id + " not found");
    var board = posBoard.get();
    board.setTitle(boardDto.title());
    board.setDescription(boardDto.description());
    boardRepository.save(board);

    try {
      return Utils.createResponse(200, mapper.writeValueAsString(board));
    } catch (JsonProcessingException e) {
      return Utils.createResponse(400, "error creating response");
    }

  }
}
