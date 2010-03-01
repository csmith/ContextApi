# Context API

## Project overview

The aim of this project is to produce a context-aware API for Android devices. This will take the form of a set of programs which monitor the state of the device's sensors, and perform activity inference to determine the user's current activity. This activity data is combined with other sources of information such as the user's location, and made available to third-party developers through a public, documented API.

## Current status

At present, there is one Android app -- the SensorLogger -- which records the accelerometer and magnetic field sensors on the device for around a minute, and submits the data to a website along with a user-supplied activity label.

The website then allows administrators to classify the data manually (using the labels as a guide), and this classified data can be exported to a text file which can be downloaded and analysed.

There is then a Java desktop application -- the Extractor -- which takes this exported data, cuts it up into sliding windows, and extracts features from it. The data can then be exported in ARFF format for use in Weka. The feature extraction parts of the extractor are designed to be used in the final Android application.
