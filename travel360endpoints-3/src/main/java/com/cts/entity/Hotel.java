package com.cts.entity;

import com.cts.enums.HotelStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "HOTEL")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long hotelId;

    private String hotelName;
    
    private int ratings;
    private String city;
    private String address;
    private double price;
    private String contactNo;
    private String emailId;
    @Enumerated(EnumType.STRING)
    private HotelStatus status;
    private int totalRooms;
    
    @ManyToOne
    @JoinColumn(name = "partner_id")
    private Partner partner;
    

    
    
     
    
}