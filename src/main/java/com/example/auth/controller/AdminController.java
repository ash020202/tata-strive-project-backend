package com.example.auth.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.auth.dto.ApiResponse;
import com.example.auth.model.ContactForm;
import com.example.auth.model.FoodItem;
import com.example.auth.model.Order;
import com.example.auth.model.Reservation;
import com.example.auth.repository.FoodItemRepository;
import com.example.auth.repository.OrderRepository;
import com.example.auth.service.ContactFormService;
import com.example.auth.service.ReservationService;
// @CrossOrigin(origins = "http://localhost:5173")
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/admin")
public class AdminController {

	@Autowired
    private OrderRepository orderRepository;

	 @Autowired
	    private FoodItemRepository foodItemRepository;


	@Autowired
	private ReservationService reservationService;

    @Autowired
	 private ContactFormService contactFormService;


        @PreAuthorize("hasRole('ROLE_ADMIN')")
        //admin : get all-food items
        @GetMapping("/food-items")
        public ResponseEntity<?> getAllFoodItems() {
            List<FoodItem> foodItems = foodItemRepository.findAll();
            return ResponseEntity.ok(new ApiResponse<List<FoodItem>>("Food items retrieved successfully", foodItems));
        }

	    // Admin: Get all orders
	    @PreAuthorize("hasRole('ROLE_ADMIN')")
	    @GetMapping("/orders")
	    public ResponseEntity<?> getAllOrders() {
	        List<Order> orders = orderRepository.findAll();

	        if (orders.isEmpty()) {
	            return ResponseEntity.status(404).body("No orders found");
	        }

	        return ResponseEntity.ok(orders);
	    }


	
// Admin: Add new food item
	 @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/add-food")
    public ResponseEntity<?> addFoodItem(@RequestBody FoodItem foodItem) {
        FoodItem savedFoodItem = foodItemRepository.save(foodItem);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<FoodItem>("Food item added successfully", savedFoodItem));
    }

	 @PreAuthorize("hasRole('ROLE_ADMIN')")
    // Admin: Update food item
    @PutMapping("/{id}")
    public FoodItem updateFoodItem(@PathVariable Long id, @RequestBody FoodItem updatedFoodItem) {
        return foodItemRepository.findById(id).map(foodItem -> {
            foodItem.setName(updatedFoodItem.getName());
            foodItem.setPrice(updatedFoodItem.getPrice());
            foodItem.setDescription(updatedFoodItem.getDescription());
            foodItem.setCategory(updatedFoodItem.getCategory());
            foodItem.setImage(updatedFoodItem.getImage());
            foodItem.setAvailable(updatedFoodItem.getisAvailable());
            return foodItemRepository.save(foodItem);
        }).orElseThrow(() -> new RuntimeException("Food item not found"));
    }
        //Admin: delete food item
        @PreAuthorize("hasRole('ROLE_ADMIN')")
        @DeleteMapping("/delete/{id}")
        public ResponseEntity<String> deleteFoodItem(@PathVariable Long id) {
            foodItemRepository.deleteById(id);
            return ResponseEntity.ok("Food item deleted successfully");
        }


        // Admin: Change order status
        @PreAuthorize("hasRole('ROLE_ADMIN')")
        @PutMapping("/{id}/status")
        public ResponseEntity<?> updateOrderStatus(@PathVariable Long id, @RequestParam String status) {
            Optional<Order> order = orderRepository.findById(id);

            if (order.isPresent()) {
                order.get().setStatus(status);
                Order updatedOrder = orderRepository.save(order.get());
                return ResponseEntity.ok(updatedOrder);
            } else {
                return ResponseEntity.status(404).body("Order not found");
            }
        }

   
        // Admin: Get all reservations
        @GetMapping("/get-all-reservations")
        public ResponseEntity<List<Reservation>> getAllReservations() {
            List<Reservation> reservations = reservationService.getAllReservations();
            return ResponseEntity.ok(reservations);
        }

        // Admin: Get all contact forms
        @GetMapping("/get-contact-forms")
        public ResponseEntity<List<ContactForm>> getAllContactForms() {
            List<ContactForm> contactForms = contactFormService.getAllContactForms();
            return ResponseEntity.ok(contactForms);
        }
}

