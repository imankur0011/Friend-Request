package com.nagarro.ecommerce.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.nagarro.ecommerce.dto.UserDTO;
import com.nagarro.ecommerce.entity.FriendRequest;
import com.nagarro.ecommerce.exception.FriendRequestException;
import com.nagarro.ecommerce.repository.FriendRequestRepository;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@Service
@Transactional
public class FriendRequestServiceImpl implements FriendRequestService {

    private final FriendRequestRepository friendRequestRepository;
    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final CircuitBreakerConfig circuitBreakerConfig;
    private final RestTemplate restTemplate; // Inject the RestTemplate here
    private final String userServiceBaseUrl; // Configuration property for User service base URL

    public FriendRequestServiceImpl(FriendRequestRepository friendRequestRepository,
                                    CircuitBreakerRegistry circuitBreakerRegistry,
                                    CircuitBreakerConfig circuitBreakerConfig,
                                    RestTemplate restTemplate,
                                    @Value("http://localhost:8091") String userServiceBaseUrl) {
        this.friendRequestRepository = friendRequestRepository;
        this.circuitBreakerRegistry = circuitBreakerRegistry;
        this.circuitBreakerConfig = circuitBreakerConfig;
        this.restTemplate = restTemplate;
        this.userServiceBaseUrl = userServiceBaseUrl;
        
    }


    @Override
    @CircuitBreaker(name = "sendFriendRequest", fallbackMethod = "sendFriendRequestFallback")
    public ResponseEntity<ResponseMessage> sendFriendRequest(String senderUsername, String recipientUsername) {
        if (senderUsername.equals(recipientUsername)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseMessage("You cannot send a friend request to yourself.", HttpStatus.BAD_REQUEST.value()));
        }

        // Fetch the user from User service and add it to the database if not present
        UserDTO senderDTO = getUserByUsername(senderUsername);
        if (senderDTO == null) {
            // If the sender user does not exist, create a new UserDTO object and add the necessary details
            senderDTO = new UserDTO();
            senderDTO.setUsername(senderUsername);
//            senderDTO.setName(name); // Set the sender's name
//            senderDTO.setEmail(email); // Set the sender's email
//            senderDTO.setPassword(password); // Set the sender's password

            // Set other properties of the senderDTO as needed

            // Add the sender user to the User microservice
            addUserToUserMicroservice(senderDTO);
        }

        // Similarly, do the same for the recipient user
        UserDTO recipientDTO = getUserByUsername(recipientUsername);
        if (recipientDTO == null) {
            // If the recipient user does not exist, create a new UserDTO object and add the necessary details
            recipientDTO = new UserDTO();
            recipientDTO.setUsername(recipientUsername);
//            recipientDTO.setName("Recipient Name"); // Set the recipient's name
//            recipientDTO.setEmail("recipient@example.com"); // Set the recipient's email
//            recipientDTO.setPassword("recipientpassword"); // Set the recipient's password

            // Set other properties of the recipientDTO as needed

            // Add the recipient user to the User microservice
            addUserToUserMicroservice(recipientDTO);
        }

        // Check if a friend request already exists between the users
        if (friendRequestRepository.findBySenderIdAndRecipientId(senderUsername, recipientUsername).isPresent()) {
            return ResponseEntity.status(HttpStatus.ALREADY_REPORTED)
                    .body(new ResponseMessage("Friend request already sent to the recipient.", HttpStatus.ALREADY_REPORTED.value()));
        }

        // If both sender and recipient exist, create and save the friend request
        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setSenderId(senderUsername);
        friendRequest.setRecipientId(recipientUsername);
        friendRequest.setStatus("pending");
        friendRequest.setCreatedAt(LocalDateTime.now());

