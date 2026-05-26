package com.cts.service;

import com.cts.dto.PaymentDTO;
import com.cts.dto.PaymentResponseDTO;
import com.cts.entity.Payment;

import java.util.List;

public interface PaymentService {

	PaymentResponseDTO makePayment(PaymentDTO dto);

	List<PaymentResponseDTO> getPaymentsByInvoice(Long invoiceId);

	List<PaymentResponseDTO> getAllPayments();

	PaymentResponseDTO getPaymentById(Long id);

}
