/*
 * Copyright (c) 2006 JMockit developers
 * This file is subject to the terms of the MIT license (see LICENSE.txt).
 */
package mockit.internal.reflection;

import static mockit.internal.reflection.ParameterReflection.NO_PARAMETERS;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class AnnotationReflection {
    private AnnotationReflection() {
    }

    @Nonnull
    public static String readAnnotationAttribute(@Nonnull Object annotationInstance, @Nonnull String attributeName) {
        try {
            return readAttribute(annotationInstance, attributeName);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Nullable
    public static String readAnnotationAttributeIfAvailable(@Nonnull Object annotationInstance,
            @Nonnull String attributeName) {
        try {
            return readAttribute(annotationInstance, attributeName);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    @Nonnull
    private static String readAttribute(@Nonnull Object annotationInstance, @Nonnull String attributeName)
            throws NoSuchMethodException {
        try {
            Method publicMethod = annotationInstance.getClass().getMethod(attributeName, NO_PARAMETERS);
            return (String) publicMethod.invoke(annotationInstance);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e.getCause());
        }
    }
}
