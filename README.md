# Microservices Project – Social Shopping Features

This repository contains multiple microservices that together form a single project.  
All services are interdependent and need to be **running simultaneously** for the system to function correctly.

## Services

1. **Friend-Request Service**  
   Handles friend connection requests between users (send, accept, reject, list connections).

2. **Users Service**  
   Manages user information such as registration, authentication, and profile data.

3. **Wishlist-Collaborator Service**  
   Allows users to collaborate on wishlists by adding friends who can also contribute items.

4. **Eureka Service (Service Registry)**  
   Service discovery and registry to manage and locate other microservices dynamically.
