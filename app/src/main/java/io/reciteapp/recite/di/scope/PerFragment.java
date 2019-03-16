package io.reciteapp.recite.di.scope;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.inject.Scope;

/**
 * Scope for Fragment lifecycle
 */

@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface PerFragment {
}
