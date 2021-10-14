package de.imfactions.util.reflection;

public class FieldNotFoundException extends ReflectiveOperationException {
    public FieldNotFoundException(String message) {
        super(message);
    }
}
