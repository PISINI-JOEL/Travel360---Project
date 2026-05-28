package com.cts.service;

public interface SearchService {

    Object search(String type,
                  String source,
                  String destination,
                  String city,
                  Double min,
                  Double max,
                  Integer ratings,
                  int page,
                  int size);
}