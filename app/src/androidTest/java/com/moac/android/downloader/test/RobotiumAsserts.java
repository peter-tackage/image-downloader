package com.moac.android.downloader.test;


import com.robotium.solo.Condition;
import com.robotium.solo.Solo;

import java.util.concurrent.TimeUnit;

import static org.fest.assertions.api.Assertions.assertThat;

public class RobotiumAsserts {

    public static void assertVisibilityAfterWait(final Solo solo, final int id, final int visibility, final int wait, final TimeUnit timeUnits) {
        assertThat(solo.waitForCondition(new Condition() {
            @Override
            public boolean isSatisfied() {
                return solo.getView(id).getVisibility() == visibility;
            }
        }, (int)TimeUnit.MILLISECONDS.convert(wait, timeUnits)));

    }

    protected RobotiumAsserts() {}
}
