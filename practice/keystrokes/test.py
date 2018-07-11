import pyautogui
import platform
import psutil
import time


pyautogui.PAUSE = 1
pyautogui.FAILSAFE = True

supported_apps_type1 = ['AcroRd32.exe']
right = 'right'
left = 'left'
middle = ' '
current_process = 'none'


if platform.system() == 'Windows':
    import win32process
    import win32gui

    def active_window_process_name():
        pid = win32process.GetWindowThreadProcessId(
            win32gui.GetForegroundWindow())  # This produces a list of PIDs active window relates to
        return psutil.Process(pid[-1]).name()  # pid[-1] is the most likely to survive last longer

    print("Open a pdf in acrobat reader")
    while True:
        time.sleep(3)  # click on a window you like and wait 3 seconds
        current_process = active_window_process_name()
        if current_process in supported_apps_type1:
            pyautogui.press(right)
