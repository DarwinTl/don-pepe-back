package com.tienda.controllers;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import com.tienda.entities.DataPayment;
import com.tienda.services.PaypalService;

@RestController
@CrossOrigin(origins = "hhtp:/lacalhost:4200")
@RequestMapping("/api/v1/payments")
public class PaypalController {

	private final PaypalService paypalService;

	private final String SUCCESS_URL = "http:localhost:8080/api/v1/payments/success";
	private final String CANCEL_URL = "http:localhost:8080/api/v1/payments/cancel";

	public PaypalService getPaypalService() {
		return paypalService;
	}

	public PaypalController(PaypalService paypalService) {
		this.paypalService = paypalService;
	}

	@PostMapping
	public String createPayment(@RequestBody DataPayment dataPayment)
			throws NumberFormatException, PayPalRESTException {
		Payment payment = paypalService.createPayment(Double.valueOf(dataPayment.getAmount()),
				dataPayment.getCurrency(), dataPayment.getMethod(), "SALE", dataPayment.getDescription(), CANCEL_URL,
				SUCCESS_URL);

		for (Links links : payment.getLinks()) {
			if (links.getRel().equals("approval_url")) {
				return links.getHref();
			}
		}

		return "";
	}

	@GetMapping("/success")
	public RedirectView paymentSuccess(@RequestParam("paymentId") String paymentId,
			@RequestParam("payerId") String payerId) throws PayPalRESTException {

		Payment payment = paypalService.executePayment(paymentId, payerId);
		if (payment.getState().equals("approved")) {
			// esta sera la ruta en el fron para el success
			return new RedirectView("http:localhost:4200/payment/success");
		}

		return null;
	}

	@GetMapping("/cancel")
	public RedirectView paymentCancel() {
		return new RedirectView("http:localhost:4200");
	}
}
