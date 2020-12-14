package de.steuer.cloud.lib.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Steuer
 * Created: 13.12.2020
 */

@Target(ElementType.TYPE_USE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CloudCommandData {

    String name() default "";
    String usage() default "";
    String description() default "";

    String[] aliases() default {};

}
