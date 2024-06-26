/*
 * Copyright (c) 2006 JMockit developers
 * This file is subject to the terms of the MIT license (see LICENSE.txt).
 */
package mockit.integration.springframework;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import mockit.internal.injection.BeanExporter;
import mockit.internal.injection.TestedClassInstantiations;
import mockit.internal.state.TestRun;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.web.context.support.StaticWebApplicationContext;

/**
 * A {@link org.springframework.web.context.WebApplicationContext} implementation which exposes the
 * {@linkplain mockit.Tested @Tested} objects and their injected dependencies declared in the current test class.
 */
public final class TestWebApplicationContext extends StaticWebApplicationContext {
    @Override
    @Nonnull
    public Object getBean(@Nonnull String name) {
        BeanExporter beanExporter = getBeanExporter();
        return BeanLookup.getBean(beanExporter, name);
    }

    @Nonnull
    private static BeanExporter getBeanExporter() {
        TestedClassInstantiations testedClasses = TestRun.getTestedClassInstantiations();

        if (testedClasses == null) {
            throw new BeanDefinitionStoreException("Test class does not define any @Tested fields");
        }

        return testedClasses.getBeanExporter();
    }

    @Override
    @Nonnull
    public <T> T getBean(@Nonnull String name, @Nullable Class<T> requiredType) {
        BeanExporter beanExporter = getBeanExporter();
        return BeanLookup.getBean(beanExporter, name, requiredType);
    }

    @Override
    @Nonnull
    public <T> T getBean(@Nonnull Class<T> requiredType) {
        BeanExporter beanExporter = getBeanExporter();
        return BeanLookup.getBean(beanExporter, requiredType);
    }
}
