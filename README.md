# Drone3D
[![Build Status](https://api.cirrus-ci.com/github/Drone3D-Team/Drone3D.svg)](https://cirrus-ci.com/github/Drone3D-Team/Drone3D)
[![Maintainability](https://api.codeclimate.com/v1/badges/7d12c4fd472569490788/maintainability)](https://codeclimate.com/github/Drone3D-Team/Drone3D/maintainability)
[![Test Coverage](https://api.codeclimate.com/v1/badges/7d12c4fd472569490788/test_coverage)](https://codeclimate.com/github/Drone3D-Team/Drone3D/test_coverage)

[![Drone3D trailer](./images/trailer.png)](https://youtu.be/HngOc-b1naM)

## Table of contents
* [Description](#description)
* [Usage and features](#usage-and-features)
* [Set up](#set-up)
* [Development](#development)
* [License](#license)

## Description
Drone3D is a mobile application that lets you plan and execute drone missions to take pictures of a location. These photos can then be used to reconstruct a 3D model of the area. You have the possibility to share the missions you've planned with others or see their missions. This app can be used with any drone or simulation supported by [MAVSDK](https://mavsdk.mavlink.io/main/en/index.html).
## Usage and features
<img src="images/mainMenu.png" width="200">

### Register and login
<img src="images/register.jpg" width="200"> <img src="images/login.jpg" width="200">

You can create an account or log into an existing one with an email and a password. Once you are logged-in you'll be able to store and share missions.

### Connect a drone or a simulation
<img src="images/connectSimulation.jpg" width="200"> 

To connect a drone you'll have to power up your drone, set it up to communicate through the udp://:14540 port, click on the "connect a drone" button, it will open the port and search for the drone for three seconds before timing out. Repeat the operation as many time as needed.
To connect a simulation, you'll first have to create a simulation with a tool such as [auterion](https://suite.auterion.com/login?returnUrl=%2Fsimulations) and then enter the corresponding IP and port, click on the "connect a simulation" button. It can take a few seconds.

### Manage offline map
<img src="images/offlineMap.jpg" width="200"> 

This page lets you download a part of the map. Select the area you want to save by zooming on the map. Once you have it on your screen, press the save button, give it a name and download it, depending on the size of the area it can take a few seconds. You have space to download up to 6000 tiles, the number of tiles can vary based on the size of the area and the precision of the map. If you no longer need a previously saved area and want to free some space, you can delete it by pressing the corresponding delete button.

### Create and save an itinerary 
<img src="images/createItinerary.png" width="200"> <img src="images/createMission.png" width="200"> 

This page contains all the tools needed to create a mission:
* You can select the area for which you want to create a 3D model by adding three points on the map (click on the map to add a point).
* Choose your flight altitude with the vertical bar on the left. (Be sure to be above any obstacle!)
* Select your strategy on the right with the top button. Either single or double path strategy are implemented.
* Preview the path your drone will take. (To have exact path, be sure to be already connected to your drone, default settings will be used otherwise.)
* Hide/show the path once you have generated it with the previous button.
* Delete the area and start again in case you changed your mind.
* Save and/or share the mapping mission (only if you are logged-in).

If you're not logged-in, you won't be able to save the mission but you can still launch it.

<img src="images/saveMission.png" width="200"> 

To save a mission, you'll need to give it a name and select if you want to store it privately, share it or both.

### Browse itineraries
<img src="images/browseItinerary.png" width="200"> 

If you are logged-in, you can find all your previously saved mission as well as access the missions others have shared. If you want to delete any of your mission that is either private or shared, click on it and press on the delete button.

In case you are not logged-in, you can still access shared missions.

You can do term related search with the top search bar to find the mission you are looking for. Pressing any of the mission will open the page that lets you launch it.

### Launch a mission
<img src="images/itineraryShow.png" width="200"> <img src="images/weatherInfo.png" width="200"> 

To be able to launch a mission you'll need to be connected to a drone or a simulation and to have a mission ready. You can either create a new mission or select one in the saved ones. You have the possibility to check the weather at the location of the mission. You'll get an alert if the weather is considered not good enough to fly your drone. You simply have to click on the drone icon button to launch your mission. You'll have a loading screen during which the drone will receive and start the mission.

<img src="images/startMission.png" width="200"> <img src="images/missionInProgress.png" width="160"> 

Once the mission is dispatched to the drone, you'll be able to follow it live on the map. You'll also have the speed, height, live camera feed, state and battery of the drone. At any time you can call the drone back to you or to the take off location.

## Set-up
This app uses [Mapbox](https://www.mapbox.com) and [OpenWeather](https://openweathermap.org/api) APIs. To be able to have a working build of Drone3D, you will have to set up your own keys. We explain in this section how to do that. Keep in mind that you don't want to share your private keys, be careful!

### Mapbox
You'll first have to login or create an account [here](https://account.mapbox.com/auth/signin/). Update "mapbox_access_token" with your default public token in [strings.xml](app/src/main/res/values/strings.xml).
```
    <string name="mapbox_access_token">PASTE YOUR PUBLIC TOKEN HERE</string>
```
Then, you can create a private token following the official documentation. The only secret scope you will need is "DOWNLOAD:READ". Once your token has been created, you can set it up in [build.gradle](./build.gradle) under password:
```
   credentials {
                // This should always be `mapbox` (not the real username).
                username = 'mapbox'
                //Use Mapbox's secret key
                password = PASTE YOUR PRIVATE KEY HERE
            }
```

### OpenWeather
You'll first have to login or create an account [here](https://home.openweathermap.org/users/sign_in). Then under "API keys" create a new key and copy it. All you have to do is add a new file secrets.xml in [res/values](app/src/main/res/values/) with the following code:
```
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <item name="open_weather_api_key" type="string">PASTE YOUR KEY HERE</item>
</resources>
```

## Development
Drone3D was developed as part of the CS-306 course "Software development project" at EPFL. We have followed agile development methods by adopting the scrum framework. You can find our scrum board [here](https://github.com/Drone3D-Team/Drone3D/projects/1). We have divided the semester into sprints of one week each. For each sprint, we assigned ourselves tasks. Once a task has been implemented, tested and merged into the main branch, it is moved to the corresponding "Done is sprint X" column. At the end of the week, a presentation of the application's functionality takes place, as well as a review of the last sprint. We summarize the work done in each sprint in a sprint summary available in our [wiki](https://github.com/Drone3D-Team/Drone3D/wiki).

We had a few requirements for this app:

### Correctness & Utility
Drone3D lets users plan and execute mission to create 3D model of a area.

### Split app model
Our app uses firebase to let the users store and share their mission.

### Sensor usage
Every map in the app is zoomed on the user, based on his GPS location. Users can follow the live progression of the drone on the map when a mission is launched. At any time they can send an instruction so that the drone comes back to their location.

### User support
We use an email/password registration and login system. Logged-in users have the possibility to store and share their mission. The app is still usable without being logged-in.

### Local cache & offline mode 
Users have the possibility to download parts on the map before entering the mission site in case there is little or no connection there. They can also store and share mapping missions locally. It will be synchronized with the online database as soon as they regain connection.

### Testing
Our code is carefully tested. It has the following stats:
[![Build Status](https://api.cirrus-ci.com/github/Drone3D-Team/Drone3D.svg)](https://cirrus-ci.com/github/Drone3D-Team/Drone3D)
[![Maintainability](https://api.codeclimate.com/v1/badges/7d12c4fd472569490788/maintainability)](https://codeclimate.com/github/Drone3D-Team/Drone3D/maintainability)
[![Test Coverage](https://api.codeclimate.com/v1/badges/7d12c4fd472569490788/test_coverage)](https://codeclimate.com/github/Drone3D-Team/Drone3D/test_coverage)

## License

[GNU Affero General Public License version 3](/LICENSE.txt)

Drone3D a mobile application that helps you plan drone flights to build 3D model of a scene.
Copyright (C) 2021  Drone3D-Team

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/agpl-3.0.html>.
