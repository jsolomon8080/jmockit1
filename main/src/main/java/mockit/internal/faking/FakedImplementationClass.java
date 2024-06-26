/*
 * Copyright (c) 2006 JMockit developers
 * This file is subject to the terms of the MIT license (see LICENSE.txt).
 */
package mockit.internal.faking;

import static java.lang.reflect.Modifier.isPublic;

import java.lang.reflect.Proxy;
import java.lang.reflect.Type;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import mockit.MockUp;
import mockit.asm.classes.ClassReader;
import mockit.asm.classes.ClassVisitor;
import mockit.internal.classGeneration.ImplementationClass;
import mockit.internal.expectations.mocking.InterfaceImplementationGenerator;
import mockit.internal.util.Utilities;

public final class FakedImplementationClass<T> {
    private static final ClassLoader THIS_CL = FakedImplementationClass.class.getClassLoader();

    @Nonnull
    private final MockUp<?> fakeInstance;
    @Nullable
    private ImplementationClass<T> implementationClass;
    private Class<T> generatedClass;

    public FakedImplementationClass(@Nonnull MockUp<?> fakeInstance) {
        this.fakeInstance = fakeInstance;
    }

    @Nonnull
    public Class<T> createImplementation(@Nonnull Class<T> interfaceToBeFaked, @Nullable Type typeToFake) {
        createImplementation(interfaceToBeFaked);
        byte[] generatedBytecode = implementationClass == null ? null : implementationClass.getGeneratedBytecode();

        FakeClassSetup fakeClassSetup = new FakeClassSetup(generatedClass, typeToFake, fakeInstance, generatedBytecode);
        fakeClassSetup.redefineMethodsInGeneratedClass();

        return generatedClass;
    }

    @Nonnull
    Class<T> createImplementation(@Nonnull Class<T> interfaceToBeFaked) {
        if (isPublic(interfaceToBeFaked.getModifiers())) {
            generateImplementationForPublicInterface(interfaceToBeFaked);
        } else {
            // noinspection unchecked
            generatedClass = (Class<T>) Proxy.getProxyClass(interfaceToBeFaked.getClassLoader(), interfaceToBeFaked);
        }

        return generatedClass;
    }

    private void generateImplementationForPublicInterface(@Nonnull Class<T> interfaceToBeFaked) {
        implementationClass = new ImplementationClass<T>(interfaceToBeFaked) {
            @Nonnull
            @Override
            protected ClassVisitor createMethodBodyGenerator(@Nonnull ClassReader typeReader) {
                return new InterfaceImplementationGenerator(typeReader, interfaceToBeFaked, generatedClassName);
            }
        };

        generatedClass = implementationClass.generateClass();
    }

    @Nonnull
    public Class<T> createImplementation(@Nonnull Type[] interfacesToBeFaked) {
        Class<?>[] interfacesToFake = new Class<?>[interfacesToBeFaked.length];

        for (int i = 0; i < interfacesToFake.length; i++) {
            interfacesToFake[i] = Utilities.getClassType(interfacesToBeFaked[i]);
        }

        // noinspection unchecked
        generatedClass = (Class<T>) Proxy.getProxyClass(THIS_CL, interfacesToFake);
        new FakeClassSetup(generatedClass, null, fakeInstance, null).redefineMethods();

        return generatedClass;
    }
}
