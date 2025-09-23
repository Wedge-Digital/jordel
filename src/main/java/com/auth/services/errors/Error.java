package com.auth.services.errors;


//public class Error {
//
//    private final ErrorType type;
//    private final String message;
//
//    public Error(ErrorType type, String message) {
//        this.type = type;
//        this.message = message;
//    }
//    public final ErrorType getType() {
//        return this.type;
//    }
//
//    public String toString() {
//        return "ValidationError{" +
//                "message='" + this.getMessage() + '\'' +
//                '}';
//    }
//
//    public String getMessage() {
//        return message;
//    }
//
//
//}

public abstract class Error {
    private final String context;

    public abstract String getMessage();

    protected Error(String context) {
        this.context = context;
    }

    public String getContext()
    {
        return context;
    }
}