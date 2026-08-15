package exception;

public class CurrencyNotFoundException extends ServiceException {
    public CurrencyNotFoundException(String message) {
        super(message);
    }
}
