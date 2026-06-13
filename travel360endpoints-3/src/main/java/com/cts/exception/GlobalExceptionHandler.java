package com.cts.exception;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex){
		Map<String,String> map = new LinkedHashMap<>();
//
//		List<FieldError> errors=  ex.getFieldErrors();
//		for(FieldError error:errors) {
//			map.put(error.getField(), error.getDefaultMessage());
//		}
		ex.getFieldErrors().forEach(error->map.put(error.getField(), error.getDefaultMessage()));
		log.warn("Validation failed: {}", map);
		return new ResponseEntity<>(map.toString(), HttpStatus.CONFLICT);
	}


    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUser(UserNotFoundException ex) {
        log.warn("User not found: {}", ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(FlightNotFoundException.class)
    public ResponseEntity<String> handleFlight(FlightNotFoundException ex) {
        log.warn("Flight not found: {}", ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(HotelNotFoundException.class)
    public ResponseEntity<String> handleHotel(HotelNotFoundException ex) {
        log.warn("Hotel not found: {}", ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(PackageNotFoundException.class)
    public ResponseEntity<String> handlePackage(PackageNotFoundException ex) {
        log.warn("Package not found: {}", ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(TransportNotFoundException.class)
    public ResponseEntity<String> handleTransport(TransportNotFoundException ex) {
        log.warn("Transport not found: {}", ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(InvoiceNotFoundException.class)
    public ResponseEntity<String> handleInvoice(InvoiceNotFoundException ex) {
        log.warn("Invoice not found: {}", ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(PaymentNotFoundException.class)
    public ResponseEntity<String> handlePayment(PaymentNotFoundException ex) {
        log.warn("Payment not found: {}", ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(InvalidBookingException.class)
    public ResponseEntity<String> handleInvalid(InvalidBookingException ex) {
        log.warn("Invalid booking: {}", ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(InsufficientAvailabilityException.class)
    public ResponseEntity<String> handleAvailability(InsufficientAvailabilityException ex) {
        log.warn("Insufficient availability: {}", ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(PartnerNotFoundException.class)
    public ResponseEntity<String> handlePartner(PartnerNotFoundException ex) {
        log.warn("Partner not found: {}", ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(InvalidPartnerException.class)
    public ResponseEntity<String> handleInvalidPartner(InvalidPartnerException ex) {
        log.warn("Invalid partner: {}", ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleConstraintViolation(ConstraintViolationException ex) {
        log.warn("Constraint violation: {}", ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Illegal argument: {}", ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDenied(AccessDeniedException ex) {
        log.warn("Access denied: {}", ex.getMessage());
        return new ResponseEntity<>("Access denied: you do not have permission to perform this action",
                HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFound(ResourceNotFoundException ex) {
        log.warn("Resource not found: {}", ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneric(Exception ex) {
        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(PackageItineraryNotFound.class)
	public ResponseEntity<?> handleItineraryNotFoundException(PackageItineraryNotFound ex) {
		log.warn("Package itinerary not found: {}", ex.getMessage());
		return new ResponseEntity<>(ex.getMessage(),HttpStatus.CONFLICT);
	}
}

