/*
 * Copyright (c) 2006 JMockit developers
 * This file is subject to the terms of the MIT license (see LICENSE.txt).
 */
package mockit.internal.faking;

import static mockit.internal.util.Utilities.getClassType;

import java.lang.reflect.Type;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import mockit.MockUp;
import mockit.asm.classes.ClassReader;
import mockit.internal.BaseClassModifier;
import mockit.internal.capturing.CaptureOfImplementations;

public final class CaptureOfFakedImplementations extends CaptureOfImplementations<Void> {
    private final FakeClassSetup fakeClassSetup;

    public CaptureOfFakedImplementations(@Nonnull MockUp<?> fake, @Nonnull Type baseType) {
        Class<?> baseClassType = getClassType(baseType);
        fakeClassSetup = new FakeClassSetup(baseClassType, baseType, fake, null);
    }

    @Nonnull
    @Override
    protected BaseClassModifier createModifier(@Nullable ClassLoader cl, @Nonnull ClassReader cr,
            @Nonnull Class<?> baseType, Void typeMetadata) {
        return fakeClassSetup.createClassModifier(cr);
    }

    @Override
    protected void redefineClass(@Nonnull Class<?> realClass, @Nonnull byte[] modifiedClass) {
        fakeClassSetup.applyClassModifications(realClass, modifiedClass);
    }

    @Nullable
    public <T> Class<T> apply() {
        @SuppressWarnings("unchecked")
        Class<T> baseType = (Class<T>) fakeClassSetup.realClass;
        Class<T> baseClassType = baseType;
        Class<T> fakedClass = null;

        if (baseType.isInterface()) {
            fakedClass = new FakedImplementationClass<T>(fakeClassSetup.fake).createImplementation(baseType);
            baseClassType = fakedClass;
        }

        if (baseClassType != Object.class) {
            redefineClass(baseClassType, baseType, null);
        }

        makeSureAllSubtypesAreModified(baseType, false, null);
        return fakedClass;
    }
}
