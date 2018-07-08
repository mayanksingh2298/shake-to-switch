#!/usr/bin/python3
import platform
import psutil
import subprocess as sp
import time


if platform.system() == 'Windows':
    import win32process
    import win32gui

    def active_window_process_name():
        pid = win32process.GetWindowThreadProcessId(
            win32gui.GetForegroundWindow())  # This produces a list of PIDs active window relates to
        print(psutil.Process(pid[-1]).name())  # pid[-1] is the most likely to survive last longer

    while True:
        time.sleep(3)  # click on a window you like and wait 3 seconds
        active_window_process_name()
elif platform.system() == 'Linux' or platform.system() == 'Darwin':
    while True:
        time.sleep(3)
        p = psutil.Process(int(sp.check_output(["xdotool", "getactivewindow", "getwindowpid"]).decode("utf-8").strip()))
        print(p.name())
        '''
    try this method which should automatically detect focus change and also print window name not process
    import Xlib
    import Xlib.display

    disp = Xlib.display.Display()
    root = disp.screen().root

    NET_WM_NAME = disp.intern_atom('_NET_WM_NAME')
    NET_ACTIVE_WINDOW = disp.intern_atom('_NET_ACTIVE_WINDOW')

    root.change_attributes(event_mask=Xlib.X.FocusChangeMask)
    while True:
        time.sleep(3)
        try:
            window_id = root.get_full_property(NET_ACTIVE_WINDOW, Xlib.X.AnyPropertyType).value[0]
            window = disp.create_resource_object('window', window_id)
            window.change_attributes(event_mask=Xlib.X.PropertyChangeMask)
            window_name = window.get_full_property(NET_WM_NAME, 0).value
        except Xlib.error.XError: #simplify dealing with BadWindow
            window_name = None
        print(window_name)
        event = disp.next_event()
    '''


