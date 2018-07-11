#!/usr/bin/python           # This is server.py file

import os
import socket               # Import socket module

s = socket.socket()         # Create a socket object
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
while True:
   c, addr = s.accept()     # Establish connection with client.
   print(('Got connection from'+ str(addr)))
   print((str(c.recv(1024).decode("UTF-8"))[2:]))
   b = bytes('Thank you for connecting', 'utf-8')
   c.send(b)
   c.close()                # Close the connection
