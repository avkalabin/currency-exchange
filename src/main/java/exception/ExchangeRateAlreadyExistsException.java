package exception;

public class ExchangeRateAlreadyExistsException extends ServiceException {

    public ExchangeRateAlreadyExistsException(String message) {
        super(message);
    }
}