package com.moac.android.downloader.download;

public class DefaultTransitioner implements Transitioner {

    @Override
    public boolean isAllowed(Status fromStatus, Status toStatus) {
        boolean isAllowed;
        switch (fromStatus) {
            case UNKNOWN:
            case CANCELLED:
            case SUCCESSFUL:
            case FAILED:
                // Only support a restart from a finished state
                isAllowed = toStatus == Status.CREATED || toStatus == Status.PENDING
                        || fromStatus == toStatus;
                break;
            case CREATED:
            case PENDING:
            case RUNNING:
                // Don't support move back for an in-progress request
                isAllowed = toStatus.ordinal() > fromStatus.ordinal();
                break;
            default:
                throw new IllegalStateException("Unhandled request state: " + fromStatus);
        }
        return isAllowed;
    }
}
