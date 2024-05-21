package nl.taskmate.boardservice.presentation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(1)
public class UsernameHeaderFilter extends OncePerRequestFilter {
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();
    @Value("${userservice.baseurl}")
    private String userServiceUrl;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        HeaderMapRequestWrapper requestWrapper = new HeaderMapRequestWrapper(request);
        if (!(request.getMethod().equalsIgnoreCase("OPTIONS")) && request.getRequestURI().startsWith("/boardservice/api/boards")) {
            String username = requestWrapper.getHeader("userdata");
            var valid = checkIfUserExists(username);
            if (!valid) {
                response.setStatus(404);
                response.getOutputStream().write(("User with username '" + username + "' was not found.").getBytes());
                return;
            }
        }
        filterChain.doFilter(requestWrapper, response);
    }

    private boolean checkIfUserExists(String username) {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(userServiceUrl + "/" + username, String.class);
            JsonNode root = mapper.readTree(response.getBody());
            String name = root.path("username").asText();
            if (!name.equals(username)) {
                return false;
            }
        } catch (JsonProcessingException | HttpClientErrorException e) {
            return false;
        }
        return true;
    }

}
