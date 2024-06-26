/*
 * Copyright (c) 2006 JMockit developers
 * This file is subject to the terms of the MIT license (see LICENSE.txt).
 */
package mockit.internal.expectations;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import mockit.internal.expectations.invocation.ExpectedInvocation;
import mockit.internal.expectations.invocation.InvocationConstraints;
import mockit.internal.expectations.invocation.UnexpectedInvocation;

final class ReplayPhase extends Phase {
    @Nonnull
    final FailureState failureState;
    @Nonnull
    final List<Expectation> invocations;
    @Nonnull
    final List<Object> invocationInstances;
    @Nonnull
    final List<Object[]> invocationArguments;

    ReplayPhase(@Nonnull PhasedExecutionState executionState, @Nonnull FailureState failureState) {
        super(executionState);
        this.failureState = failureState;
        invocations = new ArrayList<>();
        invocationInstances = new ArrayList<>();
        invocationArguments = new ArrayList<>();
    }

    @Override
    @Nullable
    Object handleInvocation(@Nullable Object mock, int mockAccess, @Nonnull String mockClassDesc,
            @Nonnull String mockNameAndDesc, @Nullable String genericSignature, boolean withRealImpl,
            @Nonnull Object[] args) throws Throwable {
        Expectation expectation = executionState.findExpectation(mock, mockClassDesc, mockNameAndDesc, args);
        Object replacementInstance = mock == null ? null
                : executionState.equivalentInstances.getReplacementInstanceForMethodInvocation(mock, mockNameAndDesc);

        if (expectation == null) {
            expectation = createExpectation(replacementInstance == null ? mock : replacementInstance, mockAccess,
                    mockClassDesc, mockNameAndDesc, genericSignature, args);
        } else if (expectation.recordPhase != null) {
            registerNewInstanceAsEquivalentToOneFromRecordedConstructorInvocation(mock, expectation.invocation);
        }

        invocations.add(expectation);
        invocationInstances.add(mock);
        invocationArguments.add(args);
        expectation.constraints.incrementInvocationCount();

        return produceResult(expectation, mock, withRealImpl, args);
    }

    @Nonnull
    private Expectation createExpectation(@Nullable Object mock, int mockAccess, @Nonnull String mockClassDesc,
            @Nonnull String mockNameAndDesc, @Nullable String genericSignature, @Nonnull Object[] args) {
        ExpectedInvocation invocation = new ExpectedInvocation(mock, mockAccess, mockClassDesc, mockNameAndDesc, false,
                genericSignature, args);
        Expectation expectation = new Expectation(invocation);
        executionState.addExpectation(expectation);
        return expectation;
    }

    private void registerNewInstanceAsEquivalentToOneFromRecordedConstructorInvocation(@Nullable Object mock,
            @Nonnull ExpectedInvocation invocation) {
        if (mock != null && invocation.isConstructor()) {
            Map<Object, Object> instanceMap = getInstanceMap();
            instanceMap.put(mock, invocation.instance);
        }
    }

    @Nullable
    private Object produceResult(@Nonnull Expectation expectation, @Nullable Object mock, boolean withRealImpl,
            @Nonnull Object[] args) throws Throwable {
        boolean executeRealImpl = withRealImpl && expectation.recordPhase == null;

        if (executeRealImpl) {
            expectation.executedRealImplementation = true;
            return Void.class;
        }

        if (expectation.constraints.isInvocationCountMoreThanMaximumExpected()) {
            UnexpectedInvocation unexpectedInvocation = expectation.invocation.errorForUnexpectedInvocation(args);
            failureState.setErrorThrown(unexpectedInvocation);
            return null;
        }

        return expectation.produceResult(mock, args);
    }

    @Nullable
    Error endExecution() {
        return getErrorForFirstExpectationThatIsMissing();
    }

    @Nullable
    private Error getErrorForFirstExpectationThatIsMissing() {
        List<Expectation> notStrictExpectations = executionState.expectations;

        // New expectations might get added to the list, so a regular loop would cause a CME.
        for (Expectation notStrict : notStrictExpectations) {
            InvocationConstraints constraints = notStrict.constraints;

            if (constraints.isInvocationCountLessThanMinimumExpected()) {
                List<ExpectedInvocation> nonMatchingInvocations = getNonMatchingInvocations(notStrict);
                return constraints.errorForMissingExpectations(notStrict.invocation, nonMatchingInvocations);
            }
        }

        return null;
    }

    @Nonnull
    private List<ExpectedInvocation> getNonMatchingInvocations(@Nonnull Expectation unsatisfiedExpectation) {
        ExpectedInvocation unsatisfiedInvocation = unsatisfiedExpectation.invocation;
        List<ExpectedInvocation> nonMatchingInvocations = new ArrayList<>();

        for (Expectation replayedExpectation : invocations) {
            ExpectedInvocation replayedInvocation = replayedExpectation.invocation;

            if (replayedExpectation != unsatisfiedExpectation && replayedInvocation.isMatch(unsatisfiedInvocation)) {
                nonMatchingInvocations.add(replayedInvocation);
            }
        }

        return nonMatchingInvocations;
    }
}
