package nl.taskmate.userservice.presentation.dto;

import nl.taskmate.userservice.domain.User;

public record UserDto(String id, String username, String displayName) {
    public static UserDto fromUser(User user) {
        return new UserDto(user.getId().toString(), user.getUsername(), user.getDisplayName());
    }
}
