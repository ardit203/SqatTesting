package finki.ukim.mk.onlineclothingstore.model.exceptions;

public class IllegalImageUrlException extends RuntimeException{
    public IllegalImageUrlException() {
        super("Illegal image URL provided. Ensure the URL is valid, accessible, and points to an image file.");
    }
}
