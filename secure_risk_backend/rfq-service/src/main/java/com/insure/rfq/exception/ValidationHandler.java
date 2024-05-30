package com.insure.rfq.exception;

import jakarta.el.MethodNotFoundException;
import org.apache.http.auth.AuthenticationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException.*;
import org.springframework.web.client.HttpServerErrorException.GatewayTimeout;
import org.springframework.web.client.HttpServerErrorException.InternalServerError;
import org.springframework.web.client.HttpServerErrorException.ServiceUnavailable;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.net.ConnectException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

@RestControllerAdvice
public class ValidationHandler {

    @ExceptionHandler(ConnectException.class)
    public ResponseEntity<ExceptionEntity> connectionRefuse(ConnectException exeConn) {
        ExceptionEntity exception = new ExceptionEntity();
        exception.setTimeStamp(LocalDateTime.now());
        exception.setMessage(exeConn.getMessage());
        exception.setStatusResponse(HttpStatusCode.valueOf(500));
        exception.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        return new ResponseEntity<ExceptionEntity>(exception, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(TimeoutException.class)
    public ResponseEntity<ExceptionEntity> checkRequestTimeOut(TimeoutException exeTimeOut) {
        ExceptionEntity exception = new ExceptionEntity();
        exception.setTimeStamp(LocalDateTime.now());
        exception.setMessage(exeTimeOut.getMessage());
        exception.setStatusResponse(HttpStatusCode.valueOf(504));
        exception.setStatusCode(HttpStatus.GATEWAY_TIMEOUT.value());
        return new ResponseEntity<ExceptionEntity>(exception, HttpStatus.GATEWAY_TIMEOUT);
    }

    @ExceptionHandler(InternalServerError.class)
    public ResponseEntity<ExceptionEntity> checkInvalidRequest(InternalServerError exeTimeOut) {
        ExceptionEntity exception = new ExceptionEntity();
        exception.setTimeStamp(LocalDateTime.now());
        exception.setMessage(exeTimeOut.getMessage());
        exception.setStatusResponse(HttpStatusCode.valueOf(500));
        exception.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        return new ResponseEntity<ExceptionEntity>(exception, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({MethodNotFoundException.class})
    public ResponseEntity<ExceptionEntity> checkMethodNotAllowed(MethodNotFoundException except) {
        ExceptionEntity exception = new ExceptionEntity();
        exception.setTimeStamp(LocalDateTime.now());
        exception.setMessage(except.getMessage());
        exception.setStatusResponse(HttpStatusCode.valueOf(405));
        exception.setStatusCode(HttpStatus.METHOD_NOT_ALLOWED.value());
        return new ResponseEntity<ExceptionEntity>(exception, HttpStatus.METHOD_NOT_ALLOWED);
    }


    @ExceptionHandler(BadRequest.class)
    public ResponseEntity<ExceptionEntity>
    checkMethodNotFound(BadRequest exception) {
        ExceptionEntity exception1 = new
                ExceptionEntity();
        exception1.setTimeStamp(LocalDateTime.now());
        exception1.setMessage(exception.getMessage());
        exception1.setStatusResponse(HttpStatusCode.valueOf(400));
        exception1.setStatusCode(HttpStatus.BAD_REQUEST.value());
        return new
                ResponseEntity<ExceptionEntity>(exception1, HttpStatus.BAD_REQUEST);

    }


    @ExceptionHandler(Unauthorized.class)
    public ResponseEntity<ExceptionEntity> checkUnauthorize(Unauthorized exceAuth) {
        ExceptionEntity exception = new ExceptionEntity();
        exception.setTimeStamp(LocalDateTime.now());
        exception.setMessage(exceAuth.getMessage());
        exception.setStatusResponse(HttpStatusCode.valueOf(401));
        exception.setStatusCode(HttpStatus.UNAUTHORIZED.value());
        return new ResponseEntity<ExceptionEntity>(exception, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Forbidden.class)
    public ResponseEntity<ExceptionEntity> checkForbidden(Forbidden exceFore) {
        ExceptionEntity exception = new ExceptionEntity();
        exception.setTimeStamp(LocalDateTime.now());
        exception.setMessage(exceFore.getMessage());
        exception.setStatusResponse(HttpStatusCode.valueOf(403));
        exception.setStatusCode(HttpStatus.FORBIDDEN.value());
        return new ResponseEntity<ExceptionEntity>(exception, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ServiceUnavailable.class)
    public ResponseEntity<ExceptionEntity> checkServiceAvailbility(ServiceUnavailable exeServiceUnable) {
        ExceptionEntity exception = new ExceptionEntity();
        exception.setTimeStamp(LocalDateTime.now());
        exception.setMessage(exeServiceUnable.getMessage());
        exception.setStatusResponse(HttpStatusCode.valueOf(503));
        exception.setStatusCode(HttpStatus.SERVICE_UNAVAILABLE.value());
        return new ResponseEntity<ExceptionEntity>(exception, HttpStatus.SERVICE_UNAVAILABLE);

    }

    @ExceptionHandler(Conflict.class)
    public ResponseEntity<ExceptionEntity> checkConflict(Conflict exeConflict) {
        ExceptionEntity exception = new ExceptionEntity();
        exception.setTimeStamp(LocalDateTime.now());
        exception.setMessage(exeConflict.getMessage());
        exception.setStatusResponse(HttpStatusCode.valueOf(409));
        exception.setStatusCode(HttpStatus.CONFLICT.value());
        return new ResponseEntity<ExceptionEntity>(exception, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UnsupportedMediaType.class)
    public ResponseEntity<ExceptionEntity> checkConflict(UnsupportedMediaType exeMediaTYpe) {
        ExceptionEntity exception = new ExceptionEntity();
        exception.setTimeStamp(LocalDateTime.now());
        exception.setMessage(exeMediaTYpe.getMessage());
        exception.setStatusResponse(HttpStatusCode.valueOf(409));
        exception.setStatusCode(HttpStatus.CONFLICT.value());
        return new ResponseEntity<ExceptionEntity>(exception, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UnprocessableEntity.class)
    public ResponseEntity<ExceptionEntity> checkUnprocessableEntity(UnprocessableEntity exeUnprocess) {
        ExceptionEntity exception = new ExceptionEntity();
        exception.setTimeStamp(LocalDateTime.now());
        exception.setMessage(exeUnprocess.getMessage());
        exception.setStatusResponse(HttpStatusCode.valueOf(400));
        exception.setStatusCode(HttpStatus.UNAUTHORIZED.value());
        return new ResponseEntity<ExceptionEntity>(exception, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TooManyRequests.class)
    public ResponseEntity<ExceptionEntity> checkForTooMAnyRquest(TooManyRequests exeTooMany) {
        ExceptionEntity exception = new ExceptionEntity();
        exception.setTimeStamp(LocalDateTime.now());
        exception.setMessage(exeTooMany.getMessage());
        exception.setStatusResponse(HttpStatusCode.valueOf(429));
        exception.setStatusCode(HttpStatus.TOO_MANY_REQUESTS.value());
        return new ResponseEntity<ExceptionEntity>(exception, HttpStatus.TOO_MANY_REQUESTS);

    }

    @ExceptionHandler(GatewayTimeout.class)
    public ResponseEntity<ExceptionEntity> checkGateWayTimeOut(GatewayTimeout exeGatewayTimeOut) {
        ExceptionEntity exception = new ExceptionEntity();
        exception.setTimeStamp(LocalDateTime.now());
        exception.setMessage(exeGatewayTimeOut.getMessage());
        exception.setStatusResponse(HttpStatusCode.valueOf(504));
        exception.setStatusCode(HttpStatus.GATEWAY_TIMEOUT.value());
        return new ResponseEntity<ExceptionEntity>(exception, HttpStatus.GATEWAY_TIMEOUT);
    }

    @ExceptionHandler(NotAcceptable.class)
    public ResponseEntity<ExceptionEntity> checkNotAcceptablet(NotAcceptable exeNotAcceptable) {
        ExceptionEntity exception = new ExceptionEntity();
        exception.setTimeStamp(LocalDateTime.now());
        exception.setMessage(exeNotAcceptable.getMessage());
        exception.setStatusResponse(HttpStatusCode.valueOf(406));
        exception.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
        return new ResponseEntity<ExceptionEntity>(exception, HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ExceptionEntity> checkResponseStautsu(AuthenticationException exe) {
        ExceptionEntity exception = new ExceptionEntity();
        exception.setTimeStamp(LocalDateTime.now());
        exception.setMessage(exe.getMessage());
        exception.setStatusResponse(HttpStatusCode.valueOf(401));
        exception.setStatusCode(HttpStatus.UNAUTHORIZED.value());
        return new ResponseEntity<ExceptionEntity>(exception, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ExceptionEntity> checkMethodNotAllowedException(HttpRequestMethodNotSupportedException ex) {
        ExceptionEntity exception = new ExceptionEntity();
        exception.setTimeStamp(LocalDateTime.now());
        exception.setMessage(ex.getMessage());
        exception.setStatusResponse(HttpStatus.METHOD_NOT_ALLOWED);
        exception.setStatusCode(HttpStatus.METHOD_NOT_ALLOWED.value());
        return new ResponseEntity<ExceptionEntity>(exception, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> checkMethodNotAllowedException(MethodArgumentNotValidException ex) {

        Map<String, String> map = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((errors) -> {
            String error = ((FieldError) errors).getField();
            String msg = errors.getDefaultMessage();
            map.put(error, msg);

        });

        return new ResponseEntity<Map<String, String>>(map, HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ExceptionEntity> checkGateWayTimeOut(MaxUploadSizeExceededException maxExe) {
        ExceptionEntity exception = new ExceptionEntity();
        exception.setTimeStamp(LocalDateTime.now());
        exception.setMessage(maxExe.getMessage());
        exception.setStatusResponse(HttpStatusCode.valueOf(415));
        exception.setStatusCode(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value());
        return new ResponseEntity<ExceptionEntity>(exception, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionEntity> checkIllegalArgumentException(IllegalArgumentException illEcxep) {
        ExceptionEntity exception = new ExceptionEntity();
        exception.setTimeStamp(LocalDateTime.now());
        exception.setMessage(illEcxep.getMessage());
        exception.setStatusResponse(HttpStatusCode.valueOf(400));
        exception.setStatusCode(HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<ExceptionEntity>(exception, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(InvalidTpaException.class)
    public ResponseEntity<ExceptionEntity> tpaNotFound(InvalidTpaException illEcxep) {
        ExceptionEntity exception = new ExceptionEntity();
        exception.setTimeStamp(LocalDateTime.now());
        exception.setMessage(illEcxep.getMessage());
        exception.setStatusResponse(HttpStatusCode.valueOf(400));
        exception.setStatusCode(HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(exception, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ExceptionEntity> checkdataViolation(DataIntegrityViolationException illEcxep) {
        ExceptionEntity exception = new ExceptionEntity();
        exception.setTimeStamp(LocalDateTime.now());
        exception.setMessage(illEcxep.getMessage());
        exception.setStatusResponse(HttpStatusCode.valueOf(400));
        exception.setStatusCode(HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<ExceptionEntity>(exception, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TpaNotFoundException.class)
    public ResponseEntity<ExceptionEntity> tpaNotFoundException(TpaNotFoundException illEcxep) {
        ExceptionEntity exception = new ExceptionEntity();
        exception.setTimeStamp(LocalDateTime.now());
        exception.setMessage(illEcxep.getMessage());
        exception.setStatusResponse(HttpStatusCode.valueOf(400));
        exception.setStatusCode(HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(exception, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidMyDetailsIdException.class)
    public ResponseEntity<ExceptionEntity> myDetailsIdNotFound(InvalidMyDetailsIdException illEcxep) {
        ExceptionEntity exception = new ExceptionEntity();
        exception.setTimeStamp(LocalDateTime.now());
        exception.setMessage(illEcxep.getMessage());
        exception.setStatusResponse(HttpStatusCode.valueOf(400));
        exception.setStatusCode(HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(exception, HttpStatus.BAD_REQUEST);
    }
}
