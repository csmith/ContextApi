# Context API

## Project overview

The aim of this project is to produce a context-aware API for Android devices. This will take the form of a set of programs which monitor the state of the device's sensors, and perform activity inference to determine the user's current activity. This activity data is combined with other sources of information such as the user's location, and made available to third-party developers through a public, documented API.

## The ContextAnalyser application

The primary output of this project is the *ContextAnalyser* application. This consists of a background service which samples accelerometer data periodically, and monitors the device's current location using the coarse (network) provider. The accelerometer data is classified into a user *activity*, such as 'walking' or 'travelling by bus'. The location information is used to build a set of known *places* which the user frequents. The service then records *journeys*, sequences of activities which take place in between two known places. This allows it to predict destination based on the activity the user performs in the future.

### Activity classification

Samples from the accelerometer are taken every 50ms, for a total of 128 samples. The mean and range of two axis of the accelerometer are calculated to give four features. These features are then used in a K-Nearest Neighbour algorithm against a model built from training data to determine the activity.

A change in activity results in an ACTIVITY_CHANGED broadcast intent.

### Place identification

Locations are considered the same if they are within 500m of each other. A place is a location in which the device has remained for several consecutive samples of the location data. Each place is assigned a name; this is initially its latitude and longitude, but the service attempts to geocode to a street name once a place has been identified.

A set of known places are exposed via a content provider. A change in location results in a CONTEXT_CHANGED broadcast intent.

### Journey recording

When the device is at an unknown location (i.e., one not yet established as a place), it maintains a log of all activities which occur. When the device then reaches a known place, this log of activities is compiled into a sequence of journey steps (which consist of an activity and number of reptitions of that activity which were observed). Unique journeys are then stored for future use.

## Other applications

Two Android applications were made as a precursor to the ContextAnalyser, in order to test theories and collect data. These were the *SensorLogger*, which records as much sensor data as possible, suggests a classification, and allows the user to accept or correct the classification and submit the results. The source for the website which handles the posted results is included in this repository. The second application is the *ActivityRecorder*, which monitors a minimal amount of sensors, logs activities, and displays a list. It offers no avenue for feedback or corrections, and was used to test the behaviour of the classification aggregator.
