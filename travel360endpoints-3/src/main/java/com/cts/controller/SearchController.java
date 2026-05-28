package com.cts.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cts.service.SearchService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/search")
@AllArgsConstructor
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
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Object result = searchService.search(
                type, source, destination, city, min, max, ratings, page, size
        );

        return new ResponseEntity<>(result, HttpStatus.OK); 
    }
}