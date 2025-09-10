package com.nagarro.ecommerce.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.nagarro.ecommerce.entity.FriendRequest;

public interface FriendRequestService {
    ResponseEntity<ResponseMessage> sendFriendRequest(String senderUsername, String recipientUsername);
    ResponseEntity<ResponseMessage> acceptFriendRequest(String senderUsername, String recipientUsername);
    ResponseEntity<ResponseMessage> rejectFriendRequest(String senderUsername, String recipientUsername);
	List<FriendRequest> getReceivedFriendRequests(String username);
	List<FriendRequest> getSentFriendRequests(String username);
	List<String> getMyFriends(String username);
	
}
