# Release Notes Sit-Bit 1.0.0:
## Features:
- Added the login screen and Firebase authentication functionalities.
- Added the registration screen and account registration with Firebase functionalities.
- Added the home screen alongside the displaying of daily and weekly activity graphs. Additionally, the home screen allows the user to start or stop the recording of their sedentary habits.
- Added the goals screen and the ability to set a today’s activity goal and see the user’s progress towards that goal.
- Added the history screen and the ability to see their recorded activity history for a chosen day in the past.
  - Note: Only a month of data is kept at a time to conserve database space. Any set of data older than a month is automatically deleted.
- Added the export screen and the ability to export data from an interval of days to a text document.
- Added the settings screen and the ability to access the “Account Settings” and “Notification Settings” screens. Additionally the user may log out from this screen.
- Added the account settings screen and the ability to update the user’s password or delete the user’s account.
- Added the notification settings screen and the ability to enable or disable app notifications.
## Known Bugs:
- The application and screen must remain active if the user wishes to guarantee frequent accelerometer data retrieval during recording.
  - The hardware and operating system reduce the frequency at which some sensors sample during low power modes (e.g. when the screen is off). It cannot be circumvented.
# SitBit Installation Steps:
1. Have access to a device with Android 4.4 or above installed.
2. Your version of Android can be found in the phone’s settings.
3. Open up a web browser application.
4. Go to the following web address: https://play.google.com/store/apps/details?id=com.app.sitbit
5. If the web browser asks to open the page in the Android Play Store, do this.
6. Click install.
7. Once the application has finished installing, you may begin using the application.
8. To run the application, click on the app from your home screen.

# Accessing Firebase Console:
1. Go to https://console.firebase.google.com/ in your web browser.
2. Sign into your SitBitApp@gmail.com account.
3. Click on the “SitBit” project.
4. On the left hand side, options for “Authentication” and “Database” are available.
5. Click on “Authentication” to view all of the accounts registered for SitBit.
   - Additional options such as account creation and deletion are available here.
6. Click on “Database” and then select “Realtime Database” to view all of the data stored for each registered SitBit user.
   - The format of this stored data is implementation specific and will likely be unhelpful to anyone outside of the SitBit developers.
