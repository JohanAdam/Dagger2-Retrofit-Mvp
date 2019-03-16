package io.reciteapp.recite.di.scope;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.inject.Scope;

/**
 * Scope for Activity lifecycle
 */

@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface PerActivity {
}
