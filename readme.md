# Summary

Synchronizes contacts of co-workers.

The minimum supported version of platform is 2.3.

# Develop

To work on this project you can use these tools: [Git][tool:git], [Maven][tool:maven], [Android Configurator for Eclipse][tool:android.m2e] and [Android SDK Tools][tool:android.sdk].

To build application using Maven, follow next steps:

1. Install Android SDK and update it.
1. Create environment variable `ANDROID_HOME`, which points to directory with Android SDK.
1. Build project using command `mvn clean package`.
1. Deploy application using command `mvn android:deploy`.

If you use [Eclipse][tool:eclipse], follow next steps:

1. Install Eclipse.
1. Install Android Configurator for Eclipse.
1. Import project using `File > Import > Maven > Existing Maven Projects`.
1. Run application using `Run > Run As > Android Application`.

[tool:git]: http://git-scm.com/
[tool:maven]: http://maven.apache.org/
[tool:android.m2e]: http://rgladwell.github.io/m2e-android/
[tool:android.sdk]: http://developer.android.com/sdk/
[tool:eclipse]: http://www.eclipse.org/

# Use

## Install and Run

1. Install application.
1. Go to `Settings > Accounts > Add Account` (or `Contacts -> Accounts -> Add Account`).
1. Select `Coworkers`.
1. Enter your username and password.
1. Press button `Sign In`.

Application will automatically synchronize contacts of coworkers.

## Settings

1. `Group` - change title for group.
1. `Photos` - select how to sync photos.
1. `Native Names` - select language for names.
1. `Frequency` - select frequency of synchronization.
1. `Notifications` - enable or disable notifications.

# License

[Apache Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)
