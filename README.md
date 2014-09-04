Image Downloader
================

The core is implemented as an Android Service backed by a thread-pool to provide concurrent downloads of media independently of the Activity lifecycle.

The test client allows you to tap on a preview image to download its full-sized remote equivalent to your Pictures folder. To cancel the download, tap again on the image while the progress indicator is being displayed.

The test client is responsible for specifying the exact location of the remote image and the local path and filename to which it should be downloaded. In this case, it has been written to request a new unique filename in the phone's Picture directory for each request.

Each item to be downloaded is identified by a unique download tracking id provided by the client. This could be a API model item id in a real-world app.

The test client Activity binds to the Service during the Activity's onResume() and queries it via the DownloadClient IBinder to determine the state of the downloads at the time. The status of previously finished item is kept by the Service to make it possible in future for completed download status to be displayed by the Activity UI regardless of whether the UI was active at the time the download completed.

The client receives status events as the download progresses using a LocalBroadcastReceiver which filters for the status events generated by the Service. This "Event Bus" style approach decouples the UI elements from Service. Other similar approaches using the GreenRobot and Square's Otto libraries were considered, but were not chosen in this exercise as they would introduce extra dependencies.

When downloads are successfully completed, the Service triggers the MediaScanner to ensure the file is available to other applications.

Status bar notifications are shown for key stages of the download process. This serves to indicate to the user when downloads are in progress when the leave the application or the originating UI component.

The design and the use of dependency injection allows for swapping out of components for testing or other purposes. For example, an alternate DownloadServiceModule could be written that provided classes capable of performing downloads using a different HTTP implementation or perhaps allow for authentication headers to be inserted prior to performing the HTTP GET request.

The Service's download request state model prevents concurrent requests with the same download tracking id. However, successfully completed, fail or cancelled requests can be re-downloaded. Requests that exceed the supported concurrent thread limit are queued by the Service's thread-pool executor.

Areas for further work:

- Status bar notifications UX could be improved by batching notifications and allowing for opening of the media or cancelling of the download from the notification item.
- In-progress downloads should be restarted after the service is restored after being killed by Android
- Improved download mechanics in Job class, better support for mocking of the "heavy lifting" components
- Continuation of partially completed downloads
- Allow for renaming when filenames clash
- Retry for failed downloads, including at the HTTP level.
- Content type support to aid in the use of Intents to display downloaded content