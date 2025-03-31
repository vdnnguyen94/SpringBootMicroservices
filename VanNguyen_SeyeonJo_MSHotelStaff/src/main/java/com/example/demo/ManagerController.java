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
import org.springframework.web.server.ResponseStatusException;
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
    
    @GetMapping("/hotels/lowrate")
    public ResponseEntity<List<Hotel>> getLowRatedHotels() {
        List<Hotel> hotels = hotelRepository.findByStarRatingLessThanEqual(3);
        return ResponseEntity.ok(hotels);
    }
    @GetMapping("/hotels/highrate")
    public ResponseEntity<List<Hotel>> getHighRatedHotels() {
        List<Hotel> hotels = hotelRepository.findByStarRatingGreaterThanEqual(4);
        return ResponseEntity.ok(hotels);
    }

    //list Staffs
    @GetMapping("/staff")
    public List<Staff> listStaff() {
        return staffRepository.findAll();
    }
    // staffs by Department
    @GetMapping("/staffSorted")
    public ResponseEntity<List<Staff>> getStaffSortedSimple() {
        List<Staff> staffList = staffRepository.findAllSortedByDepartment();
        return ResponseEntity.ok(staffList);
    }

    @PostMapping("/staff")
    public ResponseEntity<Staff> createStaff(@RequestBody Staff staff) {
        Staff saved = staffRepository.save(staff);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
    @GetMapping("/staff/{staffId}")
    public ResponseEntity<Staff> getStaffById(@PathVariable Integer staffId) {
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Staff not found"));

        return ResponseEntity.ok(staff); 
    }
    @DeleteMapping("/staff/{staffId}")
    public ResponseEntity<Staff> deleteStaff(@PathVariable Integer staffId) {
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Staff not found"));

        staffRepository.delete(staff);
        return ResponseEntity.ok(staff); 
    }

    @PutMapping("/staff/{staffId}/hotel/{hotelId}/assign")
    public ResponseEntity<String> assignHotelToStaff(@PathVariable Integer staffId, @PathVariable String hotelId) {
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Staff not found"));

        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Hotel not found"));

        int staffRating = staff.getPerformanceRating();
        int hotelRating = hotel.getStarRating(); 

        // ✳️ Validation: both should be in same rating group
        boolean bothLow = staffRating >= 1 && staffRating <= 3 && hotelRating >= 1 && hotelRating <= 3;
        boolean bothHigh = staffRating >= 4 && staffRating <= 5 && hotelRating >= 4 && hotelRating <= 5;

        if (!(bothLow || bothHigh)) {
            throw new RuntimeException("ERROR: MISMATCH PERFORMANCE");
        }

        staff.setHotel(hotel);
        staffRepository.save(staff);
        return ResponseEntity.ok("Hotel assigned to staff.");
    }

    @PutMapping("/staff/{staffId}/unassign")
    public ResponseEntity<String> unassignHotelFromStaff(@PathVariable Integer staffId) {
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Staff not found"));

        if (staff.getHotel() == null) {
            return ResponseEntity.badRequest().body("Staff is not currently assigned to any hotel.");
        }

        staff.setHotel(null);
        staffRepository.save(staff);
        return ResponseEntity.ok("Hotel unassigned from staff.");
    }

    //IMPORTANT LOGIC CHANGE PERFORMANCE please read README
    @PutMapping("/staff/{staffId}/performance")
    public ResponseEntity<HotelAssignmentChangeResponse> updatePerformance(
            @PathVariable Integer staffId,
            @RequestParam int newRating) {

        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Staff not found"));

        int currentRating = staff.getPerformanceRating();

        // Check if crossing between rating groups
        boolean isFromLowToHigh = (currentRating <= 3 && newRating >= 4);
        boolean isFromHighToLow = (currentRating >= 4 && newRating <= 3);

        boolean requiresHotelUpdate = isFromLowToHigh || isFromHighToLow;

        if (requiresHotelUpdate) {
            staff.setHotel(null); // unassign hotel
        }

        staff.setPerformanceRating(newRating);
        staffRepository.save(staff);

        return ResponseEntity.ok(new HotelAssignmentChangeResponse(requiresHotelUpdate));
    }


}
