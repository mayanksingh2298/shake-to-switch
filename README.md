# shake to switch

Functionality
The project comes with an android app and a computer server, which after setting up, would enable the user to control their desktop applications remotely via gesture flicks on their smartphones.

Currently implemented gestures are - forward/backward, right, left, hard-right, hard-left.

Apps currently supported (in order of priority)
Linux - evince, vlc, chrome(youtube), firefox(youtube)
Windows - powerpoint, adobe reader (32bit), vlc media player, chrome(youtube), firefox(youtube)

## Technical
The android app is developed using android studio and the computer server is scripted using python.


## Getting Started
### Installing
you need python-pip installed on your system to run the server on your system
install virtualenv using pip
clone the repository to your system
set up a python virtual environment in the root directory
navitage to the root directory of project from terminal and activate the virtual environment
run "pip install -r requirements_win.txt" in a windows terminal
run "pip install -r requirements.txt" in a linux/mac terminal
install the application apk file

### How to use?
open any desired system app that you want to control
run "python server-detect.py" from terminal (from root directory of project)
"shake_to_switch\ShakeToSwitch\app\release\app-release.apk" in any android device
open the application from android device and enter the host IP displayed in the system terminal and toggle the connect switch
now you can shake/flick your mobile device to control the system application remotely

### How to configure?
tap the options button in the top right of the home screen of app and navigate to settings
enable/disable the gestures and set threshold according to your ease
use "enable this if gestures aren't perfect" switch to toggle between two different kind of accelerometer sensors and chose one best suited to your needs
in order to customise the priority order of apps, edit line 28(for windows) and line 30(for linux or mac), and reorder written apps in your preferred order.

## Some videos

## Supported apps
Linux - evince, vlc, chrome(youtube), firefox(youtube)
Windows - powerpoint, adobe reader (32bit), vlc media player, chrome(youtube), firefox(youtube)

## Import points to remember
1. Avoid controlling 32 bit apps from 64 bit python and vice versa, for non focus mod only, on windows (works sometimes, sometimes don't, gives warning on terminal always)
2. Restart browsers like chrome which start multiple processes on startup because sometimes they open a background invisible window which screws up code. 
3. If some gesture isn't working, try resetting the switch in settings

## Authors
* [Mayank Singh Chauhan](https://www.github.com/mayanksingh2298)
* [Shourya Aggarwal](https://github.com/ShouryaAggarwal)
