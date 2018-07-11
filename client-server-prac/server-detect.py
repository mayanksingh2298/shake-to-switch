#!/usr/bin/python           # This is server.py file

import platform
import os
import socket               # Import socket module
import psutil
import time
import pyautogui
import subprocess


PAUSE = 'Play/Pause'
BACKWARD = 'Left'
FORWARD = 'Right'
PREVIOUS = 'Hard-Left'
NEXT = 'Hard-Right'
SLEEP_DURATION=0.01

FOCUS_MODE = False

apps = []
if platform.system() == 'Windows':
    import pywinauto
    import win32process
    import win32gui
    from win32com.client import GetObject

    apps = ['POWERPNT.EXE', 'vlc.exe', 'chrome.exe', 'firefox.exe']  # in order of priority
elif platform.system() == 'Linux' or platform.system() == 'Darwin':
    apps = ['evince', 'vlc','chrome','firefox']

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
        target_pid_list=[]
        target_name_list=[]
        is_chrome_added = False
        for pid in all_running_processes:
            for app in apps:
                if psutil.pid_exists(pid) and psutil.Process(pid).name()==app:
                    if app.startswith('chrome') :
                        if psutil.Process(pid).environ()=={} and psutil.Process(pid).cmdline()[0].endswith('chrome') and (not is_chrome_added):
                            target_pid_list.append(pid)
                            target_name_list.append(app)
                            is_chrome_added=True
                        else:
                            continue
                    else:
                        target_pid_list.append(pid)
                        target_name_list.append(app)

        for app in apps:
            if app in target_name_list:
                target_name=app
                target_pid=target_pid_list[target_name_list.index(app)]
                break
        print (target_name)
        return (target_pid,target_name)
    except Exception as e:
        print (str(e))
        return (-1,"")


def simulate_nt(signal_input, target_name):
    if target_name.startswith('POWERPNT'):
        if signal_input == PAUSE:
            pyautogui.hotkey('shift', 'f5')
        elif signal_input == BACKWARD or signal_input == PREVIOUS:
            pyautogui.press('up')
        elif signal_input == FORWARD or signal_input == NEXT:
            pyautogui.press('down')
    elif target_name.startswith('AcroRd32'):
        if signal_input == BACKWARD:
            pyautogui.press('left')
        elif signal_input == FORWARD:
            pyautogui.press('right')
    elif target_name.startswith('evince'):
        if signal_input == BACKWARD or signal_input == PREVIOUS:
            pyautogui.press('p')
        elif signal_input == FORWARD or signal_input == NEXT:
            pyautogui.press('n')
    elif target_name.startswith('vlc'):
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
    elif target_name.startswith('chrome') or target_name.startswith('firefox.exe'):
        if signal_input == PAUSE:
            pyautogui.press('k')
        elif signal_input == BACKWARD:
            pyautogui.press('j')
        elif signal_input == FORWARD:
            pyautogui.press('l')
        elif signal_input == NEXT:
            pyautogui.hotkey('shift', 'n')
        elif signal_input == PREVIOUS:
            pyautogui.hotkey('shift', 'p')


def handle_signal_posix(signal_input, target_pid, target_name):
    try:
        if target_pid == -1:
            return
        window_id = os.popen("xdotool search --pid " + str(target_pid)).read().split()[-1]

        if not FOCUS_MODE:
            os.popen("xdotool windowactivate "+window_id)
            time.sleep(SLEEP_DURATION)

        # simulate_nt(signal_input,target_name)

        if target_name == 'evince':
            if signal_input == FORWARD or signal_input == NEXT:
                os.popen("xdotool key n")
            elif signal_input == BACKWARD or signal_input == PREVIOUS:
                os.popen("xdotool key p")

        elif target_name == 'vlc':
            if signal_input == PAUSE:
                os.popen("xdotool key space")
            elif signal_input == BACKWARD:
                os.popen("xdotool key shift+Left")
            elif signal_input == FORWARD:
                os.popen("xdotool key shift+Right")
            elif signal_input == PREVIOUS:
                os.popen("xdotool keky p")
            elif signal_input == NEXT:
                os.popen("xdotool key n")

        elif target_name == 'chrome' or target_name == 'firefox':
            if signal_input == PAUSE:
                os.popen("xdotool key k")
            elif signal_input == FORWARD or signal_input == NEXT:
                os.popen("xdotool key l")
            elif signal_input == BACKWARD or signal_input == PREVIOUS:
                os.popen("xdotool key j")
        
        if not FOCUS_MODE:
            os.popen("xdotool windowminimize "+window_id)
    except:
        pass


def active_window_process_name_posix():
    p = psutil.Process(int(subprocess.check_output(["xdotool", "getactivewindow", "getwindowpid"]).decode("utf-8").strip()))
    return p.name()


if os.name == 'nt':
    def handle_signal_nt(signal_input, target_pid, target_name):
        try:
            if target_pid == -1:
                return
            if not FOCUS_MODE:
                app = pywinauto.application.Application()
                app.connect(path=target_name)
                if target_name == "chrome.exe":
                    window_handle = pywinauto.findwindows.find_windows(class_name_re=".*Chrome.*")[0]
                    app_dialog = app.window(handle=window_handle)
                elif target_name == "firefox.exe":
                    window_handle = pywinauto.findwindows.find_windows(class_name_re=".*Mozilla.*")[0]
                    app_dialog = app.window(handle=window_handle)
                else:
                    app_dialog = app.top_window_()
                app_dialog.Minimize()
                app_dialog.Restore()
                #time.sleep(SLEEP_DURATION)

            simulate_nt(signal_input, target_name)

            if not FOCUS_MODE:
                #time.sleep(SLEEP_DURATION)
                app_dialog.Minimize()
        except:
            pass

    def active_window_process_name_nt():
        pid = win32process.GetWindowThreadProcessId(
            win32gui.GetForegroundWindow())  # This produces a list of PIDs active window relates to
        return psutil.Process(pid[-1]).name()  # pid[-1] is the most likely to survive last longer

    def get_target_process_nt():
        target_name = ""
        target_pid = -1
        try:
            WMI = GetObject('winmgmts:')
            for app in apps:
                p = WMI.ExecQuery('select * from Win32_Process where Name="'+app+'"')
                if len(p) > 0:
                    target_name = app
                    target_pid = p[0].Properties_('ProcessId').Value
                    break
        except:
            pass
        print(target_name)
        return target_pid, target_name


while True:
    #get the target process
    if os.name == 'nt':
        target_pid, target_name = get_target_process_nt()
    else:
        target_pid, target_name = get_target_process()
    # print (target_pid, target_name)
    current = 'None'

    #get the input signal from client
    c, addr = s.accept()     # Establish connection with client.
    print(('Got connection from'+ str(addr)))
    signal_input = str(c.recv(1024).decode("UTF-8"))[2:]
    print(signal_input)
    # b = bytes('Thank you for connecting', 'utf-8')
    # c.send(b)
    c.close()                # Close the connection
    # check current foreground window
    if os.name == 'nt':
        current = active_window_process_name_nt()
    elif os.name == 'posix':
        current = active_window_process_name_posix()
    if current == target_name:
        FOCUS_MODE = True
    else:
        FOCUS_MODE = False
    # print(FOCUS_MODE)
    # handle the input
    if os.name == 'posix':
            handle_signal_posix(signal_input, target_pid, target_name)
    elif os.name == 'nt':
            handle_signal_nt(signal_input, target_pid, target_name)