package com.example.demo.controller;


import com.example.demo.entity.OrderDetailEntity;
import com.example.demo.entity.OrdersEntity;
import com.example.demo.entity.ProductEntity;
import com.example.demo.repository.OrderDetailRepository;
import com.example.demo.repository.OrdersRepository;
import com.example.demo.repository.ProductRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class CartController {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private OrdersRepository ordersRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;


    @RequestMapping(value = "/cart", method = RequestMethod.GET)
    public String showCart(HttpSession session, Model model) {
        Map<Integer, Integer> cart = (Map<Integer, Integer>) session.getAttribute("cart");
        if (cart == null) {
            cart = new HashMap<>();
        }
        List<Integer> productIds = cart.keySet().stream().collect(Collectors.toList());
        List<ProductEntity> products = (List<ProductEntity>) productRepository.findAllById(productIds);
        Map<ProductEntity, Integer> cartItems = new HashMap<>();
        for (ProductEntity product : products) {
            cartItems.put(product, cart.get(product.getId()));
        }
        model.addAttribute("cartItems", cartItems);
        return "cart";
    }

    @RequestMapping(value = "/addToCart/{id}", method = RequestMethod.POST)
    public String addToCart(@PathVariable("id") int productId, HttpSession session) {
        Map<Integer, Integer> cart = (Map<Integer, Integer>) session.getAttribute("cart");
        if (cart == null) {
            cart = new HashMap<>();
        }
        if (cart.containsKey(productId)) {
            cart.put(productId, cart.get(productId) + 1);
        } else {
            cart.put(productId, 1);
        }
        session.setAttribute("cart", cart);
        return "redirect:/home";
    }

    @RequestMapping(value = "/checkoutform", method = RequestMethod.GET)
    public String checkoutform() {
        return "checkout";
    }

    @RequestMapping(value = "/checkoutform", method = RequestMethod.POST)
    public String checkoutformppost(@RequestParam("name") String name, @RequestParam("address") String address, HttpSession session) {
        OrdersEntity order = new OrdersEntity();
        order.setCustomerName(name);
        order.setCustomerAddress(address);
        order.setOrderDate(Date.valueOf(LocalDate.now()));
        ordersRepository.save(order);

        Map<Integer, Integer> cart = (Map<Integer, Integer>) session.getAttribute("cart");
        if (cart == null) {
            cart = new HashMap<>();
        }

        List<OrderDetailEntity> orderDetails = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : cart.entrySet()) {
            int productId = entry.getKey();
            int quantity = entry.getValue();

            ProductEntity product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found with id " + productId));

            OrderDetailEntity orderDetail = new OrderDetailEntity();
            orderDetail.setOrdersEntity(order);
            orderDetail.setProductEntity(product);
            orderDetail.setQuality(quantity);

            orderDetails.add(orderDetail);
        }

        order.setOrderDetails(orderDetails);
        ordersRepository.save(order);

        return "redirect:/home";
    }

    @RequestMapping(value = "/allcheckout", method = RequestMethod.GET)
    public String showCheckOut(Model model) {
        List<OrdersEntity> checkoutList = (List<OrdersEntity>) ordersRepository.findAll();
        model.addAttribute("checkOutList", checkoutList);
        return "allcheckout";
    }

    @RequestMapping(value = "/orderdetail/{id}", method = RequestMethod.POST)
    public String detailCheckout(@PathVariable("id") int orderId, Model model) {
        List<OrderDetailEntity> orderdetail = (List<OrderDetailEntity>) orderDetailRepository.findByOrdersEntityId(orderId);
        model.addAttribute("checkOutList", orderdetail);
        return "orderDetail";
    }
}
