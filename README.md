# Cordova Plugin Gdpr Choices (cordova-plugin-gdpr-choices)

Google User Messaging Platform (UMP) SDK gdpr choices. (ONLY Android)

This plugin retrieve the choices made by the user via the Google UMP.

The management of the privacy message via Google UMP is not the subject of this plugin, therefore it must be managed in another way.

## Install on Cordova
```bash
cordova plugin add https://github.com/MrRotella/cordova-plugin-gdpr-choices.git
```

### andoridx preference version
```bash
cordova plugin add https://github.com/MrRotella/cordova-plugin-gdpr-choices.git --variable PREF_VERSION=1.2.0
```

### USE
- isGdpr (promise)
    ```bash
    cordova.Gdpr.isGdpr()
    ```
    - Return JSON object:
        ```bash
        {
            success: true/false (false cannot check),
            value: -1/0/1 (-1 cannot check,0 Gdpr not applies,1 if Gdpr applies),
            message        
        }
        ```
- canShowAds (promise)
    ```bash
    cordova.Gdpr.canShowAds()
    ```
    - Return JSON object:
        ```bash
        {
            success: true/false (false cannot check),
            value: true/false,
            message        
        }
        ```
- canShowPersonalizedAds (promise)
    ```bash
    cordova.Gdpr.canShowPersonalizedAds()
    ```
    - Return JSON object:
        ```bash
        {
            success: true/false (false cannot check),
            value: true/false,
            message        
        }
        ```
    