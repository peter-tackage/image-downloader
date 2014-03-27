package com.moac.android.downloader.acceptance;

import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.view.ViewGroup;

import com.moac.android.downloader.DemoActivity;
import com.moac.android.downloader.R;
import com.robotium.solo.Solo;

import java.util.concurrent.TimeUnit;

import static com.moac.android.downloader.test.RobotiumAsserts.assertVisibilityAfterWait;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.ANDROID.assertThat;

/*
 * Whitebox UI testing
 *
 * TODO Ideally we should have better control over the download duration via mocking
 * TODO The UI doesn't really provide good verification points - you can't determine the download outcome
 * TODO waitForViewVisibility and make fluent: like assertThat(view).after(10, TU.SECONDS).isNotVisible()
 */
public class DownloadControlTest extends ActivityInstrumentationTestCase2<DemoActivity> {

    private static final int DOWNLOAD_DURATION_SEC = 20;
    private static final int DOWNLOAD_CANCEL_TIMEOUT_SEC = 1;

    private Solo solo;

    public DownloadControlTest() throws ClassNotFoundException {
        super(DemoActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        solo = new Solo(getInstrumentation(), getActivity());
        solo.waitForActivity(DemoActivity.class);
    }

    @Override
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }

    /**
     * Verify the action bar contents
     * This is a pretty pointless test as the ActionBar is mostly empty
     */
    public void test_actionBar() {
        // Check that we have the right activity under test
        solo.assertCurrentActivity("Incorrect Activity", DemoActivity.class);

        // Verify the app name and icon are in the Action bar
        assertThat(solo.getText(getInstrumentation().getTargetContext().getString(R.string.app_name))).isVisible();
        assertThat(solo.getView(android.R.id.home)).isVisible().isNotClickable();
    }

    /**
     * Verify the UI removes the progress dialog once downloads have completed
     */
    public void test_successfulDownload() throws Exception {

        // Check that we have the right activity under test
        solo.assertCurrentActivity("Incorrect Activity", DemoActivity.class);

        // Get the image container
        ViewGroup viewGroup1 = (ViewGroup)solo.getView(R.id.vg_demo_pic1);

        // Verify an image is visible and progress indicator not visible
        assertThat(viewGroup1).isVisible();
        assertThat(solo.getView(R.id.vg_progress_indicator_1)).isNotVisible();

        // Click on the image to start downloading
        solo.clickOnView(viewGroup1);

        // Verify the image's progress indicator is shown
        assertThat(solo.waitForView(R.id.vg_progress_indicator_1));
        assertThat(solo.getView(R.id.vg_progress_indicator_1)).isVisible();
        assertThat(solo.searchText(getInstrumentation().getTargetContext().getString(R.string.tap_to_cancel)));

        // Verify progress indicator removed once download is complete
        assertVisibilityAfterWait(solo, R.id.vg_progress_indicator_1, View.GONE, DOWNLOAD_DURATION_SEC, TimeUnit.SECONDS);
    }

    /**
     * Verify the UI removes the progress dialog once downloads have been cancelled
     */
    public void test_cancelledDownload() throws Exception {

        // Get the image container
        ViewGroup viewGroup1 = (ViewGroup)solo.getView(R.id.vg_demo_pic1);

        // Verify an image is visible and progress indicator not visible
        assertThat(viewGroup1).isVisible();
        assertThat(solo.getView(R.id.vg_progress_indicator_1)).isNotVisible();

        // Click on the image to start downloading
        solo.clickOnView(viewGroup1);

        // Verify the image's progress indicator is shown
        assertThat(solo.waitForView(R.id.vg_progress_indicator_1));
        assertThat(solo.getView(R.id.vg_progress_indicator_1)).isVisible();
        assertThat(solo.searchText(getInstrumentation().getTargetContext().getString(R.string.tap_to_cancel)));

        // Click on the image to cancel downloading
        solo.clickOnView(viewGroup1);

        // Verify progress indicator removed once download is complete
        assertVisibilityAfterWait(solo, R.id.vg_progress_indicator_1, View.GONE, DOWNLOAD_CANCEL_TIMEOUT_SEC, TimeUnit.SECONDS);
    }

}
