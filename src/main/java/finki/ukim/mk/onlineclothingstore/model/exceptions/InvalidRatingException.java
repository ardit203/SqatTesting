package finki.ukim.mk.onlineclothingstore.model.exceptions;

public class InvalidRatingException extends RuntimeException{
    public InvalidRatingException() {
        super("Rating must be at least 0 and at most 5 (0–5 allowed).");
    }
}
