package com.moac.android.downloader.acceptance;

import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.moac.android.downloader.DemoActivity;
import com.moac.android.downloader.R;
import com.robotium.solo.Solo;


import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.moac.android.downloader.test.RobotiumAsserts.assertVisibilityAfterWait;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.ANDROID.assertThat;

/*
 * Whitebox UI testing
 */
public class DownloadControlTest extends ActivityInstrumentationTestCase2<DemoActivity> {

    private Solo solo;

    @SuppressWarnings("unchecked")
    public DownloadControlTest() throws ClassNotFoundException {
        super(DemoActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        solo = new Solo(getInstrumentation(), getActivity());
    }

    @Override
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }

    /**
     * Verify the Group List contains a default created Group
     */
    public void testGroupManagementAcceptance() throws Exception {

        // Check that we have the right activity under test
        solo.assertCurrentActivity("Incorrect Activity", DemoActivity.class);

        // Check we have the expected image containers
        List<ImageView> imageContainers = solo.getCurrentViews(ImageView.class);
        ViewGroup viewGroup1 = (ViewGroup)solo.getView(R.id.vg_demo_pic1);
        ViewGroup viewGroup2 = (ViewGroup)solo.getView(R.id.vg_demo_pic2);
        assertThat(viewGroup1).isVisible();
        assertThat(viewGroup2).isVisible();

        // Click on the first image
        viewGroup1.callOnClick();

        // Wait for indication that it has started to load (requires communication to Service)
        assertThat(solo.waitForView(R.id.vg_progress_indicator_1));

        // Click on the second image
        viewGroup2.callOnClick();
        assertThat(solo.waitForView(R.id.vg_progress_indicator_2));

        assertVisibilityAfterWait(solo, R.id.vg_progress_indicator_1, View.GONE, 20, TimeUnit.SECONDS);
        assertVisibilityAfterWait(solo, R.id.vg_progress_indicator_2, View.GONE, 20, TimeUnit.SECONDS);

        // TODO waitForViewVisibility and make fluent
    }

}
