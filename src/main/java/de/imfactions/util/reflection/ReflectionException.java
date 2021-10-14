package de.imfactions.util.reflection;

public class ReflectionException extends ReflectiveOperationException {
    public ReflectionException(String message) {
        super(message);
    }

    public ReflectionException(Exception e) {
        super(e);
    }
}
