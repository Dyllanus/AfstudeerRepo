package nl.dyllan.handler.tasklist;


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
import nl.dyllan.handler.tasklist.dto.MoveTaskRequestDto;

import java.util.UUID;

@Named("moveTask")
public class MoveTaskHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

  @Inject
  BoardRepository boardRepository;

  @Inject
  UserRepository userRepository;

  ObjectMapper mapper = Utils.mapper;

  @Override
  public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, Context context) {
    String owner = apiGatewayProxyRequestEvent.getHeaders().get("username");
    UUID boardId = UUID.fromString(apiGatewayProxyRequestEvent.getPathParameters().get("boardId"));
    UUID oldTaskListId = UUID.fromString(apiGatewayProxyRequestEvent.getPathParameters().get("taskListId"));
    String jsonBody = apiGatewayProxyRequestEvent.getBody();

    MoveTaskRequestDto dto;
    try {
      dto = mapper.readValue(jsonBody, MoveTaskRequestDto.class);
    } catch (JsonProcessingException e) {
      return Utils.createResponse(400, "Error converting request body");
    }

    var posUser = userRepository.getUser(owner);
    if (posUser.isEmpty()) return Utils.createResponse(404, "User" + owner + " not found" );

    var posBoard = boardRepository.findWholeBoard(boardId);
    if (posBoard.isEmpty()) return Utils.createResponse(404, "Board not found");
    var board = posBoard.get();
    board.moveTask(dto.newTaskListId(), oldTaskListId, dto.taskId(), owner);

    try {
      return Utils.createResponse(200, mapper.writeValueAsString(board));
    } catch (JsonProcessingException e) {
      return Utils.createResponse(400, "Error converting request body");
    }
  }
}
