package jsfas.common.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

/**
 * Enable/disable some common feature from Controller
 *
 * @author iswill
 * @since 21/01/2017
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Function {
	String functionCode();
	String functionSubCode() default " ";
	String functionPage() default " ";
	boolean accessChecking() default true;
}
