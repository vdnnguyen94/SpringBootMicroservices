package com.example.demo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ManagerController {
    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private StaffRepository staffRepository;
    
    private static final List<String> DEPARTMENTS = List.of(
            "Reception",
            "Cleaning",
            "Management",
            "Restaurant"
        );
    
    //list Hotels
    @GetMapping("/hotels")
    public List<Hotel> listHotels() {
        return hotelRepository.findAll();
    }
    //list Staffs
    @GetMapping("/staff")
    public List<Staff> listStaff() {
        return staffRepository.findAll();
    }
    
    @PostMapping("/staff")
    public ResponseEntity<Staff> createStaff(@RequestBody Staff staff) {
        staff.setHotel(null); // Make sure hotel is not assigned
        Staff saved = staffRepository.save(staff);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    

}
