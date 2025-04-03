package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.core.ParameterizedTypeReference;

import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;

import java.net.URI;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ManagerController {
	
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private UserRepository userRepository;    
	@Autowired
	DiscoveryClient discoveryClient;	
    private URI apiURI;

    // Initializes the URI of the external service instance on application startup
    @PostConstruct
    public void init() {
        List<ServiceInstance> instances = discoveryClient.getInstances("VanNguyen_SeyeonJo_MSHotelStaff");
        if (instances != null && !instances.isEmpty()) {
        	apiURI = instances.get(0).getUri();
        } else {
            throw new RuntimeException("VanNguyen_SeyeonJo_MSHotelStaff is not available.");
        }
    }
    

    // Creates default admin and staff accounts if they don't already exist
    @GetMapping("/setupusers")
    public String createDefaultUsers(RedirectAttributes redirectAttributes) {

        boolean adminExists = userRepository.findByUsername("admin1") != null;
        boolean staffExists = userRepository.findByUsername("staff1") != null;

        if (adminExists || staffExists) {
            System.out.println("Default users already exist. Seeding skipped.");
            redirectAttributes.addFlashAttribute("errorMessage", "Users already exist.");
            return "redirect:/";
        }

        User admin = User.createWithEncodedPassword(
            "admin1", "qwe123", "Van Nguyen", "Admin", passwordEncoder);

        User staff = User.createWithEncodedPassword(
            "staff1", "qwe123", "Staff Testing", "Staff", passwordEncoder);

        userRepository.save(admin);
        userRepository.save(staff);

        System.out.println("✅ Default users created successfully.");
        redirectAttributes.addFlashAttribute("successMessage", "Default users created successfully.");

        return "redirect:/";
    }
     
    // Returns the login page
	@GetMapping("/login")
	public String loginGET()
	{
		return "login";
	}
	
    // Displays the admin dashboard with a list of hotels and staff
	@RequestMapping("/")
    public String home(Model model)
    {
	    Hotel[] hotels = restTemplate.getForObject(apiURI + "/api/hotels", Hotel[].class);
	    model.addAttribute("hotels", hotels);

	    // Fetch Staff
	    //Staff[] staff = restTemplate.getForObject(apiURI + "/api/staff", Staff[].class);
	    Staff[] staff = restTemplate.getForObject(apiURI + "/api/staffSorted", Staff[].class);
	    model.addAttribute("staffList", staff);

	    return "home"; 
    } 

    // Retrieves and displays details of a specific hotel
	@GetMapping("/admin/hotel/{hotelId}")
	public String viewHotel(@PathVariable String hotelId, Model model) {
	    String url = apiURI + "/api/hotels/" + hotelId; 
	    ResponseEntity<Hotel> response = restTemplate.getForEntity(url, Hotel.class);

	    if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
	        model.addAttribute("hotel", response.getBody());
	        return "hotelDetails";
	    } else {
	        model.addAttribute("error", "Hotel not found");
	        return "errorPage"; 
	    }
	}

    // Retrieves and displays details of a specific staff member
	@GetMapping("admin/staff/{staffId}")
	public String viewStaff(@PathVariable Integer staffId, Model model) {
	    Staff staff = restTemplate.getForObject(apiURI + "/api/staff/{staffId}", Staff.class, staffId);
	    model.addAttribute("staff", staff);
	    
	    List<Hotel> hotels = Arrays.asList(
	        restTemplate.getForObject(apiURI + "/api/hotels", Hotel[].class)
	    );
	    model.addAttribute("hotels", hotels);

	    return "staffDetails";
	}
	
    // Deletes a staff member and redirects with a success message
    @PostMapping("/staff/delete/{staffId}")
    public String deleteStaffViaPost(@PathVariable Integer staffId, RedirectAttributes redirectAttributes) {
        restTemplate.delete(apiURI + "/api/staff/" + staffId);
        redirectAttributes.addFlashAttribute("successMessage", "Staff deleted successfully.");
        return "redirect:/";
    }
    
    // Returns the performance review form for a staff member
    @GetMapping("admin/staff/edit/{staffId}")
    public String staffPerformanceForm(@PathVariable Integer staffId, Model model) {
        Staff staff = restTemplate.getForObject(apiURI + "/api/staff/{staffId}", Staff.class, staffId);
        model.addAttribute("staff", staff);
        return "performanceReview";
    }
    
    // Updates the performance rating of a staff member and handles hotel reassignment if needed
    @PostMapping("admin/staff/{staffId}/updatePerformance")
    public String updatePerformance(@PathVariable Integer staffId, @RequestParam int newRating, RedirectAttributes redirectAttributes) {
        if (newRating < 1 || newRating > 5) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid performance rating. Must be between 1 and 5.");
            return "redirect:/admin/staff/edit/" + staffId;
        }
        
        try {
        	
            // Send request to update performance
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
            
            ResponseEntity<HotelAssignmentChangeResponse> responseEntity =
                    restTemplate.exchange(
                        apiURI + "/api/staff/" + staffId + "/performance?newRating=" + newRating,
                        HttpMethod.PUT,
                        requestEntity,
                        HotelAssignmentChangeResponse.class
                    );
            HotelAssignmentChangeResponse response = responseEntity.getBody();
            System.out.println("RESPONSE requiresHotelUpdate: " + response.isRequiresHotelUpdate());


            if (response != null && response.isRequiresHotelUpdate()) {
                redirectAttributes.addFlashAttribute("successMessage",
                    "Performance successfully updated. Hotel has been removed from staff due to mismatch. Please assign hotel according to new rating.");
            } else {
                redirectAttributes.addFlashAttribute("successMessage",
                    "Performance successfully updated. No change in hotel assignment.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update performance.");
            return "redirect:/admin/staff/edit/" + staffId;
        }

        return "redirect:/admin/staff/" + staffId;
    }
    
    // Returns the performance review form for a staff member
    @GetMapping("admin/staff/assign/{staffId}")
    public String showAssignForm(@PathVariable Integer staffId, Model model) {
        Staff staff = restTemplate.getForObject(apiURI + "/api/staff/{staffId}", Staff.class, staffId);
        
        List<Hotel> hotels = restTemplate.exchange(
            apiURI + "/api/hotels",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<Hotel>>() {}
        ).getBody();

        model.addAttribute("staff", staff);
        model.addAttribute("hotels", hotels); 

        return "assignHotel";
    }
    
    // Assigns a hotel to a staff member
    @PostMapping("admin/staff/{staffId}/assignHotel")
    public String assignHotelToStaff(@PathVariable Integer staffId, 
                                     @RequestParam String hotelId, 
                                     RedirectAttributes redirectAttributes) {
        try {
            ResponseEntity<Staff> staffResponse = restTemplate.getForEntity(apiURI + "/api/staff/" + staffId, Staff.class);
            Staff staff = staffResponse.getBody();
            ResponseEntity<Hotel> hotelResponse = restTemplate.getForEntity(apiURI + "/api/hotels/" + hotelId, Hotel.class);
            Hotel hotel = hotelResponse.getBody();

            int staffRating = staff.getPerformanceRating();
            int hotelRating = hotel.getStarRating();

            boolean bothLow = staffRating >= 1 && staffRating <= 3 && hotelRating >= 1 && hotelRating <= 3;
            boolean bothHigh = staffRating >= 4 && staffRating <= 5 && hotelRating >= 4 && hotelRating <= 5;

            if (!(bothLow || bothHigh)) {
                redirectAttributes.addFlashAttribute("errorMessage", "ERROR: MISMATCH PERFORMANCE");
                return "redirect:/admin/staff/" + staffId;
            }

            String url = apiURI + "/api/staff/" + staffId + "/hotel/" + hotelId + "/assign"; 
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.POST, entity, String.class
            );
            redirectAttributes.addFlashAttribute("successMessage", response.getBody());

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
        }

        return "redirect:/admin/staff/" + staffId;
    }
    
    // Unassigns a hotel from a staff member
    @PostMapping("admin/staff/{staffId}/unassignHotel")
    public String unassignHotelFromStaff(@PathVariable Integer staffId, RedirectAttributes redirectAttributes) {
        try {
            restTemplate.put(apiURI + "/api/staff/{staffId}/unassign", null, staffId);
            redirectAttributes.addFlashAttribute("successMessage", "Hotel successfully unassigned from staff.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to unassign hotel from staff.");
        }
        return "redirect:/admin/dashboard";
    }

}
