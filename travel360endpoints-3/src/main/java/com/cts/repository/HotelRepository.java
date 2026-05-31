package com.cts.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.cts.entity.Hotel;
import com.cts.entity.Partner;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {

	List<Hotel> findByPartner(Partner partner);

	Page<Hotel> findByCity(String city,Pageable pageable);

	List<Hotel> findByCityAndPriceBetween(String city, double minPrice, double maxPrice);

	@Query("""
		    SELECT h FROM Hotel h
		    WHERE (:location IS NULL OR h.city = :location)
		    AND (:ratings IS NULL OR h.ratings = :ratings)
		    AND (:minPrice IS NULL OR h.price >= :minPrice)
		    AND (:maxPrice IS NULL OR h.price <= :maxPrice)
		""")
		Page<Hotel> filterHotels(String location, Integer ratings, Double minPrice, Double maxPrice,Pageable pageable);

	

}
