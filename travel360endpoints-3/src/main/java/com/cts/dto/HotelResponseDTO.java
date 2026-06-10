package com.cts.dto;

import com.cts.enums.HotelStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HotelResponseDTO {

    private Long hotelId;
    private String hotelName;
    private int ratings;
    private String city;
    private String address;
    private double price;
    private String contactNo;
    private String emailId;
    private HotelStatus status;
    private int totalRooms;
}
