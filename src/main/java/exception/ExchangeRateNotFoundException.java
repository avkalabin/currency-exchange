package exception;

public class ExchangeRateNotFoundException extends ServiceException {

    public ExchangeRateNotFoundException(String message) {
        super(message);
    }
}