        friendRequestRepository.save(friendRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseMessage("Friend request sent successfully.", HttpStatus.CREATED.value()));
    }


       

    private UserDTO getUserByUsername(String username) {
        String userApiUrl = userServiceBaseUrl + "/api/users/" + username;
        ResponseEntity<UserDTO> response;
        try {
            response = restTemplate.getForEntity(userApiUrl, UserDTO.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            } else if (response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError()) {
                // Log the error
                System.err.println("Failed to fetch user from User service. Status code: " + response.getStatusCode());
                return null;
            } else {
                // Unexpected status code, log the error
                System.err.println("Unexpected status code: " + response.getStatusCode());
                return null;
            }
        } catch (RestClientException e) {
            // Log the error
            System.err.println("Failed to fetch user from User service.");
            e.printStackTrace();
            return null;
        }
    }


 // Method to add a user to the User microservice
    private void addUserToUserMicroservice(UserDTO userDTO) {
        String userApiUrl = userServiceBaseUrl + "/api/users";
        try {
            restTemplate.postForObject(userApiUrl, userDTO, UserDTO.class);
        } catch (RestClientException e) {
            // Handle the RestClientException here (e.g., log the error)
            // In this example, we'll just throw a FriendRequestException for simplicity
            throw new FriendRequestException("Error adding user to User microservice.");
        }
    }



    // Implement the fallback method to be called in case of a circuit breaker open event
    public ResponseEntity<ResponseMessage>sendFriendRequestFallback (String senderUsername, String recipientUsername, Throwable t) {
        // Handle the fallback logic here, return a custom response
        // This method will be called when the circuit breaker is open
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(new ResponseMessage("Service unavailable, please try again later.", HttpStatus.SERVICE_UNAVAILABLE.value()));
    }

    @Override
    @CircuitBreaker(name = "acceptFriendRequest", fallbackMethod = "acceptFriendRequestFallback")
    public ResponseEntity<ResponseMessage> acceptFriendRequest(String senderUsername, String recipientUsername) {
        FriendRequest friendRequest = getFriendRequest(senderUsername, recipientUsername);

        if (friendRequest.getStatus().equals("pending")) {
            friendRequest.setStatus("accepted");
            friendRequestRepository.save(friendRequest);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage("Friend request accepted successfully.", HttpStatus.OK.value()));
        } else if (friendRequest.getStatus().equals("accepted")) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ResponseMessage("Friend request is already accepted.", HttpStatus.CONFLICT.value()));
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ResponseMessage("Friend request is not pending, It is rejected.", HttpStatus.CONFLICT.value()));
        }
    }

    // Implement the fallback method to be called in case of a circuit breaker open event
    public ResponseEntity<ResponseMessage> acceptFriendRequestFallback(String senderUsername, String recipientUsername, Throwable t) {
        // Handle the fallback logic here, return a custom response
        // This method will be called when the circuit breaker is open
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(new ResponseMessage("Service unavailable, please try again later.", HttpStatus.SERVICE_UNAVAILABLE.value()));
    }

    @Override
    @CircuitBreaker(name = "rejectFriendRequest", fallbackMethod = "rejectFriendRequestFallback")
    public ResponseEntity<ResponseMessage> rejectFriendRequest(String senderUsername, String recipientUsername) {
        FriendRequest friendRequest = getFriendRequest(senderUsername, recipientUsername);

        if (friendRequest.getStatus().equals("pending")) {
            friendRequest.setStatus("rejected");
            friendRequestRepository.save(friendRequest);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage("Friend request rejected successfully.", HttpStatus.OK.value()));
        } else if (friendRequest.getStatus().equals("rejected")) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ResponseMessage("Friend request is already rejected.", HttpStatus.CONFLICT.value()));
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ResponseMessage("Friend request is not pending, It is accepted.", HttpStatus.CONFLICT.value()));
        }
    }

    // Implement the fallback method to be called in case of a circuit breaker open event
    public ResponseEntity<ResponseMessage> rejectFriendRequestFallback(String senderUsername, String recipientUsername, Throwable t) {
        // Handle the fallback logic here, return a custom response
        // This method will be called when the circuit breaker is open
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(new ResponseMessage("Service unavailable, please try again later.", HttpStatus.SERVICE_UNAVAILABLE.value()));
    }

    @Override
    public List<FriendRequest> getSentFriendRequests(String username) {
        return friendRequestRepository.findBySenderId(username);
    }

    @Override
    public List<FriendRequest> getReceivedFriendRequests(String username) {
        return friendRequestRepository.findByRecipientId(username);
    }

    private FriendRequest getFriendRequest(String senderUsername, String recipientUsername) {
        return friendRequestRepository.findBySenderIdAndRecipientId(senderUsername, recipientUsername)
                .orElseThrow(() -> new FriendRequestException("Friend request not found."));
    }
    @Override
    public List<String> getMyFriends(String username) {
        List<FriendRequest> acceptedFriendRequests = friendRequestRepository.findByRecipientIdAndStatus(username, "accepted");
        List<String> myFriends = new ArrayList<>();

        for (FriendRequest friendRequest : acceptedFriendRequests) {
            myFriends.add(friendRequest.getSenderId());
        }

        List<FriendRequest> sentFriendRequests = friendRequestRepository.findBySenderIdAndStatus(username, "accepted");
        for (FriendRequest friendRequest : sentFriendRequests) {
            myFriends.add(friendRequest.getRecipientId());
            
        }
		return myFriends;
    }

}
