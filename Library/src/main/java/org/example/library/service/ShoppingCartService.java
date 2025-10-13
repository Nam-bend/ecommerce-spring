package org.example.library.service;

import org.example.library.entity.Customer;
import org.example.library.entity.Product;
import org.example.library.entity.ShoppingCart;

public interface ShoppingCartService {
    ShoppingCart addItemToCart(Product product, int quantity, Customer customer);
    ShoppingCart updateItemInCart(Product product, int quantity, Customer customer);
    ShoppingCart deleteItemFromCart(Product product, Customer customer);
}
