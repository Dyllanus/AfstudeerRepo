package nl.dyllan.handler.tasks;

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
import nl.dyllan.domain.Task;
import nl.dyllan.domain.exceptions.UserNotAssignedToBoardException;
import nl.dyllan.handler.tasks.dto.CreateTaskDto;

import java.util.UUID;

@Named("createUpdateTask")
public class CreateUpdateTaskHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

  @Inject
  BoardRepository boardRepository;
  
  ObjectMapper mapper = Utils.mapper;

  @Override
  public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, Context context) {
    String username = apiGatewayProxyRequestEvent.getHeaders().get("username");
    UUID taskListId = UUID.fromString(apiGatewayProxyRequestEvent.getPathParameters().get("taskListId"));
    UUID boardId = UUID.fromString(apiGatewayProxyRequestEvent.getPathParameters().get("boardId"));
    String jsonBody = apiGatewayProxyRequestEvent.getBody();
    CreateTaskDto dto;
    try{
      dto = mapper.readValue(jsonBody, CreateTaskDto.class);
    }catch (JsonProcessingException e){
      return Utils.createResponse(400, "Error processing request");
    }


    var optBoard = boardRepository.findWholeBoard(boardId);
    if (optBoard.isEmpty()) return Utils.createResponse(404, "Board not found");
    var board = optBoard.get();

    try {
      board.checkIfInAssignedUsers(username);
    } catch (UserNotAssignedToBoardException e){
      return Utils.createResponse(404, e.getMessage());
    }

    var tasklist = board.getTaskListById(taskListId);
    var task = new Task(dto.title(), dto.description());
    board.getTags().stream().filter(tag -> dto.tags().contains(tag.getTitle())).forEach(task::addTag);
    board.checkIfInAssignedUsers(dto.assignedUsers());
    task.setAssignedUsers(dto.assignedUsers());

    tasklist.addTask(task);
    boardRepository.save(board);

    try {
      return Utils.createResponse(201, mapper.writeValueAsString(task));
    } catch (JsonProcessingException e) {
      return Utils.createResponse(400, "error creating response");
    }
  }
}
