package com.nagarro.ecommerce.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
	@Table(name = "friend_request")
	public class FriendRequest {
	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long friendRequestId;

	    @Column(nullable = false)
	    private String senderId;

	    @Column(nullable = false)
	    private String recipientId;

	    @Column(nullable = false)
	    private String status;

	    @Column(nullable = false)
	    private LocalDateTime createdAt;

		public Long getFriendRequestId() {
			return friendRequestId;
		}

		public void setFriendRequestId(Long friendRequestId) {
			this.friendRequestId = friendRequestId;
		}

		public String getSenderId() {
			return senderId;
		}

		public void setSenderId(String senderId) {
			this.senderId = senderId;
		}

		public String getRecipientId() {
			return recipientId;
		}

		public void setRecipientId(String recipientId) {
			this.recipientId = recipientId;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public LocalDateTime getCreatedAt() {
			return createdAt;
		}

		public void setCreatedAt(LocalDateTime createdAt) {
			this.createdAt = createdAt;
		}
}








