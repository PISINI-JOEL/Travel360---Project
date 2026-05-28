package com.cts.exception;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
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
		return new ResponseEntity<>(map.toString(), HttpStatus.CONFLICT);
	}

   
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUser(UserNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    
    @ExceptionHandler(FlightNotFoundException.class)
    public ResponseEntity<String> handleFlight(FlightNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

   
    @ExceptionHandler(HotelNotFoundException.class)
    public ResponseEntity<String> handleHotel(HotelNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    
    @ExceptionHandler(PackageNotFoundException.class)
    public ResponseEntity<String> handlePackage(PackageNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

   
    @ExceptionHandler(TransportNotFoundException.class)
    public ResponseEntity<String> handleTransport(TransportNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

   
    @ExceptionHandler(InvoiceNotFoundException.class)
    public ResponseEntity<String> handleInvoice(InvoiceNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

   
    @ExceptionHandler(PaymentNotFoundException.class)
    public ResponseEntity<String> handlePayment(PaymentNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    
    @ExceptionHandler(InvalidBookingException.class)
    public ResponseEntity<String> handleInvalid(InvalidBookingException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

   
    @ExceptionHandler(InsufficientAvailabilityException.class)
    public ResponseEntity<String> handleAvailability(InsufficientAvailabilityException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

   
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneric(Exception ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(PackageItineraryNotFound.class)
	public ResponseEntity<?> handleItineraryNotFoundException(PackageItineraryNotFound ex) {
		return new ResponseEntity<>(ex.getMessage(),HttpStatus.CONFLICT);
	}
}

