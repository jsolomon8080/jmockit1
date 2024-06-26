/*
 * Copyright (c) 2006 JMockit developers
 * This file is subject to the terms of the MIT license (see LICENSE.txt).
 */
package mockit.internal.injection.field;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import mockit.internal.injection.InjectionState;
import mockit.internal.injection.Injector;
import mockit.internal.injection.full.FullInjection;

public final class FieldInjection extends Injector {
    public FieldInjection(@Nonnull InjectionState injectionState, @Nullable FullInjection fullInjection) {
        super(injectionState, fullInjection);
    }
}
