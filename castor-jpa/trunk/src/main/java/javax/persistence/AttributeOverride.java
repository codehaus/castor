package javax.persistence;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;;

@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD}) 
@Retention(RetentionPolicy.RUNTIME)
public @interface AttributeOverride {
    String name();
    Column column();
}