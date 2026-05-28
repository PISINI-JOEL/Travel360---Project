package com.cts.service;

import lombok.Data;


public interface SearchService {

	Object search(String type, String source, String destination, String city, Double min, Double max, Integer ratings);

}
