/*
 * Copyright (c) 2006 JMockit developers
 * This file is subject to the terms of the MIT license (see LICENSE.txt).
 */
package mockit.internal.expectations;

import static mockit.internal.util.Utilities.containsReference;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nonnull;

import mockit.internal.util.GeneratedClasses;

final class InstanceBasedMatching {
    @Nonnull
    private final List<Class<?>> mockedTypesToMatchOnInstances;

    InstanceBasedMatching() {
        mockedTypesToMatchOnInstances = new LinkedList<>();
    }

    void discoverMockedTypesToMatchOnInstances(@Nonnull List<Class<?>> targetClasses) {
        int numClasses = targetClasses.size();

        if (numClasses > 1) {
            for (int i = 0; i < numClasses; i++) {
                Class<?> targetClass = targetClasses.get(i);

                if (targetClasses.lastIndexOf(targetClass) > i) {
                    addMockedTypeToMatchOnInstance(targetClass);
                }
            }
        }
    }

    private void addMockedTypeToMatchOnInstance(@Nonnull Class<?> mockedType) {
        if (!containsReference(mockedTypesToMatchOnInstances, mockedType)) {
            mockedTypesToMatchOnInstances.add(mockedType);
        }
    }

    boolean isToBeMatchedOnInstance(@Nonnull Object mock) {
        Class<?> mockedClass = GeneratedClasses.getMockedClass(mock);
        return containsReference(mockedTypesToMatchOnInstances, mockedClass);
    }
}
