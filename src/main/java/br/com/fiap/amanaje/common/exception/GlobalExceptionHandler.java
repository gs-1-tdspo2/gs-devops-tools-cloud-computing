package br.com.fiap.amanaje.common.exception;

import java.time.Instant;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiErrorResponse> handleValidation(
			MethodArgumentNotValidException exception,
			HttpServletRequest request) {
		String message = exception.getBindingResult().getFieldErrors().stream()
				.map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
				.collect(Collectors.joining("; "));

		return buildResponse(HttpStatus.BAD_REQUEST, message, request);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ApiErrorResponse> handleUnreadableMessage(
			HttpMessageNotReadableException exception,
			HttpServletRequest request) {
		return buildResponse(HttpStatus.BAD_REQUEST, "Corpo da requisição inválido", request);
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ApiErrorResponse> handleTypeMismatch(
			MethodArgumentTypeMismatchException exception,
			HttpServletRequest request) {
		return buildResponse(HttpStatus.BAD_REQUEST, "Parâmetro inválido: " + exception.getName(), request);
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ApiErrorResponse> handleResourceNotFound(
			ResourceNotFoundException exception,
			HttpServletRequest request) {
		return buildResponse(HttpStatus.NOT_FOUND, exception.getMessage(), request);
	}

	@ExceptionHandler(BusinessRuleException.class)
	public ResponseEntity<ApiErrorResponse> handleBusinessRule(
			BusinessRuleException exception,
			HttpServletRequest request) {
		return buildResponse(HttpStatus.BAD_REQUEST, exception.getMessage(), request);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ApiErrorResponse> handleIllegalArgument(
			IllegalArgumentException exception,
			HttpServletRequest request) {
		return buildResponse(HttpStatus.BAD_REQUEST, exception.getMessage(), request);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiErrorResponse> handleGenericException(
			Exception exception,
			HttpServletRequest request) {
		return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno inesperado", request);
	}

	private ResponseEntity<ApiErrorResponse> buildResponse(
			HttpStatus status,
			String message,
			HttpServletRequest request) {
		ApiErrorResponse response = new ApiErrorResponse(
				Instant.now(),
				status.value(),
				status.getReasonPhrase(),
				message,
				request.getRequestURI());

		return ResponseEntity.status(status).body(response);
	}

}
