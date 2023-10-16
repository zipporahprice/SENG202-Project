package seng202.team10.exceptions;

/**
 * This class represents an exception related to data import operations.
 * <p>
 * It is used to signify that an abnormal condition or error has
 * occurred during data import, providing additional information through its message.
 * </p>
 * <p>
 * Example usage:
 * <pre>
 *   try {
 *       // data import logic
 *   } catch (Exception e) {
 *       throw new DataImportException("Specific error message");
 *   }
 * </pre>
 * </p>
 */
public class DataImportException extends Exception {

    /**
     * Constructs a new DataImportException with the specified detail message.
     *
     * @param message the detail message, saved for later retrieval by the
     *                {@link #getMessage()} method.
     */
    public DataImportException(String message) {
        super(message);
    }
}
