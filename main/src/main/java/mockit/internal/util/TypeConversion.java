/*
 * Copyright (c) 2006 JMockit developers
 * This file is subject to the terms of the MIT license (see LICENSE.txt).
 */
package mockit.internal.util;

import static mockit.internal.util.AutoBoxing.isWrapperOfPrimitiveType;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class TypeConversion {
    private TypeConversion() {
    }

    @Nullable
    public static Object convertFromString(@Nonnull Class<?> targetType, @Nonnull String value) {
        if (targetType == String.class) {
            return value;
        }
        if (isCharacter(targetType)) {
            return value.charAt(0);
        }
        if (targetType.isPrimitive() || isWrapperOfPrimitiveType(targetType)) {
            return newWrapperInstance(targetType, value);
        }
        if (targetType == BigDecimal.class) {
            return new BigDecimal(value.trim());
        }
        if (targetType == BigInteger.class) {
            return new BigInteger(value.trim());
        }
        if (targetType == AtomicInteger.class) {
            return new AtomicInteger(Integer.parseInt(value.trim()));
        }
        if (targetType == AtomicLong.class) {
            return new AtomicLong(Long.parseLong(value.trim()));
        }
        if (targetType.isEnum()) {
            // noinspection unchecked
            return enumValue(targetType, value);
        }

        return null;
    }

    private static boolean isCharacter(@Nonnull Class<?> targetType) {
        return targetType == char.class || targetType == Character.class;
    }

    @Nonnull
    private static Object newWrapperInstance(@Nonnull Class<?> targetType, @Nonnull String value) {
        String trimmedValue = value.trim();

        try {
            if (targetType == int.class || targetType == Integer.class) {
                return Integer.valueOf(trimmedValue);
            }
            if (targetType == long.class || targetType == Long.class) {
                return Long.valueOf(trimmedValue);
            }
            if (targetType == short.class || targetType == Short.class) {
                return Short.valueOf(trimmedValue);
            }
            if (targetType == byte.class || targetType == Byte.class) {
                return Byte.valueOf(trimmedValue);
            }
            if (targetType == double.class || targetType == Double.class) {
                return Double.valueOf(trimmedValue);
            }
            if (targetType == float.class || targetType == Float.class) {
                return Float.valueOf(trimmedValue);
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid value \"" + trimmedValue + "\" for " + targetType);
        }

        return Boolean.valueOf(trimmedValue);
    }

    @Nonnull
    private static <E extends Enum<E>> Object enumValue(Class<?> targetType, @Nonnull String value) {
        @SuppressWarnings("unchecked")
        Class<E> enumType = (Class<E>) targetType;
        return Enum.valueOf(enumType, value);
    }
}
