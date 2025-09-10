package com.nagarro.ecommerce.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nagarro.ecommerce.entity.FriendRequest;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {
    List<FriendRequest> findBySenderIdOrRecipientId(String senderId, String recipientId);
    // Add more custom queries as needed
    List<FriendRequest> findBySenderId(String senderId);

    List<FriendRequest> findByRecipientId(String recipientId);
    
    Optional<FriendRequest> findBySenderIdAndRecipientId(String senderId, String recipientId);
    List<FriendRequest> findByRecipientIdAndStatus(String recipientId, String status);
    List<FriendRequest> findBySenderIdAndStatus(String recipientId, String status);
   
}
