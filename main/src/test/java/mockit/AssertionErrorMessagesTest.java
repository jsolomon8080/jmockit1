package mockit;

import mockit.internal.expectations.invocation.MissingInvocation;
import mockit.internal.expectations.invocation.UnexpectedInvocation;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * The Class AssertionErrorMessagesTest.
 */
public final class AssertionErrorMessagesTest {

    /** The thrown. */
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    /**
     * The Class Collaborator.
     */
    static class Collaborator {

        /**
         * Do something.
         */
        void doSomething() {
        }

        /**
         * Do something.
         *
         * @param i
         *            the i
         * @param s
         *            the s
         */
        void doSomething(@SuppressWarnings("unused") int i, @SuppressWarnings("unused") String s) {
        }

        /**
         * Do something else.
         *
         * @param s
         *            the s
         */
        void doSomethingElse(@SuppressWarnings("unused") String s) {
        }
    }

    /** The mock. */
    @Mocked
    Collaborator mock;

    /**
     * Unexpected invocation for recorded expectation.
     */
    @Test
    public void unexpectedInvocationForRecordedExpectation() {
        new Expectations() {
            {
                mock.doSomething(anyInt, anyString);
                times = 1;
            }
        };

        thrown.expect(UnexpectedInvocation.class);
        thrown.expectMessage("Unexpected invocation to");
        thrown.expectMessage("doSomething(2, \"xyz\")");

        mock.doSomething(1, "Abc");
        mock.doSomething(2, "xyz");
    }

    /**
     * Unexpected invocation where expecting another for recorded expectations.
     */
    @Test
    public void unexpectedInvocationWhereExpectingAnotherForRecordedExpectations() {
        mock.doSomething(1, "Abc");
        mock.doSomething(2, "xyz");
        mock.doSomethingElse("test");

        thrown.expect(UnexpectedInvocation.class);
        thrown.expectMessage("doSomething(2, \"xyz\"");

        new VerificationsInOrder() {
            {
                mock.doSomething(anyInt, anyString);
                times = 1;
                mock.doSomethingElse(anyString);
            }
        };
    }

    /**
     * Unexpected invocation for recorded expectation with maximum invocation count of zero.
     */
    @Test
    public void unexpectedInvocationForRecordedExpectationWithMaximumInvocationCountOfZero() {
        new Expectations() {
            {
                mock.doSomething(anyInt, anyString);
                times = 0;
            }
        };

        thrown.expect(UnexpectedInvocation.class);
        thrown.expectMessage("1, \"Abc\"");

        mock.doSomething(1, "Abc");
    }

    /**
     * Unexpected invocation for verified expectation.
     */
    @Test
    public void unexpectedInvocationForVerifiedExpectation() {
        mock.doSomething(123, "Test");
        mock.doSomethingElse("abc");

        thrown.expect(UnexpectedInvocation.class);
        thrown.expectMessage("123, \"Test\"");

        new Verifications() {
            {
                mock.doSomething(123, anyString);
                times = 0;
            }
        };
    }

    /**
     * Unexpected invocation for expectations verified in order.
     */
    @Test
    public void unexpectedInvocationForExpectationsVerifiedInOrder() {
        mock.doSomethingElse("test");
        mock.doSomething(123, "Test");

        thrown.expect(UnexpectedInvocation.class);
        thrown.expectMessage("123, \"Test\"");

        new VerificationsInOrder() {
            {
                mock.doSomethingElse(anyString);
                mock.doSomething(anyInt, anyString);
                times = 0;
            }
        };
    }

    /**
     * Unexpected invocation on method with no parameters.
     */
    @Test
    public void unexpectedInvocationOnMethodWithNoParameters() {
        new Expectations() {
            {
                mock.doSomethingElse(anyString);
            }
        };

        mock.doSomething();

        thrown.expect(UnexpectedInvocation.class);
        thrown.expectMessage("doSomething()\n   on mock instance");
        new FullVerifications(mock) {
        };
    }

    /**
     * Missing invocation for recorded expectation.
     */
    @Test
    public void missingInvocationForRecordedExpectation() {
        new Expectations() {
            {
                mock.doSomething(anyInt, anyString);
                times = 2;
            }
        };

        thrown.expect(MissingInvocation.class);
        thrown.expectMessage("any int, any String");

        mock.doSomething(123, "Abc");
    }

    /**
     * Missing invocation for recorded expectation which gets non matching invocations at replay time.
     */
    @Test
    public void missingInvocationForRecordedExpectationWhichGetsNonMatchingInvocationsAtReplayTime() {
        new Expectations() {
            {
                mock.doSomethingElse("test");
            }
        };

        thrown.expect(MissingInvocation.class);
        thrown.expectMessage("doSomethingElse(\"test\")");
        thrown.expectMessage("instead got:");
        thrown.expectMessage("doSomethingElse(\"Abc\")");
        thrown.expectMessage("doSomethingElse(\"\")");

        mock.doSomethingElse("Abc");
        mock.doSomething(1, "xy");
        mock.doSomethingElse("");
    }

    /**
     * Missing invocation for verified expectation.
     */
    @Test
    public void missingInvocationForVerifiedExpectation() {
        thrown.expect(MissingInvocation.class);
        thrown.expectMessage("123, any String");

        new Verifications() {
            {
                mock.doSomething(123, anyString);
            }
        };
    }

    /**
     * Missing invocation for verified expectation which gets non matching invocations at replay time.
     */
    @Test
    public void missingInvocationForVerifiedExpectationWhichGetsNonMatchingInvocationsAtReplayTime() {
        mock.doSomethingElse("Abc");
        mock.doSomething(1, "xy");
        mock.doSomethingElse("");

        thrown.expect(MissingInvocation.class);
        thrown.expectMessage("doSomethingElse(\"test\")");
        thrown.expectMessage("instead got:");
        thrown.expectMessage("doSomethingElse(\"Abc\")");
        thrown.expectMessage("doSomethingElse(\"\")");

        new Verifications() {
            {
                mock.doSomethingElse("test");
            }
        };
    }

    /**
     * Missing invocation for expectation verified in order.
     */
    @Test
    public void missingInvocationForExpectationVerifiedInOrder() {
        mock.doSomething(123, "Test");

        thrown.expect(MissingInvocation.class);
        thrown.expectMessage("any int, any String");

        new VerificationsInOrder() {
            {
                mock.doSomething(anyInt, anyString);
                minTimes = 3;
            }
        };
    }

    /**
     * Missing invocation for fully verified expectations.
     */
    @Test
    public void missingInvocationForFullyVerifiedExpectations() {
        mock.doSomething(123, "Abc");

        thrown.expect(MissingInvocation.class);
        thrown.expectMessage("any int, any String");

        new FullVerifications() {
            {
                mock.doSomething(anyInt, anyString);
                times = 2;
            }
        };
    }

    /**
     * Missing invocation for expectation using matcher for different parameter type.
     */
    @Test
    public void missingInvocationForExpectationUsingMatcherForDifferentParameterType() {
        mock.doSomething(5, "");

        thrown.expect(MissingInvocation.class);
        thrown.expectMessage("any char");

        new Verifications() {
            {
                mock.doSomething(anyChar, "");
            }
        };
    }
}
