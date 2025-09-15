package com.bekaku.api.spring.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

//@ControllerAdvice
@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ExceptionResolver {
    //    @ExceptionHandler(ApiException.class)
//    ErrorResponse handleApiException(ApiException e) {
//        return ErrorResponse.builder(e, e.getApiError().getStatus(), e.getMessage())
//                .title("ApiException")
//                .property("error", e.getApiError().getErrors())
//                .property("message", e.getApiError().getMessage())
//                .property("timestamp", Instant.now())
//                .build();
//    }
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiError> handleApiException(ApiException ex) {
        return new ResponseEntity<>(ex.getApiError(), ex.getApiError().getStatus());
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> handleAll(final Exception ex, final WebRequest request) {
        ex.printStackTrace();
        final ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, ex.getLocalizedMessage(), "error occurred");
        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }

    @ExceptionHandler({MissingPathVariableException.class})
    public ResponseEntity<Object> handleMissingPathVariableException(HttpServletRequest request, MissingPathVariableException e) {
        final ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, e.getLocalizedMessage(), "Required path variable is missing in this request. Please add it to your request.");
        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }

    @ExceptionHandler({NoHandlerFoundException.class})
    public ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex) {
        final String error = "No handler found for " + ex.getHttpMethod() + " " + ex.getRequestURL();
        final ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, ex.getLocalizedMessage(), error);
        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        final List<String> errors = new ArrayList<>();
        for (final FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        }
        for (final ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
        }
        final ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), errors);
        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }

    @ExceptionHandler({BindException.class})
    public ResponseEntity<Object> handleBindException(BindException ex) {
        final List<String> errors = new ArrayList<>();
        for (final FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        }
        for (final ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
        }
        final ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), errors);
        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }

    @ExceptionHandler({TypeMismatchException.class})
    public ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex) {
        final String error = ex.getValue() + " value for " + ex.getPropertyName() + " should be of type " + ex.getRequiredType();

        final ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), error);
        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }

    @ExceptionHandler({MissingServletRequestParameterException.class})
    public ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex) {
//        logger.info(ex.getClass().getName());
        final String error = ex.getParameterName() + " parameter is missing";
        final ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), error);
        return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    public ResponseEntity<Object> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        final String error = ex.getName() + " should be of type " + Objects.requireNonNull(ex.getRequiredType()).getName();
        final ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), error);
        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex) {
//        logger.info(ex.getClass().getName());
        final List<String> errors = new ArrayList<String>();
        for (final ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            errors.add(violation.getRootBeanClass().getName() + " " + violation.getPropertyPath() + ": " + violation.getMessage());
        }

        final ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), errors);
        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Object> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
//        logger.info(ex.getClass().getName());
        final StringBuilder builder = new StringBuilder();
        builder.append(ex.getMethod());
        builder.append(" method is not supported for this request. Supported methods are ");
        Objects.requireNonNull(ex.getSupportedHttpMethods()).forEach(t -> builder.append(t).append(" "));

        final ApiError apiError = new ApiError(HttpStatus.METHOD_NOT_ALLOWED, ex.getLocalizedMessage(), builder.toString());
        return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
    }
//    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
//    public ResponseEntity<Object> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
//        HttpHeaders headers = new HttpHeaders();
//        headers.setAllow(Objects.requireNonNull(ex.getSupportedHttpMethods()));
//        return new ResponseEntity<>(headers, HttpStatus.METHOD_NOT_ALLOWED);
//    }

    @ExceptionHandler({HttpMediaTypeNotSupportedException.class})
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex) {
//        logger.info(ex.getClass().getName());
        final StringBuilder builder = new StringBuilder();
        builder.append(ex.getContentType());
        builder.append(" media type is not supported. Supported media types are ");
        ex.getSupportedMediaTypes().forEach(t -> builder.append(t).append(" "));

        final ApiError apiError = new ApiError(HttpStatus.UNSUPPORTED_MEDIA_TYPE, ex.getLocalizedMessage(), builder.substring(0, builder.length() - 2));
        return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
    }

    @ExceptionHandler({MultipartException.class})
    public ResponseEntity<Object> handleMultipartFile(Exception ex) {
        final ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), ex.getCause().getMessage());
        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }

    // Handle ClientAbortException without trying to inject it as parameter
    @ExceptionHandler(ClientAbortException.class)
    public void handleClientAbortException(HttpServletRequest request) {
        log.info("Client aborted connection for: {}", request.getRequestURI());
        // Don't try to send response - client already disconnected
        // Just log it and return gracefully
    }

    //    @ExceptionHandler(IOException.class)
//    public ResponseEntity<Object> handleIOException(IOException ex) {
//        if (ex.getMessage() != null && (ex.getMessage().contains("aborted by the software") ||
//                (ex.getClass() != null && ex.getClass().getName().contains("ClientAbortException"))
//        )) {
//            return ResponseEntity.noContent().build();
//        }
//        final ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, ex.getLocalizedMessage(), ex.getCause().getMessage());
//        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
//    }
// Alternative: Handle it as IOException (ClientAbortException extends IOException)
    @ExceptionHandler(IOException.class)
    public void handleIOException(IOException ex, HttpServletRequest request, HttpServletResponse response) {
        String message = ex.getMessage();
        String uri = request.getRequestURI();

        // Check for client disconnect patterns
        if (message != null && (
                message.contains("Broken pipe") ||
                        message.contains("Connection reset") ||
                        message.contains("ClientAbortException") ||
                        ex.getClass().getSimpleName().contains("ClientAbort"))) {

//            log.info("Client disconnected during streaming for: {} - {}", uri, message);
            return; // Don't send error response for client disconnects
        }

        // Handle other IO exceptions
        if (!response.isCommitted()) {
            try {
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"I/O error occurred\"}");
                response.getWriter().flush();
            } catch (IOException writeEx) {
                log.warn("Could not write error response: {}", writeEx.getMessage());
            }
        } else {
//            log.warn("I/O exception after response committed for: {} - {}", uri, message);
        }
    }


    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Resource> handleMissingStaticResource(NoResourceFoundException ex) throws IOException {
        // Extract file extension from the requested path
        String requestedPath = ex.getMessage(); // Assume this contains the file path, like "img/sample.jpg"
        String extension = getExtension(requestedPath).toLowerCase();


        // Construct the default file name (e.g., "default.jpg", "default.pdf")
        String defaultFilePath = "static/default." + extension;

//        log.warn("handleMissingStaticResource > requestedPath:{}, extension:{} , defaultFilePath:{}",
//                requestedPath, extension, defaultFilePath);

        Resource defaultResource = new ClassPathResource(defaultFilePath);

        if (!defaultResource.exists()) {
            return ResponseEntity.notFound().build(); // Optional: fallback behavior if even default is missing
        }

        MediaType mediaType = getMediaTypeForExtension(extension);

        return ResponseEntity
                .ok()
                .contentType(mediaType)
                .body(defaultResource);
    }

    private String getExtension(String filename) {
        if (filename == null) return "";
        // Remove trailing dots
        filename = filename.trim().replaceAll("\\.+$", "");
        int dotIndex = filename.lastIndexOf('.');
        return (dotIndex == -1) ? "" : filename.substring(dotIndex + 1);
    }

    private MediaType getMediaTypeForExtension(String extension) {
        return switch (extension) {
            case "jpg", "jpeg", "png", "gif" -> MediaType.IMAGE_JPEG;
            case "pdf" -> MediaType.APPLICATION_PDF;
            default -> MediaType.APPLICATION_OCTET_STREAM;
        };
    }
}
