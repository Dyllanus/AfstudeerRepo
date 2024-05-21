package nl.dyllan.handler.tasks.dto;

import java.util.List;
import java.util.Set;

public record CreateTaskDto(String title, String description,
                            List<String> tags, Set<String> assignedUsers) {
}
