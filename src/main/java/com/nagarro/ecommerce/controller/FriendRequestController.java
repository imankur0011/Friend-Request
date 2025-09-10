package com.nagarro.ecommerce.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nagarro.ecommerce.dto.UserDTO;
import com.nagarro.ecommerce.entity.FriendRequest;
import com.nagarro.ecommerce.model.FriendRequestRequest;
import com.nagarro.ecommerce.service.FriendRequestService;
import com.nagarro.ecommerce.service.ResponseMessage;

@RestController
@RequestMapping("/api/friends")
public class FriendRequestController {

    private final FriendRequestService friendRequestService;

    public FriendRequestController(FriendRequestService friendRequestService) {
        this.friendRequestService = friendRequestService;
    }

    @PostMapping("/add")
    public ResponseEntity<ResponseMessage> sendFriendRequest(@RequestBody FriendRequestRequest request) {
        return friendRequestService.sendFriendRequest(request.getSenderUsername(), request.getRecipientUsername());
    }

    @PostMapping("/accept")
    public ResponseEntity<ResponseMessage> acceptFriendRequest(@RequestBody FriendRequestRequest request) {
        return friendRequestService.acceptFriendRequest(request.getSenderUsername(), request.getRecipientUsername());
    }

    @PostMapping("/reject")
    public ResponseEntity<ResponseMessage> rejectFriendRequest(@RequestBody FriendRequestRequest request) {
        return friendRequestService.rejectFriendRequest(request.getSenderUsername(), request.getRecipientUsername());
    }

    @GetMapping("/sent-requests")
    public ResponseEntity<List<FriendRequest>> getSentFriendRequests(@RequestParam String username) {
        List<FriendRequest> sentRequests = friendRequestService.getSentFriendRequests(username);
        return ResponseEntity.ok(sentRequests);
    }

    @GetMapping("/received-requests")
    public ResponseEntity<List<FriendRequest>> getReceivedFriendRequests(@RequestParam String username) {
        List<FriendRequest> receivedRequests = friendRequestService.getReceivedFriendRequests(username);
        return ResponseEntity.ok(receivedRequests);
    }
    @GetMapping("/my-friends")
    public ResponseEntity<List<String>> getMyFriends(@RequestParam String username) {
        List<String> myFriends = friendRequestService.getMyFriends(username);
        return ResponseEntity.ok(myFriends);
    }
  
}
