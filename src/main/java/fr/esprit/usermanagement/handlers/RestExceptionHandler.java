package fr.esprit.usermanagement.handlers;

import fr.esprit.usermanagement.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(InvalidEntityException.class)
    public ResponseEntity<ErrorDTO> handleException(InvalidEntityException exception , WebRequest webRequest){
        final HttpStatus badRequest = HttpStatus.BAD_REQUEST;
        ErrorDTO error =  ErrorDTO.builder()
                .errorCodes(exception.getErrorCodes())
                .httpCode(badRequest.value())
                .message(exception.getMessage())
                .errors(exception.getErrors())
                .build();
        return new ResponseEntity<>(error , badRequest);

    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorDTO> handleException(UnauthorizedException exception , WebRequest webRequest){
        final HttpStatus badRequest = HttpStatus.UNAUTHORIZED;
        ErrorDTO error =  ErrorDTO.builder()
                .errorCodes(exception.getErrorCodes())
                .httpCode(badRequest.value())
                .message(exception.getMessage())
                .errors(exception.getErrors())
                .build();
        return new ResponseEntity<>(error , badRequest);

    }


    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorDTO> handleException(EntityNotFoundException exception , WebRequest webRequest){
        final HttpStatus badRequest = HttpStatus.NOT_FOUND;
        ErrorDTO error =  ErrorDTO.builder()
                .errorCodes(exception.getErrorCodes())
                .httpCode(badRequest.value())
                .message(exception.getMessage())
                .errors(exception.getErrors())
                .build();
        return new ResponseEntity<>(error , badRequest);

    }

    @ExceptionHandler(ErrorOccurredException.class)
    public ResponseEntity<ErrorDTO> handleException(ErrorOccurredException exception , WebRequest webRequest){
        final HttpStatus badRequest = HttpStatus.INTERNAL_SERVER_ERROR;
        ErrorDTO error =  ErrorDTO.builder()
                .errorCodes(exception.getErrorCodes())
                .httpCode(badRequest.value())
                .message(exception.getMessage())
                .errors(exception.getErrors())
                .build();
        return new ResponseEntity<>(error , badRequest);

    }

    @ExceptionHandler(EntityAlreadyExistException.class)
    public ResponseEntity<ErrorDTO> handleException(EntityAlreadyExistException exception , WebRequest webRequest){
        final HttpStatus badRequest = HttpStatus.CONFLICT;
        ErrorDTO error =  ErrorDTO.builder()
                .errorCodes(exception.getErrorCodes())
                .httpCode(badRequest.value())
                .message(exception.getMessage())
                .errors(exception.getErrors())
                .build();
        return new ResponseEntity<>(error , badRequest);

    }
}
