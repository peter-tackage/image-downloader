package com.moac.android.downloader.test;

import android.view.View;

import com.robotium.solo.Condition;
import com.robotium.solo.Solo;

import java.util.concurrent.TimeUnit;

import static org.fest.assertions.api.Assertions.assertThat;

public class RobotiumAsserts {

    public static void assertVisibility(final Solo solo, final int id, final int visibility, final int timeout, final TimeUnit timeUnits) {
        assertThat(solo.waitForCondition(new Condition() {
            @Override
            public boolean isSatisfied() {
                View v = solo.getCurrentActivity().findViewById(id);

                // If the view doesn't exist, then return true if we were expecting GONE
                return v != null ? v.getVisibility() == visibility : visibility == View.GONE;
            }
        }, (int)TimeUnit.MILLISECONDS.convert(timeout, timeUnits)));

    }

    protected RobotiumAsserts() {}
}
