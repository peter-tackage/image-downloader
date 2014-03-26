package com.moac.android.downloader.download;

import android.test.AndroidTestCase;

import static org.fest.assertions.api.Assertions.assertThat;

public class DefaultTransitionerTest extends AndroidTestCase {

    // SUT
    private DefaultTransitioner mTransitioner;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        mTransitioner = new DefaultTransitioner();
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        mTransitioner = null;
    }

    /*
     * Test all transitions from the UNKNOWN request state
     */
    public void test_isMovePermittedFromUnknownState() {
        assertThat(mTransitioner.isAllowed(Status.UNKNOWN, Status.UNKNOWN)).isTrue();
        assertThat(mTransitioner.isAllowed(Status.UNKNOWN, Status.CREATED)).isTrue();
        assertThat(mTransitioner.isAllowed(Status.UNKNOWN, Status.PENDING)).isTrue();
        assertThat(mTransitioner.isAllowed(Status.UNKNOWN, Status.RUNNING)).isFalse();
        assertThat(mTransitioner.isAllowed(Status.UNKNOWN, Status.CANCELLED)).isFalse();
        assertThat(mTransitioner.isAllowed(Status.UNKNOWN, Status.SUCCESSFUL)).isFalse();
        assertThat(mTransitioner.isAllowed(Status.UNKNOWN, Status.FAILED)).isFalse();
    }

    /*
     * Test all transitions from the CREATED request state
     */
    public void test_isMovePermittedFromCreatedState() {
        assertThat(mTransitioner.isAllowed(Status.CREATED, Status.UNKNOWN)).isFalse();
        assertThat(mTransitioner.isAllowed(Status.CREATED, Status.CREATED)).isFalse();
        assertThat(mTransitioner.isAllowed(Status.CREATED, Status.PENDING)).isTrue();
        assertThat(mTransitioner.isAllowed(Status.CREATED, Status.RUNNING)).isTrue();
        assertThat(mTransitioner.isAllowed(Status.CREATED, Status.CANCELLED)).isTrue();
        assertThat(mTransitioner.isAllowed(Status.CREATED, Status.SUCCESSFUL)).isTrue();
        assertThat(mTransitioner.isAllowed(Status.CREATED, Status.FAILED)).isTrue();
    }

    /*
     * Test all transitions from the PENDING request state
     */
    public void test_isMovePermittedFromPendingState() {
        assertThat(mTransitioner.isAllowed(Status.PENDING, Status.UNKNOWN)).isFalse();
        assertThat(mTransitioner.isAllowed(Status.PENDING, Status.CREATED)).isFalse();
        assertThat(mTransitioner.isAllowed(Status.PENDING, Status.PENDING)).isFalse();
        assertThat(mTransitioner.isAllowed(Status.PENDING, Status.RUNNING)).isTrue();
        assertThat(mTransitioner.isAllowed(Status.PENDING, Status.CANCELLED)).isTrue();
        assertThat(mTransitioner.isAllowed(Status.PENDING, Status.SUCCESSFUL)).isTrue();
        assertThat(mTransitioner.isAllowed(Status.PENDING, Status.FAILED)).isTrue();
    }

    /*
     * Test all transitions from the RUNNING request state
     */
    public void test_isMovePermittedFromRunningState() {
        assertThat(mTransitioner.isAllowed(Status.RUNNING, Status.UNKNOWN)).isFalse();
        assertThat(mTransitioner.isAllowed(Status.RUNNING, Status.CREATED)).isFalse();
        assertThat(mTransitioner.isAllowed(Status.RUNNING, Status.PENDING)).isFalse();
        assertThat(mTransitioner.isAllowed(Status.RUNNING, Status.RUNNING)).isFalse();
        assertThat(mTransitioner.isAllowed(Status.RUNNING, Status.CANCELLED)).isTrue();
        assertThat(mTransitioner.isAllowed(Status.RUNNING, Status.SUCCESSFUL)).isTrue();
        assertThat(mTransitioner.isAllowed(Status.RUNNING, Status.FAILED)).isTrue();
    }

    /*
     * Test all transitions from the CANCELLED request state
     */
    public void test_isMovePermittedFromCancelledState() {
        assertThat(mTransitioner.isAllowed(Status.CANCELLED, Status.UNKNOWN)).isFalse();
        assertThat(mTransitioner.isAllowed(Status.CANCELLED, Status.CREATED)).isTrue();
        assertThat(mTransitioner.isAllowed(Status.CANCELLED, Status.PENDING)).isTrue();
        assertThat(mTransitioner.isAllowed(Status.CANCELLED, Status.RUNNING)).isFalse();
        assertThat(mTransitioner.isAllowed(Status.CANCELLED, Status.CANCELLED)).isTrue();
        assertThat(mTransitioner.isAllowed(Status.CANCELLED, Status.SUCCESSFUL)).isFalse();
        assertThat(mTransitioner.isAllowed(Status.CANCELLED, Status.FAILED)).isFalse();
    }

    /*
    * Test all transitions from the SUCCESSFUL request state
    */
    public void test_isMovePermittedFromSuccessfulState() {
        assertThat(mTransitioner.isAllowed(Status.SUCCESSFUL, Status.UNKNOWN)).isFalse();
        assertThat(mTransitioner.isAllowed(Status.SUCCESSFUL, Status.CREATED)).isTrue();
        assertThat(mTransitioner.isAllowed(Status.SUCCESSFUL, Status.PENDING)).isTrue();
        assertThat(mTransitioner.isAllowed(Status.SUCCESSFUL, Status.RUNNING)).isFalse();
        assertThat(mTransitioner.isAllowed(Status.SUCCESSFUL, Status.CANCELLED)).isFalse();
        assertThat(mTransitioner.isAllowed(Status.SUCCESSFUL, Status.SUCCESSFUL)).isTrue();
        assertThat(mTransitioner.isAllowed(Status.SUCCESSFUL, Status.FAILED)).isFalse();
    }

    /*
     * Test all transitions from the FAILED request state
     */
    public void test_isMovePermittedFromFailedState() {
        assertThat(mTransitioner.isAllowed(Status.FAILED, Status.UNKNOWN)).isFalse();
        assertThat(mTransitioner.isAllowed(Status.FAILED, Status.CREATED)).isTrue();
        assertThat(mTransitioner.isAllowed(Status.FAILED, Status.PENDING)).isTrue();
        assertThat(mTransitioner.isAllowed(Status.FAILED, Status.RUNNING)).isFalse();
        assertThat(mTransitioner.isAllowed(Status.FAILED, Status.CANCELLED)).isFalse();
        assertThat(mTransitioner.isAllowed(Status.FAILED, Status.SUCCESSFUL)).isFalse();
        assertThat(mTransitioner.isAllowed(Status.FAILED, Status.FAILED)).isTrue();
    }

}
