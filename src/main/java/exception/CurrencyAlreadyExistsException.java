package exception;

public class CurrencyAlreadyExistsException extends ServiceException {

    public CurrencyAlreadyExistsException(String message) {
        super(message);
    }
}