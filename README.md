#know-your-government
Android app that acquires and displays an interactive list of political officials that represent
the current location (or a specified location) at each level of government (federal to local). Google Civic Information
API (https://developers.google.com/civic-information) is used to acquire the government official data (via REST service and JSON results).
- Android location services is used to determine the user’s location
- *AsyncTask* is used to download government official information once location is detected (or user inputs location)
- *Implicit Intents* used to open government official's social media (Twitter, Facebook, Youtube)
