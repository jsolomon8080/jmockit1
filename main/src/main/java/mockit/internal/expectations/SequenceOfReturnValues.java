/*
 * Copyright (c) 2006 JMockit developers
 * This file is subject to the terms of the MIT license (see LICENSE.txt).
 */
package mockit.internal.expectations;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

final class SequenceOfReturnValues {
    @Nonnull
    private final Expectation expectation;
    @Nonnull
    private final Class<?> returnType;
    @Nullable
    private final Object firstValue;
    @Nonnull
    private final Object[] remainingValues;

    SequenceOfReturnValues(@Nonnull Expectation expectation, @Nullable Object firstValue,
            @Nonnull Object[] remainingValues) {
        this.expectation = expectation;
        returnType = expectation.getReturnType();
        this.firstValue = firstValue;
        this.remainingValues = remainingValues;
    }

    boolean addResultWithSequenceOfValues() {
        boolean added = false;

        if (returnType != void.class) {
            if (returnType.isArray()) {
                added = addValuesInArrayIfApplicable();
            } else if (Iterator.class.isAssignableFrom(returnType)) {
                added = addValuesInIteratorIfApplicable();
            } else if (Iterable.class.isAssignableFrom(returnType)) {
                added = addValuesInIterableIfApplicable();
            }
        }

        return added;
    }

    private boolean addValuesInArrayIfApplicable() {
        if (firstValue == null || !firstValue.getClass().isArray()) {
            addArrayAsReturnValue();
            return true;
        }

        return false;
    }

    private void addArrayAsReturnValue() {
        Class<?> elementType = returnType.getComponentType();
        int n = 1 + remainingValues.length;
        Object values = Array.newInstance(elementType, n);
        setArrayElement(elementType, values, 0, firstValue);

        for (int i = 1; i < n; i++) {
            setArrayElement(elementType, values, i, remainingValues[i - 1]);
        }

        expectation.getResults().addReturnValue(values);
    }

    private static void setArrayElement(Class<?> elementType, Object array, int index, @Nullable Object value) {
        Object arrayValue = value;

        if (value != null) {
            if (elementType == byte.class || elementType == Byte.class) {
                arrayValue = ((Number) value).byteValue();
            } else if (elementType == short.class || elementType == Short.class) {
                arrayValue = ((Number) value).shortValue();
            }
        }

        Array.set(array, index, arrayValue);
    }

    private boolean addValuesInIteratorIfApplicable() {
        if (firstValue == null || !Iterator.class.isAssignableFrom(firstValue.getClass())) {
            List<Object> values = new ArrayList<>(1 + remainingValues.length);
            addAllValues(values);
            expectation.getResults().addReturnValue(values.iterator());
            return true;
        }

        return false;
    }

    private void addAllValues(@Nonnull Collection<Object> values) {
        values.add(firstValue);
        Collections.addAll(values, remainingValues);
    }

    private boolean addValuesInIterableIfApplicable() {
        if (firstValue == null || !Iterable.class.isAssignableFrom(firstValue.getClass())) {
            Collection<Object> values;

            if (returnType.isAssignableFrom(List.class)) {
                values = new ArrayList<>(1 + remainingValues.length);
            } else if (returnType.isAssignableFrom(Set.class)) {
                values = new LinkedHashSet<>(1 + remainingValues.length);
            } else if (returnType.isAssignableFrom(SortedSet.class)) {
                values = new TreeSet<>();
            } else {
                return false;
            }

            addReturnValues(values);
            return true;
        }

        return false;
    }

    private void addReturnValues(@Nonnull Collection<Object> values) {
        addAllValues(values);
        expectation.getResults().addReturnValue(values);
    }
}
