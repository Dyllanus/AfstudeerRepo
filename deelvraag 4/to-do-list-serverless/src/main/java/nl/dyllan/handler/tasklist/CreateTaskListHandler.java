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
import nl.dyllan.domain.TaskList;
import nl.dyllan.handler.tasklist.dto.CreateTaskListDto;

import java.util.UUID;

@Named("createTaskList")
public class CreateTaskListHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
  @Inject
  BoardRepository boardRepository;

  @Inject
  UserRepository userRepository;

  ObjectMapper mapper = Utils.mapper;

  @Override
  public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, Context context) {
    String owner = apiGatewayProxyRequestEvent.getHeaders().get("username");
    var boardId = UUID.fromString(apiGatewayProxyRequestEvent.getPathParameters().get("boardId"));
    String jsonBody = apiGatewayProxyRequestEvent.getBody();
    TaskList taskList;
    try {
      var dto = mapper.readValue(jsonBody, CreateTaskListDto.class);
      taskList = new TaskList(dto.title(), dto.description());
    } catch (JsonProcessingException e) {
      return Utils.createResponse(400, "Error processing json request");
    }

    var posUser = userRepository.getUser(owner);
    if (posUser.isEmpty()) return Utils.createResponse(404, "User" + owner + " not found" );

    var posBoard = boardRepository.findWholeBoard(boardId);
    if (posBoard.isEmpty()) return Utils.createResponse(404, "Board   " + boardId + " not found");
    var board = posBoard.get();
    board.addTaskList(taskList);
    boardRepository.save(board);
    try {
      return Utils.createResponse(201, mapper.writeValueAsString(board));
    } catch (JsonProcessingException e) {
      return Utils.createResponse(400, "Error creating response");
    }
  }
}
