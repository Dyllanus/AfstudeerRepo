package nl.taskmate.boardservice.application;

import nl.taskmate.boardservice.domain.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Set;

@Component
public class SendRequestToUserService {

    private final RestTemplate restTemplate = new RestTemplate();
    @Value("${userservice.baseurl}")
    private String userServiceUrl;

    public void checkIfUsersExist(Set<String> usernames) {
        try {
            ResponseEntity<String> response = restTemplate.exchange(userServiceUrl + "?usernames=" + String.join(",", usernames), HttpMethod.HEAD, null, String.class);
            if (response.getStatusCode().is2xxSuccessful()) return;
            throw new UserNotFoundException(response.getBody());
        } catch (HttpClientErrorException e) {
            throw new UserNotFoundException(e.getMessage());
        }
    }
}
