#!/usr/bin/python           # This is server.py file

import platform
import os
import socket               # Import socket module
import psutil
import time
import pyautogui


PAUSE = 'Play/Pause'
BACKWARD = 'Left'
FORWARD = 'Right'
PREVIOUS = 'Hard-Left'
NEXT = 'Hard-Right'

apps = []
if platform.system() == 'Windows':
    import pywinauto
    app = pywinauto.application.Application()
    apps = ['AcroRd32.exe', 'vlc.exe']  # in order of priority
elif platform.system() == 'Linux' or platform.system() == 'Darwin':
    apps = ['evince', 'vlc']

s = socket.socket()  # Create a socket object
# host = socket.gethostname() # Get local machine name
try:
    host = ((([ip for ip in socket.gethostbyname_ex(socket.gethostname())[2] if not ip.startswith("127.")] or [
        [(s.connect(("8.8.8.8", 53)), s.getsockname()[0], s.close()) for s in
        [socket.socket(socket.AF_INET, socket.SOCK_DGRAM)]][0][1]]) + ["no IP found"])[0])
    # host=os.popen("hostname -I").read().split()[0]
    # host = "192.168.0.107"
    print(("host ip is: " + host))
except:
    print("Please connect to a network")
    exit()
else:
    port = 12346  # Reserve a port for your service.
    s.bind((host, port))  # Bind to the port
    s.listen(5)  # Now wait for client connection.


def get_target_process():
    try:
        all_running_processes = psutil.pids()
        target_pid=-1
        target_name=""
        for app in apps:
            for pid in all_running_processes:
                if psutil.Process(pid).name()==app:
                    target_pid = pid
                    target_name = app
                    break
            if target_pid!=-1:
                break
        return (target_pid,target_name)
    except:
        return (-1,"")


def handle_signal_posix(signal_input,target_pid,target_name):
    try:
        if target_pid==-1:
            return
        window_id = os.popen("xdotool search --name "+ target_name).read().split()[-1]
        if target_name=='evince':
            pass
        elif target_name=='vlc':
            if signal_input==PAUSE:
                os.popen("xdotool windowactivate "+window_id+" && sleep 0.01 && xdotool key space && xdotool windowminimize "+window_id)
            elif signal_input==BACKWARD:
                os.popen("xdotool windowactivate "+window_id+" && sleep 0.01 && xdotool key shift+Right && xdotool windowminimize "+window_id)
            elif signal_input==FORWARD:
                os.popen("xdotool windowactivate "+window_id+" && sleep 0.01 && xdotool key shift+Left && xdotool windowminimize "+window_id)
            elif signal_input==PREVIOUS:
                os.popen("xdotool windowactivate "+window_id+" && sleep 0.01 && xdotool key p && xdotool windowminimize "+window_id)
            elif signal_input==NEXT:
                os.popen("xdotool windowactivate "+window_id+" && sleep 0.01 && xdotool key n && xdotool windowminimize "+window_id)
    except:
        pass


if os.name == 'nt':
    import pywinauto

    def handle_signal_nt(signal_input, target_pid, target_name):
        try:
            if target_pid == -1:
                return
            app.connect(path=target_name)
            app_dialog = app.top_window_()
            app_dialog.Minimize()
            app_dialog.Restore()
            time.sleep(0.01)
            if target_name == 'AcroRd32.exe':
                if signal_input == BACKWARD:
                    pyautogui.press('left')
                elif signal_input == FORWARD:
                    pyautogui.press('right')
                elif signal_input == PREVIOUS:
                    pyautogui.press('left')
                    pyautogui.press('left')
                    pyautogui.press('left')
                    pyautogui.press('left')
                elif signal_input == NEXT:
                    pyautogui.press('right')
                    pyautogui.press('right')
                    pyautogui.press('right')
                    pyautogui.press('right')
            elif target_name == 'vlc.exe':
                if signal_input == PAUSE:
                    pyautogui.press('space')
                elif signal_input == BACKWARD:
                    pyautogui.hotkey('shift', 'left')
                elif signal_input == FORWARD:
                    pyautogui.hotkey('shift', 'right')
                elif signal_input == PREVIOUS:
                    pyautogui.press('p')
                elif signal_input == NEXT:
                    pyautogui.press('n')
            elif target_name == 'chrome.exe' or target_name == 'firefox.exe':
                if signal_input == PAUSE:
                    pyautogui.press('space')
            time.sleep(0.01)
            app_dialog.Minimize()
        except:
            pass


while True:
    #get the target process
    target_pid, target_name = get_target_process()
    # print(target_pid, target_name)

    #get the input signal from client
    c, addr = s.accept()     # Establish connection with client.
    print(('Got connection from'+ str(addr)))
    signal_input = str(c.recv(1024).decode("UTF-8"))[2:]
    print(signal_input)
    # b = bytes('Thank you for connecting', 'utf-8')
    # c.send(b)
    c.close()                # Close the connection

    #handle the input
    if os.name == 'posix':
        handle_signal_posix(signal_input, target_pid, target_name)
    elif os.name == 'nt':
        handle_signal_nt(signal_input, target_pid, target_name)
