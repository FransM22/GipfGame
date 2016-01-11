package Exceptions;

/**
 * This Exception is thrown if a class tries to handle a position that is invalid for that operation.
 *
 * Created by frans on 18-9-2015.
 */
public class InvalidMoveException extends Exception {

    /**
     * Overriding method so that it won't fill in the stack trace (= very expensive operation)
     * @return
     */
    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
