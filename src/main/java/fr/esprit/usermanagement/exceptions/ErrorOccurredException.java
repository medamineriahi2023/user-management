package fr.esprit.usermanagement.exceptions;


import fr.esprit.usermanagement.exceptions.abstracts.AbstractEntityException;
import fr.esprit.usermanagement.handlers.ErrorCodes;

import java.util.List;

public class ErrorOccurredException extends AbstractEntityException {

    public ErrorOccurredException(String message , ErrorCodes errorCodes , List<String> errors){
        super(message,errorCodes,errors);
    }

    public ErrorOccurredException(String message , ErrorCodes errorCodes){
        super(message,errorCodes);
    }
}
