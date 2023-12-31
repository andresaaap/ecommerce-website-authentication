package com.example.demo.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/user")
public class UserController {

	private final static Logger logger = LoggerFactory.getLogger(UserController.class);
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@GetMapping("/id/{id}")
	public ResponseEntity<User> findById(@PathVariable Long id) {
		return ResponseEntity.of(userRepository.findById(id));
	}
	
	@GetMapping("/{username}")
	public ResponseEntity<User> findByUserName(@PathVariable String username) {
		User user = userRepository.findByUsername(username);
		return user == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(user);
	}
	
	@PostMapping("/create")
	public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {
		User user = new User();
		user.setUsername(createUserRequest.getUsername());
		// If password if different from null, then set it to the user's password
		if (createUserRequest.getPassword() == null) {
			logger.error("The password was not included, therefore the user {} was not created", createUserRequest.getUsername());
			// Return a bad request with the message "The password was not included"
			return ResponseEntity.badRequest().build();
		}

		if(createUserRequest.getPassword().length() < 7){
			logger.error("The password is too short, therefore the user {} was not created", createUserRequest.getUsername());
			// Return a bad request with the message "The password is too short"
			return ResponseEntity.badRequest().build();
		}

		if(!createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword())){
			logger.error("The password and confirm password do not match, therefore the user {} was not created", createUserRequest.getUsername());
			// Return a bad request with the message "The password and confirm password do not match"
			return ResponseEntity.badRequest().build();
		}

		user.setPassword(bCryptPasswordEncoder.encode(createUserRequest.getPassword()));
		Cart cart = new Cart();
		try {
			cartRepository.save(cart);
			user.setCart(cart);
			userRepository.save(user);
			logger.info("User created successfully: {}", user.getUsername());
			//log user password
			logger.info("User password: {}", user.getPassword());

		} catch (Exception e) {
			logger.error(e.getMessage());
			// Return a server error with the message "There was an error creating the user"
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}

		return ResponseEntity.ok(user);
	}
	
}
