package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    private UserController userController;

    private UserRepository userRepository = mock(UserRepository.class);

    private CartRepository cartRepository = mock(CartRepository.class);

    private BCryptPasswordEncoder bCryptPasswordEncoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setUp() {
        userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository", userRepository);
        TestUtils.injectObjects(userController, "cartRepository", cartRepository);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", bCryptPasswordEncoder);
    }

    @Test
    public void create_user_happy_path() throws Exception {
        when(bCryptPasswordEncoder.encode("testPassword")).thenReturn("thisIsHashed");
        CreateUserRequest newCreateUserRequest = new CreateUserRequest();
        newCreateUserRequest.setUsername("test");
        newCreateUserRequest.setPassword("testPassword");
        newCreateUserRequest.setConfirmPassword("testPassword");

        final ResponseEntity<User> response = userController.createUser(newCreateUserRequest);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User user = response.getBody();
        assertNotNull(user);
        assertEquals(0, user.getId());

        assertEquals("test", user.getUsername());
        assertEquals("thisIsHashed", user.getPassword());
    }

    @Test
    public void failed_create_user_missing_password(){
        CreateUserRequest newCreateUserRequest = new CreateUserRequest();
        newCreateUserRequest.setUsername("test");

        final ResponseEntity<User> response = userController.createUser(newCreateUserRequest);

        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());

    }

    @Test
    public void failed_create_user_short_password(){
        CreateUserRequest newCreateUserRequest = new CreateUserRequest();
        newCreateUserRequest.setUsername("test");
        newCreateUserRequest.setPassword("short");
        newCreateUserRequest.setConfirmPassword("short");

        final ResponseEntity<User> response = userController.createUser(newCreateUserRequest);

        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());

    }

    @Test
    public void failed_create_user_missing_confirmPassword(){
        CreateUserRequest newCreateUserRequest = new CreateUserRequest();
        newCreateUserRequest.setUsername("test");
        newCreateUserRequest.setPassword("testPassword");

        final ResponseEntity<User> response = userController.createUser(newCreateUserRequest);

        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());

    }

}
