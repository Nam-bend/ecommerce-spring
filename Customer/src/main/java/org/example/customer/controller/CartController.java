package org.example.customer.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.library.entity.Customer;
import org.example.library.entity.Product;
import org.example.library.entity.ShoppingCart;
import org.example.library.service.CustomerService;
import org.example.library.service.ProductService;
import org.example.library.service.ShoppingCartService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class CartController {

    private final CustomerService customerService;
    private final ProductService productService;
    private final ShoppingCartService cartService;


    @GetMapping("/cart")
    public String cart(Model model, Principal principal, HttpSession session) {
        if (principal == null) {
            return "redirect:/login";
        }

        String username = principal.getName();
        Customer customer = customerService.findByUsername(username);
        ShoppingCart shoppingCart = customer.getShoppingCart();

        if (shoppingCart == null || shoppingCart.getCartItem().isEmpty()) {
            model.addAttribute("check", "Giỏ hàng trống");
        } else {
            model.addAttribute("shoppingCart", shoppingCart);
            model.addAttribute("subTotal", shoppingCart.getTotalPrices());
            session.setAttribute("totalItems", shoppingCart.getTotalItems());
        }

        return "cart"; // view cart.html
    }
    @PostMapping("/add-to-cart")
    public String addToCart(@RequestParam("id") Long productId,
                            @RequestParam(value = "quantity", defaultValue = "1") int quantity,
                            Principal principal,
                            @RequestHeader(value = "Referer", required = false) String referer) {
        if (principal == null) {
            return "redirect:/login";
        }

        String username = principal.getName();
        Customer customer = customerService.findByUsername(username);
        Product product = productService.getProductById(productId);

        cartService.addItemToCart(product, quantity, customer);

        return "redirect:" + (referer != null ? referer : "/cart");
    }




    @PostMapping(value = "/update-cart", params = "action=update")
    public String updateCart(@RequestParam("id") Long productId,
                             @RequestParam("quantity") int quantity,
                             Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        String username = principal.getName();
        Customer customer = customerService.findByUsername(username);
        Product product = productService.getProductById(productId);

        cartService.updateItemInCart(product, quantity, customer);

        return "redirect:/cart";
    }


    @PostMapping(value = "/update-cart", params = "action=delete")
    public String deleteFromCart(@RequestParam("id") Long productId,
                                 Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        String username = principal.getName();
        Customer customer = customerService.findByUsername(username);
        Product product = productService.getProductById(productId);

        cartService.deleteItemFromCart(product, customer);

        return "redirect:/cart";
    }
}
