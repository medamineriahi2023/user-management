package fr.esprit.usermanagement.handlers;

public enum ErrorCodes {
    ENTITY_NOT_FOUND(1000),
    INVALID_ENTITY_EXCEPTION(1006),
    ERROR_OCCURRED_EXCEPTION(1007),
    ENTITY_ALREADY_EXIST(1008),

    UNAUTHORIZED_USER(1004),

    USER_ALREADY_EXIST(1005),
    USER_NOT_VALID(1001),

    SQL_EXCEPTION(1003);

    private int id;

    ErrorCodes(int id){
        this.id = id;
    }
    public int getId(){
        return id;
    }
}

