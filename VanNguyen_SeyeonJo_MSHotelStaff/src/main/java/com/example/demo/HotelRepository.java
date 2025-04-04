package com.example.demo;


import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface HotelRepository extends JpaRepository<Hotel, String> {
	List<Hotel> findByStarRatingLessThanEqual(int rating);
	List<Hotel> findByStarRatingGreaterThanEqual(int rating);


}