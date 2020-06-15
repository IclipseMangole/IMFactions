package de.imfactions.util.Command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface IMCommand {

    String name();

    String[] aliases() default {};

    String[] permissions() default {};

    boolean requiresOp() default false;

    boolean requiresConsole() default false;

    boolean noConsole() default false;

    boolean runAsync() default false;

    int minArgs() default -1;

    int maxArgs() default -1;

    String usage() default "";

    String description() default "";

    String[] flags() default {};

    String[] parent() default {};


}
