package tech.pathtoprogramming.simplebudgeting.exception;

public class DeletionException extends RuntimeException {

    public DeletionException(String id) {
        super(String.format("Expense with id %s could not be deleted", id));
    }
}
