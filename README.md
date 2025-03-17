Biometric Authentication

Overview:
This is an assessment based on building the UI via Jetpack Compose, where user can enter
his email. There are 2 files that are mainly used. Once is for UI, and other is for the
biometric confirmation. The biometric confirmation uses fingerprint and the HMACSHA256
is used for the verification purposes. Also, the retrofit calls are made to send the userId,
token that is generated to the backend, and we also collect the success or failure from the
backend.

Features:
Email Input Validation: Ensures correct email format before proceeding.
Biometric Authentication: Uses fingerprint or face authentication.
Token Generation: Generates a secure token upon successful authentication.
API Integration: Sends authentication requests to a backend server.
Jetpack Compose UI: Modern UI implementation using Compose.
Technologies Used:
Kotlin (Android development)
Jetpack Compose (UI framework)
Android Biometric API (Fingerprint/Face authentication)
Retrofit (API communication)
HMAC-SHA256 Encryption (Token security)

How It Works
User Input & Validation:
The user enters an email in the EmailUI screen.
The email is validated using a regex pattern.
Biometric Authentication:
If the email is valid, the user proceeds to biometric authentication.
The app checks if the device supports biometrics.
If authentication succeeds, a secure token is generated.
Token Generation & API Call
The token contains userId, deviceId, and expiration time.
The token is HMAC-SHA256 encrypted for security.
The token is sent to the backend via Retrofit API.
