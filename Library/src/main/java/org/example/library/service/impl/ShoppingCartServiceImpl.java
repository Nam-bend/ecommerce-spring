package org.example.library.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.library.entity.CartItem;
import org.example.library.entity.Customer;
import org.example.library.entity.Product;
import org.example.library.entity.ShoppingCart;
import org.example.library.repository.CartItemRepository;
import org.example.library.repository.ShoppingCartRepository;
import org.example.library.service.ShoppingCartService;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    private final CartItemRepository cartItemRepository;
    private final ShoppingCartRepository shoppingCartRepository;

    @Override
    public ShoppingCart addItemToCart(Product product, int quantity, Customer customer) {
        ShoppingCart cart = customer.getShoppingCart();

        // Nếu customer chưa có cart thì tạo mới
        if (cart == null) {
            cart = new ShoppingCart();
            cart.setCustomer(customer);
            cart.setCartItem(new HashSet<>());
            cart.setTotalItems(0);
            cart.setTotalPrices(0.0);
        }

        Set<CartItem> cartItems = cart.getCartItem();
        if (cartItems == null) {
            cartItems = new HashSet<>();
        }

        // Tìm sản phẩm trong cart
        CartItem cartItem = findCartItem(cartItems, product.getId());

        if (cartItem == null) {
            // Thêm sản phẩm mới
            cartItem = new CartItem();
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cartItem.setUnitPrice(product.getSalePrice());  // giá đơn vị
            cartItem.setTotalPrice(quantity * product.getSalePrice());
            cartItem.setShoppingCart(cart);

            cartItems.add(cartItem);
        } else {
            // Cập nhật số lượng và giá
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItem.setTotalPrice(cartItem.getQuantity() * product.getSalePrice());
        }

        // Cập nhật giỏ
        cart.setCartItem(cartItems);
        cart.setTotalItems(totalItems(cartItems));
        cart.setTotalPrices(totalPrice(cartItems));

        // Chỉ cần save cart, Hibernate sẽ cascade xuống CartItem
        return shoppingCartRepository.save(cart);
    }


    @Override
    public ShoppingCart updateItemInCart(Product product, int quantity, Customer customer) {
        ShoppingCart cart = customer.getShoppingCart();
        Set<CartItem> cartItems = cart.getCartItem();

        CartItem item = findCartItem(cartItems, product.getId());
        if (item == null) {
            return cart; // không có item để update
        }

        item.setQuantity(quantity);
        item.setTotalPrice(quantity * product.getSalePrice()); // dùng salePrice
        cartItemRepository.save(item);

        cart.setTotalItems(totalItems(cartItems));
        cart.setTotalPrices(totalPrice(cartItems));

        return shoppingCartRepository.save(cart);
    }

    @Override
    public ShoppingCart deleteItemFromCart(Product product, Customer customer) {
        ShoppingCart cart = customer.getShoppingCart();
        Set<CartItem> cartItems = cart.getCartItem();

        CartItem item = findCartItem(cartItems, product.getId());
        if (item == null) {
            return cart; // không có item để xóa
        }

        cartItems.remove(item);
        cartItemRepository.delete(item);

        cart.setCartItem(cartItems);
        cart.setTotalItems(totalItems(cartItems));
        cart.setTotalPrices(totalPrice(cartItems));

        return shoppingCartRepository.save(cart);
    }

    // tìm item theo productId
    private CartItem findCartItem(Set<CartItem> cartItems, Long productId) {
        if (cartItems == null) return null;
        for (CartItem item : cartItems) {
            if (item.getProduct().getId() != null && item.getProduct().getId().equals(productId)) {
                return item;
            }
        }
        return null;
    }

    private int totalItems(Set<CartItem> cartItems) {
        int total = 0;
        for (CartItem item : cartItems) {
            total += item.getQuantity();
        }
        return total;
    }

    private double totalPrice(Set<CartItem> cartItems) {
        double total = 0.0;
        for (CartItem item : cartItems) {
            total += item.getTotalPrice();
        }
        return total;
    }
}
