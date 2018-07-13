# Shake to Switch
Don't you find that bit of a struggle when you are sitting comfortable watching a movie on your laptop and someone disturbs you and you have to reach to your keyboard to pause/rewind the movie?

Or maybe your are working on some pretty important task with VLC/youtube running in the background, and isn't it annoying when you have to switch windows in order to change the music?

Not a music or movie lover? But we're sure it is always a discomfort to reach out to keyboard to go to the next page of a document or a presentation.

This was our motivation behind building this project `Shake to Switch`. We want people to use gestures to interact with their conputers and believe it or not, your mobile device already contains a dozen sensors in it. So we made an application that uses the accelerometer in your mobile to detect flicks and send them over a socket connection to a server running on your computer. 

In simple words:-
* Shake your device softly to the left/right for `backwards/forward` signal.
* Give a hard shake to left/right for `previous/next` signal.
* Shake your device towards you for a `play/pause` signal.

TOADD accelerometer XYZ image

## Getting Started
### Installing
1. The server is a python3 script which requires some modules. You can install them inside a virtual environment as follows:
```bash
git clone https://github.com/mayanksingh2298/shake-to-switch
cd shake-to-switch
python -m venv venv
source venv/bin/activate
pip install requirements.txt (for linux and mac)
pip install requirements_win.txt (for windows) TOADD
```
2. Install the android app on your phone. (apk located at "...\shake_to_switch\ShakeToSwitch\app\release\app-release.apk"

### How to use?
1. Activate the virtual environment on your system.
```
source venv/bin/activate
```
2. Start the server on your computer.
```
python server-detect.py

```
3. Open the application on your device.
4. Enter the `IP` of your system in the application and click on `Connect`. The IP is logged on the terminal when you run the server.
5. That's it. You can give your device a flick to convey signals to the server.

### How it works?
1. Upon receiving a signal, the server scans the applications running on your system for the supported applications and select the one with the highest priority.
2. If that application is not a focussed or active window, we bring that into focus, perform the action and then minimize it.
3. If that application was already in focus, we simply do the required task.

### How to configure?
1. On the app's home screen there is a button which says, "enable this if gestures aren't perfect". You can try that. It basically switches between the Accelerometer senosor and the Linear Acceleration sensor, which rules out gravitational acceleration from measurements.
2. In the android app's settings, you can enable or disable specific gestures.
3. You can change the threshold for each gesture, i.e. how strong flick is required for the gesture to be detected.
4. You can edit the server file to change the priorities, which is line 28 for windows users and line 30 for linux and mac users.

## Some videos
TOADD

## Supported apps
* Linux
  1. evince or better known as the document reader
  2. vlc
  3. chrome
  4. firefox
* Windows
  1. powerpoint
  2. acrobat reader
  3. vlc
  4. chrome
  5. firefox
  

## Import points to remember TOADD
1. Avoid controlling 32 bit apps from 64 bit python and vice versa, for non focus mod only, on windows. (works sometimes, sometimes don't, gives warning on terminal always)
2. Restart browsers like chrome which start multiple processes on startup because sometimes they open a background invisible window which screws up code. 
3. If some gesture isn't working, try resetting the switch in settings.

## Authors
* [Mayank Singh Chauhan](https://www.github.com/mayanksingh2298)
* [Shourya Aggarwal](https://github.com/ShouryaAggarwal)
