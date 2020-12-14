package de.steuer.cloud.lib.command;

/**
 * @author Steuer
 * Created: 13.12.2020
 */

public class CloudCommandNotFoundException extends RuntimeException {

    public CloudCommandNotFoundException() {

    }

    public CloudCommandNotFoundException(final String message) {
        super(message);
    }

    public CloudCommandNotFoundException(final String message, final Throwable throwable) {
        super(message, throwable);
    }

    public CloudCommandNotFoundException(final Throwable throwable) {
        super(throwable);
    }

    public CloudCommandNotFoundException(final String message, final Throwable throwable, final boolean enableSuppression, final boolean writeableStackTrace) {
        super(message, throwable, enableSuppression, writeableStackTrace);
    }

}
