package de.imfactions.util.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ReflectionUtil {

    private static final Map<Class<?>, Class<?>> builtInMap = new HashMap();

    public static Enum<?> getEnum(Class<?> clazz, String constant) throws EnumNotFoundException {
        Enum<?>[] enumConstants = (Enum[])clazz.getEnumConstants();
        Enum[] var3 = enumConstants;
        int var4 = enumConstants.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            Enum<?> e = var3[var5];
            if (e.name().equalsIgnoreCase(constant)) {
                return e;
            }
        }

        throw new EnumNotFoundException("Enum constant not found " + constant);
    }

    public static Enum<?> getEnum(Class<?> clazz, String enumName, String constant) throws EnumNotFoundException, ClassNotFoundException {
        return getEnum(getSubClass(clazz, enumName), constant);
    }

    private static Class<?> getSubClass(Class<?> clazz, String className) throws ClassNotFoundException {
        Class[] var2 = clazz.getDeclaredClasses();
        int var3 = var2.length;

        int var4;
        Class subClass;
        for(var4 = 0; var4 < var3; ++var4) {
            subClass = var2[var4];
            if (subClass.getSimpleName().equals(className)) {
                return subClass;
            }
        }

        var2 = clazz.getClasses();
        var3 = var2.length;

        for(var4 = 0; var4 < var3; ++var4) {
            subClass = var2[var4];
            if (subClass.getSimpleName().equals(className)) {
                return subClass;
            }
        }

        throw new ClassNotFoundException("Sub class " + className + " of " + clazz.getSimpleName() + " not found!");
    }

    public static Field getField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        Field f;
        try {
            f = clazz.getDeclaredField(fieldName);
        } catch (Exception var4) {
            f = clazz.getField(fieldName);
        }

        setFieldAccessible(f);
        return f;
    }

    private static Method getMethod(Class<?> clazz, String methodName) throws NoSuchMethodException {
        Method m;
        try {
            m = clazz.getDeclaredMethod(methodName);
        } catch (Exception var4) {
            m = clazz.getMethod(methodName);
        }

        m.setAccessible(true);
        return m;
    }

    private static Method getMethod(Class<?> clazz, String methodName, Class<?>... args) throws NoSuchMethodException {
        Method m;
        try {
            m = clazz.getDeclaredMethod(methodName, args);
        } catch (Exception var5) {
            m = clazz.getMethod(methodName, args);
        }

        m.setAccessible(true);
        return m;
    }

    public static <T> Field getField(Class<?> target, String name, Class<T> fieldType, int index) throws FieldNotFoundException {
        Field[] var4 = target.getDeclaredFields();
        int var5 = var4.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            Field field = var4[var6];
            if ((name == null || field.getName().equals(name)) && fieldType.isAssignableFrom(field.getType()) && index-- <= 0) {
                field.setAccessible(true);
                return field;
            }
        }

        if (target.getSuperclass() != null) {
            return getField(target.getSuperclass(), name, fieldType, index);
        } else {
            throw new FieldNotFoundException("Cannot find field with type " + fieldType + " in " + target.getSimpleName());
        }
    }

    public static Object getObject(Object obj, String fieldName) throws ReflectionException {
        try {
            return getField(obj.getClass(), fieldName).get(obj);
        } catch (Exception var3) {
            throw new ReflectionException(var3);
        }
    }

    public static Object getFieldByType(Object obj, String typeName) throws ReflectionException {
        return getFieldByType(obj, obj.getClass(), typeName);
    }

    private static Object getFieldByType(Object obj, Class<?> superClass, String typeName) throws ReflectionException {
        return getFieldByTypeList(obj, superClass, typeName).get(0);
    }

    public static List<Object> getFieldByTypeList(Object obj, String typeName) throws ReflectionException {
        return getFieldByTypeList(obj, obj.getClass(), typeName);
    }

    public static List<Object> getFieldByTypeList(Object obj, Class<?> superClass, String typeName) throws ReflectionException {
        ArrayList fields = new ArrayList();

        try {
            Field[] var4 = superClass.getDeclaredFields();
            int var5 = var4.length;

            for(int var6 = 0; var6 < var5; ++var6) {
                Field f = var4[var6];
                if (f.getType().getSimpleName().equalsIgnoreCase(typeName)) {
                    setFieldAccessible(f);
                    fields.add(f.get(obj));
                }
            }

            if (superClass.getSuperclass() != null) {
                fields.addAll(getFieldByTypeList(obj, superClass.getSuperclass(), typeName));
            }

            if (fields.isEmpty() && obj.getClass() == superClass) {
                throw new FieldNotFoundException("Could not find field of type " + typeName + " in " + obj.getClass().getSimpleName());
            } else {
                return fields;
            }
        } catch (Exception var8) {
            throw new ReflectionException(var8);
        }
    }

    public static Object invokeConstructor(Class<?> clazz, Class<?>[] args, Object... initArgs) throws ReflectionException {
        try {
            return getConstructor(clazz, args).newInstance(initArgs);
        } catch (Exception var4) {
            throw new ReflectionException(var4);
        }
    }

    public static Object invokeConstructor(Class<?> clazz, Object... initArgs) throws ReflectionException {
        try {
            return getConstructorByArgs(clazz, initArgs).newInstance(initArgs);
        } catch (Exception var3) {
            throw new ReflectionException(var3);
        }
    }

    private static Constructor<?> getConstructor(Class<?> clazz, Class<?>... args) throws NoSuchMethodException {
        Constructor<?> c = clazz.getConstructor(args);
        c.setAccessible(true);
        return c;
    }

    private static Constructor<?> getConstructorByArgs(Class<?> clazz, Object... args) throws ReflectionException {
        Constructor[] var2 = clazz.getConstructors();
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            Constructor<?> constructor = var2[var4];
            if (constructor.getParameterTypes().length == args.length) {
                int i = 0;
                Class[] var7 = constructor.getParameterTypes();
                int var8 = var7.length;

                for(int var9 = 0; var9 < var8; ++var9) {
                    Class<?> parameter = var7[var9];
                    if (!isAssignable(parameter, args[i])) {
                        break;
                    }

                    ++i;
                }

                if (i == args.length) {
                    return constructor;
                }
            }
        }

        throw new ReflectionException("Could not find constructor with args " + Arrays.stream(args).map(Object::getClass).map(Class::getSimpleName).collect(Collectors.joining(", ")) + " in " + clazz.getSimpleName());
    }

    private static boolean isAssignable(Class<?> clazz, Object obj) {
        clazz = convertToPrimitive(clazz);
        return clazz.isInstance(obj) || clazz == convertToPrimitive(obj.getClass());
    }

    private static Class<?>[] toClassArray(Object[] args) {
        return (Class[]) Stream.of(args).map(Object::getClass).map(ReflectionUtil::convertToPrimitive).toArray((x$0) -> {
            return new Class[x$0];
        });
    }

    private static Class<?> convertToPrimitive(Class<?> clazz) {
        return builtInMap.getOrDefault(clazz, clazz);
    }

    public static Object invokeMethod(Class<?> clazz, Object obj, String method) throws ReflectionException {
        try {
            return Objects.requireNonNull(getMethod(clazz, method)).invoke(obj);
        } catch (Exception var4) {
            throw new ReflectionException(var4);
        }
    }

    public static Object invokeMethod(Class<?> clazz, Object obj, String method, Class<?>[] args, Object... initArgs) throws ReflectionException {
        try {
            return Objects.requireNonNull(getMethod(clazz, method, args)).invoke(obj, initArgs);
        } catch (Exception var6) {
            throw new ReflectionException(var6);
        }
    }

    public static Object invokeMethod(Class<?> clazz, Object obj, String method, Object... initArgs) throws ReflectionException {
        try {
            return Objects.requireNonNull(getMethod(clazz, method)).invoke(obj, initArgs);
        } catch (Exception var5) {
            throw new ReflectionException(var5);
        }
    }

    public static Object invokeMethod(Object obj, String method) throws ReflectionException {
        try {
            return Objects.requireNonNull(getMethod(obj.getClass(), method)).invoke(obj);
        } catch (Exception var3) {
            throw new ReflectionException(var3);
        }
    }

    public static Object invokeMethod(Object obj, String method, Object[] initArgs) throws ReflectionException {
        try {
            return Objects.requireNonNull(getMethod(obj.getClass(), method)).invoke(obj, initArgs);
        } catch (Exception var4) {
            throw new ReflectionException(var4);
        }
    }

    private static void setFieldAccessible(Field f) {
        f.setAccessible(true);
    }

}
