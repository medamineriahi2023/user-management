package fr.esprit.usermanagement.exceptions.abstracts;

import fr.esprit.usermanagement.handlers.ErrorCodes;
import lombok.Getter;

import java.util.List;

@Getter
public abstract class AbstractEntityException extends Exception {
    @Getter
    private ErrorCodes errorCodes ;
    @Getter
    private List<String> errors;


    public AbstractEntityException(String message , ErrorCodes errorCodes , List<String> errors){
        super(message);
        this.errorCodes = errorCodes;
        this.errors = errors;
    }

    public AbstractEntityException(String message , ErrorCodes errorCodes){
        super(message);
        this.errorCodes = errorCodes;
    }
}
