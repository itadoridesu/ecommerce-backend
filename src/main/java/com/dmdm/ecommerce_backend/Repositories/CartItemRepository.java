package com.dmdm.ecommerce_backend.Repositories;

import com.dmdm.ecommerce_backend.Entity.CartItem;
import com.dmdm.ecommerce_backend.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    /// WOOOOOOOOOOW, no need to write a single SQL line, Spring data JPA is crazy
//    findByUserId means: look for entity with a field user, then inside it id.
//    findByUserUsername means: look inside user → username.
//    findByOrderCustomerName would mean: order → customer → name.
    List<CartItem> findByUser(User user);
    Optional<CartItem> findByUserAndProductId(User user, Long productId);
}
