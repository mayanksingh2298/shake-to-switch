#!/usr/bin/python3
import platform
import psutil
import subprocess as sp
import time
import pyautogui

PAUSE = 'Play/Pause'
PREVIOUS = 'Left'
NEXT = 'Right'
apps = ['evince.exe', 'vlc.exe']  # in order of priority


def get_target_process():
    try:
        all_running_processes = psutil.pids()
        target_pid=-1
        target_name=""
        for pid in all_running_processes:
            for app in apps:
                if psutil.Process(pid).name()==app:
                    target_pid = pid
                    target_name = app
                    break
            if target_pid!=-1:
                break
        return (target_pid,target_name)
    except:
        return (-1,"")


if platform.system() == 'Windows':
    import pywinauto

    def handle_signal_nt(signal_input, target_pid, target_name):
        try:
            if target_pid == -1:
                return
            app = pywinauto.application.Application()
            if target_name == 'evince.exe':
                pass
            elif target_name == 'vlc.exe':
                app.connect(path=target_name)
                app_dialog = app.top_window_()
                app_dialog.Restore()
                time.sleep(0.01)
                if signal_input == PAUSE:
                    pyautogui.press('space')
                elif signal_input == NEXT:
                    pyautogui.hotkey('shift', 'right')
                elif signal_input == PREVIOUS:
                    pyautogui.hotkey('shift', 'left')
                time.sleep(0.01)
                app_dialog.Minimize()
        except:
            pass

    while True:
        time.sleep(3)  # click on a window you like and wait 3 seconds
        target_pid, target_name = get_target_process()
        print(target_name)
        signal_input = PAUSE
        handle_signal_nt(signal_input, target_pid, target_name)
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


