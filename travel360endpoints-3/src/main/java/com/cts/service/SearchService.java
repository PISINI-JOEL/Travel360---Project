package com.cts.service;

import com.cts.enums.TravelPackageCategory;

public interface SearchService {

    Object search(String type,
                  String source,
                  String destination,
                  String city,
                  Double min,
                  Double max,
                  Integer ratings,
                  TravelPackageCategory category,
                  int page,
                  int size);
}