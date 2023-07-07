package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;

import com.example.demo.TestUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {

    private OrderController orderController;

    private UserRepository userRepository = mock(UserRepository.class);

    private OrderRepository orderRepository = mock(OrderRepository.class);

    @Before
    public void setup(){
        // init controller
        orderController = new OrderController();
        // inject mocks
        TestUtils.injectObjects(orderController, "userRepository", userRepository);
        TestUtils.injectObjects(orderController, "orderRepository", orderRepository);
    }

    // test happy path submit order

    @Test
    public void submit_order_happy_path(){
        User user = new User();
        user.setId(0);
        user.setUsername("test");
        user.setPassword("testPassword");
        user.setCart(null);

        Item item = new Item();
        item.setId(0L);
        item.setName("testItem");
        item.setPrice(java.math.BigDecimal.valueOf(1.99));
        item.setDescription("testDescription");
        List<Item> items = new ArrayList<>();
        items.add(item);

        Cart cart = new Cart();
        cart.setId(0L);
        cart.setItems(items);
        cart.setTotal(java.math.BigDecimal.valueOf(1.99));
        cart.setUser(user);

        user.setCart(cart);

        when(userRepository.findByUsername("test")).thenReturn(user);

        ResponseEntity<UserOrder> response = orderController.submit("test");
        assert(response.getStatusCodeValue() == 200);

        UserOrder order = response.getBody();
        assert(order != null);
        assert(order.getItems().size() == 1);
        assert(order.getTotal().equals(java.math.BigDecimal.valueOf(1.99)));
        assert(order.getUser().equals(user));


    }
}
