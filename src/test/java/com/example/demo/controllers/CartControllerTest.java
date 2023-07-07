package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.Item;

import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateItemRequest;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {

    private CartController cartController;

    private UserController userController;

    private ItemController itemController;

    private UserRepository userRepo = mock(UserRepository.class);

    private CartRepository cartRepo = mock(CartRepository.class);

    private ItemRepository itemRepo = mock(ItemRepository.class);

    private BCryptPasswordEncoder bCryptPasswordEncoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setup(){
        // init controller
        cartController = new CartController();
        // inject mocks
        TestUtils.injectObjects(cartController, "userRepository", userRepo);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepo);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepo);

        userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository", userRepo);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", bCryptPasswordEncoder);
        TestUtils.injectObjects(userController, "userRepository", userRepo);
        TestUtils.injectObjects(userController, "cartRepository", cartRepo);

        itemController = new ItemController();
        TestUtils.injectObjects(itemController, "itemRepository", itemRepo);
    }

    // test add to cart happy path

    @Test
    public void add_to_cart_happy_path(){
        Item item = new Item();
        item.setId(1L);
        item.setName("testItem");
        item.setPrice(BigDecimal.valueOf(1.99));
        item.setDescription("testDescription");
        List<Item> items = new ArrayList<>();
        items.add(item);

        Cart newCart = new Cart();
        newCart.setId(1L);
        newCart.setItems(items);
        newCart.setTotal(BigDecimal.valueOf(1.99));

        User user = new User();
        user.setId(1L);
        user.setUsername("test");
        user.setPassword("testPassword");
        user.setCart(newCart);

        when(userRepo.findByUsername("test")).thenReturn(user);
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepo.findById(1L)).thenReturn(Optional.of(item));

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername(user.getUsername());
        request.setItemId(item.getId());
        request.setQuantity(1);

        ResponseEntity<Cart> response = cartController.addTocart(request);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        Cart cart = response.getBody();
        assertNotNull(cart);
        assertEquals(Optional.ofNullable(1L), Optional.ofNullable(cart.getId()));
        assertEquals(items, cart.getItems());
        // It is bad that the cart doesnt have a user, but the test passes
    }

    /**@Test
    public void add_to_cart_happy_pathv2(){
        // create a CreateUserRequest
        User user = new User();
        user.setId(1L);
        user.setUsername("test");
        user.setPassword("testPassword");

        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername(user.getUsername());
        createUserRequest.setPassword(user.getPassword());
        createUserRequest.setConfirmPassword(user.getPassword());

        // Create a new user using the user controller
        when(bCryptPasswordEncoder.encode("testPassword")).thenReturn("thisIsHashed");
        ResponseEntity<User> responseUser = userController.createUser(createUserRequest);

        // Create a new item using the item controller
        CreateItemRequest createItemRequest = new CreateItemRequest();
        createItemRequest.setName("testItem");
        createItemRequest.setPrice(BigDecimal.valueOf(1.99));
        createItemRequest.setDescription("testDescription");

        ResponseEntity<Item> responseItem = itemController.createItem(createItemRequest);

        // get item by name
        ResponseEntity<List<Item>> responseItems = itemController.getItemsByName("testItem");

        //print responseItems items size
        System.out.println(responseItems.getBody());

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername(user.getUsername());
        request.setItemId(responseItems.getBody().get(0).getId());
        request.setQuantity(1);

        ResponseEntity<Cart> response = cartController.addTocart(request);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        Cart cart = response.getBody();
        assertNotNull(cart);
        assertEquals(Optional.ofNullable(1L), Optional.ofNullable(cart.getId()));
    }
    */

    // test add to cart user not found

    @Test
    public void add_to_cart_user_not_found(){
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("test");
        request.setItemId(1L);
        request.setQuantity(1);

        when(userRepo.findByUsername("test")).thenReturn(null);

        ResponseEntity<Cart> response = cartController.addTocart(request);

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    // test add to cart item not found

    @Test
    public void add_to_cart_item_not_found(){
        User user = new User();
        user.setId(1L);
        user.setUsername("test");
        user.setPassword("testPassword");

        when(userRepo.findByUsername("test")).thenReturn(user);

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername(user.getUsername());
        request.setItemId(1L);
        request.setQuantity(1);

        when(itemRepo.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Cart> response = cartController.addTocart(request);

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    // test remove from cart happy path

    @Test
    public void remove_from_cart_happy_path(){
        Item item = new Item();
        item.setId(1L);
        item.setName("testItem");
        item.setPrice(BigDecimal.valueOf(1.99));
        item.setDescription("testDescription");
        List<Item> items = new ArrayList<>();
        items.add(item);

        Cart newCart = new Cart();
        newCart.setId(1L);
        newCart.setItems(items);
        newCart.setTotal(BigDecimal.valueOf(1.99));

        User user = new User();
        user.setId(1L);
        user.setUsername("test");
        user.setPassword("testPassword");
        user.setCart(newCart);

        when(userRepo.findByUsername("test")).thenReturn(user);
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepo.findById(1L)).thenReturn(Optional.of(item));

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername(user.getUsername());
        request.setItemId(item.getId());
        request.setQuantity(1);

        ResponseEntity<Cart> response = cartController.removeFromcart(request);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        Cart cart = response.getBody();
        assertNotNull(cart);
        assertEquals(Optional.ofNullable(1L), Optional.ofNullable(cart.getId()));
        assertEquals(items, cart.getItems());
        // It is bad that the cart doesnt have a user, but the test passes
    }

    // test remove from cart, user not found

    @Test
    public void remove_from_cart_user_not_found(){
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("test");
        request.setItemId(1L);
        request.setQuantity(1);

        when(userRepo.findByUsername("test")).thenReturn(null);

        ResponseEntity<Cart> response = cartController.removeFromcart(request);

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    // test remove from cart, item not found

    @Test
    public void remove_from_cart_item_not_found() {
        User user = new User();
        user.setId(1L);
        user.setUsername("test");
        user.setPassword("testPassword");

        when(userRepo.findByUsername("test")).thenReturn(user);

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername(user.getUsername());
        request.setItemId(1L);
        request.setQuantity(1);

        when(itemRepo.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Cart> response = cartController.removeFromcart(request);

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());

    }

}
