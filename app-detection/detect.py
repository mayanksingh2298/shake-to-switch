#!/usr/bin/python3
import platform
import psutil
import os
import subprocess as sp
import time


if platform.system() == 'Windows':
    import win32process
    import win32gui

    def active_window_process_name():
        pid = win32process.GetWindowThreadProcessId(
            win32gui.GetForegroundWindow())  # This produces a list of PIDs active window relates to
        print(psutil.Process(pid[-1]).name())  # pid[-1] is the most likely to survive last longer

    print("Click on a window in next 3 seconds")
    time.sleep(3)  # click on a window you like and wait 3 seconds
    active_window_process_name()
elif platform.system() == 'Linux':
    import Xlib.display
    for i in range(1000000):
        # display = Xlib.display.Display()
        # window = display.get_input_focus().focus
        # wmname = window.get_wm_name()
        # wmclass = window.get_wm_class()
        # if wmclass is None and wmname is None:
        #     window = window.query_tree().parent
        #     wmname = window.get_wm_name()
        # print("WM Name: %s" % (wmname,))
        p = psutil.Process(int(sp.check_output(["xdotool", "getactivewindow", "getwindowpid"]).decode("utf-8").strip()))
        print (p.name())
elif platform.system() == 'Darwin':
    import Xlib.display

    display = Xlib.display.Display()
    window = display.get_input_focus().focus
    wmname = window.get_wm_name()
    wmclass = window.get_wm_class()
    if wmclass is None and wmname is None:
        window = window.query_tree().parent
        wmname = window.get_wm_name()
    print("WM Name: %s" % (wmname,))

