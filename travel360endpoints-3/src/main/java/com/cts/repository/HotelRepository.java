package com.cts.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.cts.entity.Hotel;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {
	List<Hotel> findByCity(String city);

	List<Hotel> findByCityAndPriceBetween(String city, double minPrice, double maxPrice);

	@Query("""
		    SELECT h FROM Hotel h
		    WHERE (:location IS NULL OR h.city = :location)
		    AND (:ratings IS NULL OR h.ratings = :ratings)
		    AND (:minPrice IS NULL OR h.price >= :minPrice)
		    AND (:maxPrice IS NULL OR h.price <= :maxPrice)
		""")
		List<Hotel> filterHotels(String location, Integer ratings, Double minPrice, Double maxPrice);

	

}
