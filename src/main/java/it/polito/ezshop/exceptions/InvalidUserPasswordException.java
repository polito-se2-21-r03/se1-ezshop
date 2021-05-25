package it.polito.ezshop.exceptions;

public class InvalidUserPasswordException extends Exception {
    public InvalidUserPasswordException() { super(); }
    public InvalidUserPasswordException(String msg) { super(msg); }

}
