package cn.wubo.dynamic.loader.utility.exception;

public class CompilerRuntimeException extends RuntimeException {
    public CompilerRuntimeException(String message) {
        super(message);
    }

    public CompilerRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
