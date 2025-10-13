package org.example.library.repository;

import org.example.library.entity.CartItem;
import org.example.library.entity.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem,Long > {

}
