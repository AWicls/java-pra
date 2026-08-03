package learning.pra.exceptions;

/**
 * 配置加载失败时抛出的业务异常（非受检）。
 *
 * <p>继承 {@link RuntimeException}，便于在分层架构中作为统一异常抛出，
 * 同时用 {@link #getCause()} 保留底层异常（如 {@link java.io.IOException}）的根因。
 *
 * <p>提供四件套构造器，镜像 JDK 标准 {@link Exception}：
 * 无参 / 单消息 / 消息+cause / 单 cause。
 */
public class ConfigException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /** 无参构造，无消息无 cause。 */
    ConfigException() {
        super();
    }

    /** 仅带消息构造。 */
    ConfigException(String msg) {
        super(msg);
    }

    /** 消息 + cause 构造，包装底层异常时使用。 */
    ConfigException(String msg, Throwable cause) {
        super(msg, cause);
    }

    /** 仅带 cause 构造。 */
    ConfigException(Throwable cause) {
        super(cause);
    }

}
