package com.cts.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.cts.enums.TravelPackageCategory;
import com.cts.service.SearchService;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/search")
@AllArgsConstructor
@Validated
@Slf4j
public class SearchController {

    private final SearchService searchService;

    @GetMapping
    public ResponseEntity<?> search(
            @RequestParam String type,
            @RequestParam(required = false) String source,
            @RequestParam(required = false) String destination,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Double min,
            @RequestParam(required = false) Double max,
            @RequestParam(required = false) Integer ratings,
            @RequestParam(required = false) TravelPackageCategory category,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "5") @Min(1) @Max(100) int size) {

        log.info("Search request received: type={}, source={}, destination={}, city={}, minPrice={}, maxPrice={}, ratings={}, category={}, page={}, size={}",
                type, source, destination, city, min, max, ratings, category, page, size);

        Object result = searchService.search(
                type, source, destination, city, min, max, ratings, category, page, size
        );

        log.info("Search executed successfully for type={}", type);

        if (result instanceof java.util.List<?>) {
            log.info("Search returned {} results", ((java.util.List<?>) result).size());
        } else {
            log.debug("Search response type: {}", result.getClass().getSimpleName());
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}