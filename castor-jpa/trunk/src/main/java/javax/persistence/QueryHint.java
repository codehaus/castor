package javax.persistence;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target( {})
@Retention(RetentionPolicy.RUNTIME)
public @interface QueryHint {
    String name();
    String value();
}