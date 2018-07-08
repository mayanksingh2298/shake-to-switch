#!/usr/bin/python           # This is server.py file

import os
import socket               # Import socket module
import psutil

s = socket.socket()         # Create a socket object

PAUSE = 'Play/Pause'
PREVIOUS = 'Left'
NEXT = 'Right'

# host = socket.gethostname() # Get local machine name
try:
	host = ((([ip for ip in socket.gethostbyname_ex(socket.gethostname())[2] if not ip.startswith("127.")] or [
		[(s.connect(("8.8.8.8", 53)), s.getsockname()[0], s.close()) for s in
		 [socket.socket(socket.AF_INET, socket.SOCK_DGRAM)]][0][1]]) + ["no IP found"])[0])
	# host=os.popen("hostname -I").read().split()[0]
	# host = "192.168.0.107"
	print(("host ip is: "+host))
except:
	print ("Please connect to a network")
	exit()
port = 12346               # Reserve a port for your service.
s.bind((host, port))        # Bind to the port

s.listen(5)                 # Now wait for client connection.

def get_target_process():
	try:
		apps = ['evince','vlc'] # in order of priority
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


def handle_signal(signal_input,target_pid,target_name):
	try:
		if target_pid==-1:
			return
		window_id = os.popen("xdotool search --name "+ target_name).read().split()[-1]
		if target_name=='evince':
			pass
		elif target_name=='vlc':
			if signal_input==PAUSE:
				os.popen("xdotool windowactivate "+window_id+" && sleep 0.01 && xdotool key space && xdotool windowminimize "+window_id)
			elif signal_input==NEXT:
				os.popen("xdotool windowactivate "+window_id+" && sleep 0.01 && xdotool key shift+Right && xdotool windowminimize "+window_id)
			elif signal_input==PREVIOUS:
				os.popen("xdotool windowactivate "+window_id+" && sleep 0.01 && xdotool key shift+Left && xdotool windowminimize "+window_id)

	except:
		pass

while True:
	
	#get the target process
	target_pid, target_name = get_target_process()
	# print (target_pid)

	#get the input signal from client
	c, addr = s.accept()     # Establish connection with client.
	print(('Got connection from'+ str(addr)))
	signal_input = str(c.recv(1024).decode("UTF-8"))[2:]
	print(signal_input)
	# b = bytes('Thank you for connecting', 'utf-8')
	# c.send(b)
	c.close()                # Close the connection

	#handle the input
	handle_signal(signal_input,target_pid,target_name)

