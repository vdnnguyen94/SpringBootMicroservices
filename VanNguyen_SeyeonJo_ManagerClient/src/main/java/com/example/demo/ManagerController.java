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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.crypto.password.PasswordEncoder;

import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;

import java.net.URI;
import java.util.Arrays;
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

    @PostConstruct
    public void init() {
        List<ServiceInstance> instances = discoveryClient.getInstances("VanNguyen_SeyeonJo_MSHotelStaff");
        if (instances != null && !instances.isEmpty()) {
        	apiURI = instances.get(0).getUri();
        } else {
            throw new RuntimeException("VanNguyen_SeyeonJo_MSHotelStaff is not available.");
        }
    }
	@GetMapping("/")
	public String home(Model model)
	{
	    Hotel[] hotels = restTemplate.getForObject(apiURI + "/api/hotels", Hotel[].class);
	    model.addAttribute("hotels", hotels);

	    // Fetch Staff
	    //Staff[] staff = restTemplate.getForObject(apiURI + "/api/staff", Staff[].class);
	    Staff[] staffSorted = restTemplate.getForObject(apiURI + "/api/staffSorted", Staff[].class);
        model.addAttribute("staffList", staffSorted);
		return "home";
		
	}
    
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
    
	@GetMapping("/login")
	public String loginGET()
	{
		return "login";
	}
	
	@RequestMapping("/admin/dashboard")
    public String show(Model model)
    {
	    Hotel[] hotels = restTemplate.getForObject(apiURI + "/api/hotels", Hotel[].class);
	    model.addAttribute("hotels", hotels);

	    // Fetch Staff
	    Staff[] staff = restTemplate.getForObject(apiURI + "/api/staff", Staff[].class);
	    model.addAttribute("staffList", staff);

	    return "dashboard"; 
    } 

    // View individual Hotel details
    @GetMapping("admin/hotel/{hotelId}")
    public String viewHotel(@PathVariable String hotelId, Model model) {
        Hotel hotel = restTemplate.getForObject(apiURI + "/api/hotels/{hotelId}", Hotel.class, hotelId);
        model.addAttribute("hotel", hotel);
        return "hotelDetails";
    }

    // View individual Staff details
    @GetMapping("admin/staff/view/{staffId}")
    public String viewStaff(@PathVariable Integer staffId, Model model) {
        Staff staff = restTemplate.getForObject(apiURI + "/api/staff/{staffId}", Staff.class, staffId);
        model.addAttribute("staff", staff);
        return "staffDetails";
    }

    // Assign hotel to a staff
    @PostMapping("admin/staff/{staffId}/assignHotel/{hotelId}")
    public String assignHotelToStaff(@PathVariable Integer staffId, @PathVariable String hotelId, RedirectAttributes redirectAttributes) {
        try {
            restTemplate.put(apiURI + "/api/staff/{staffId}/hotel/{hotelId}/assign", null, staffId, hotelId);
            redirectAttributes.addFlashAttribute("successMessage", "Hotel successfully assigned to staff.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to assign hotel to staff.");
        }
        return "redirect:/admin/dashboard";
    }

    // Unassign hotel from a staff
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

    // Update performance of staff (with hotel reassignment if necessary)
    @PostMapping("admin/staff/{staffId}/updatePerformance")
    public String updatePerformance(@PathVariable Integer staffId, @RequestParam int newRating, RedirectAttributes redirectAttributes) {
        try {
            restTemplate.put(apiURI + "/api/staff/{staffId}/performance?newRating={newRating}", null, staffId, newRating);
            redirectAttributes.addFlashAttribute("successMessage", "Performance updated successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update performance.");
        }
        return "redirect:/admin/dashboard";
    }
}
