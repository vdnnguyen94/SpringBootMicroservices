package com.example.demo;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface StaffRepository extends JpaRepository<Staff, Integer>  {
	@Query("SELECT s FROM Staff s ORDER BY " +
		       "CASE s.department " +
		       "WHEN 'Reception' THEN 1 " +
		       "WHEN 'Cleaning' THEN 2 " +
		       "WHEN 'Management' THEN 3 " +
		       "WHEN 'Restaurant' THEN 4 " +
		       "ELSE 5 END")
	List<Staff> findAllSortedByDepartment();


}
