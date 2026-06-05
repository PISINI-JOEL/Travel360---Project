package com.cts.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cts.dto.AuditLogResponseDTO;
import com.cts.enums.AuditEntity;
import com.cts.enums.LogType;
import com.cts.service.AuditLogService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/AuditLogs")
@AllArgsConstructor
@Slf4j
public class AuditLogController {
	
	private AuditLogService auditservice;
	
	@GetMapping("/all")
	public ResponseEntity<?> getAll(){
		List<AuditLogResponseDTO> list1 = auditservice.getAllLogs();
		return new ResponseEntity<>(list1, HttpStatus.OK);
		
	}
	
	@GetMapping("/entity/{entityType}/{entityId}")
	public ResponseEntity<List<AuditLogResponseDTO>> getByEntity(
	        @PathVariable AuditEntity entityType,
	        @PathVariable Long entityId) {

	    List<AuditLogResponseDTO> list = auditservice.getByEntity(entityType, entityId);
	    return new ResponseEntity<>(list, HttpStatus.OK);
	}
	
	

	
	    @GetMapping("/user/{userId}")
	    public ResponseEntity<List<AuditLogResponseDTO>> getByUser(@PathVariable Long userId) {

	        List<AuditLogResponseDTO> list = auditservice.getByUser(userId);
	        return new ResponseEntity<>(list, HttpStatus.OK);
	    }

	
	    @GetMapping("/action/{action}")
	    public ResponseEntity<List<AuditLogResponseDTO>> getByAction(@PathVariable String action) {

	        List<AuditLogResponseDTO> list = auditservice.getByAction(action);
	        return new ResponseEntity<>(list, HttpStatus.OK);
	    }

	    
	    @GetMapping("/type/{logType}")
	    public ResponseEntity<List<AuditLogResponseDTO>> getByLogType(@PathVariable LogType logType) {

	        List<AuditLogResponseDTO> list = auditservice.getByLogType(logType);
	        return new ResponseEntity<>(list, HttpStatus.OK);
	    }

	
	

}
