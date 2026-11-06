# SafeHer – Women Safety SOS Alert and Safe Route Helper

## Project Description

SafeHer is an Android application developed using Java that helps improve women's safety by providing emergency SOS alerts, trusted contact management, location sharing, and nearby safe place assistance. It helps users quickly seek support and share their location during emergency situations.

## Features

- Emergency SOS alert system
- Trusted contact management (Add, Edit, Delete)
- Emergency SMS composer with location details
- GPS and network-based location retrieval
- Share current location through Google Maps
- Find nearby police stations, hospitals, and pharmacies
- SOS history management
- Local data storage using SharedPreferences
- Firebase Realtime Database integration
- Firebase Anonymous Authentication
- User-friendly Android interface

## Technologies Used

- Java
- XML (Android UI Design)
- Android Studio
- Firebase Realtime Database
- Firebase Authentication
- Google Maps
- SharedPreferences
- Android Location Services

## Main Modules

- **Dashboard:** Provides access to all application features.
- **SOS Alert:** Prepares emergency messages for trusted contacts.
- **Trusted Contacts:** Manages emergency contact details.
- **Location Sharing:** Shares a location snapshot through Google Maps.
- **Safe Places:** Searches for nearby police stations, hospitals, and pharmacies.
- **SOS History:** Displays locally saved SOS alert records.

## How to Run

1. Clone the repository:

   `git clone https://github.com/gangulamani70-bit/SafeHer-Women-Safety-App.git`

2. Open the project in Android Studio.

3. Configure Firebase:
    - Create or open your Firebase project.
    - Add the Android application with package name `com.example.safeher`.
    - Place `google-services.json` inside the `app/` folder.
    - Enable Firebase Authentication and Realtime Database.

4. Sync the project with Gradle files.

5. Connect an Android device or start an emulator.

6. Click **Run** in Android Studio.

## Output

The application displays a welcome screen and dashboard with SOS alerts, trusted contacts, location sharing, nearby safe places, and SOS history. Users can prepare emergency SMS messages and share their location with trusted contacts.

## Note

- SOS messages open the phone's SMS application and require the user to press Send.
- Location sharing uses the last-known available location and may not always represent the current location.
- Safe Places uses Google Maps search to find nearby facilities.
- SOS history records the alert initiation process; it does not confirm SMS delivery.
- Firebase security rules must be properly configured before production use.