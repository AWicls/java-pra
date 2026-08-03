package learning.pra.exceptions;

public class ConfigException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    ConfigException() {
        super();
    }

    ConfigException(String msg) {
        super(msg);
    }

    ConfigException(String msg, Throwable cause) {
        super(msg, cause);
    }

    ConfigException(Throwable cause) {
        super(cause);
    }

}
