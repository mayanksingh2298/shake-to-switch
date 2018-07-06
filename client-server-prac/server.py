#!/usr/bin/python           # This is server.py file

import os
import socket               # Import socket module

s = socket.socket()         # Create a socket object
# host = socket.gethostname() # Get local machine name
try:
	host=os.popen("hostname -I").read().split()[0]
	print("host ip is: "+host)
except:
	print ("Please connect to a network")
	exit()
port = 12346               # Reserve a port for your service.
s.bind((host, port))        # Bind to the port

s.listen(5)                 # Now wait for client connection.
while True:
   c, addr = s.accept()     # Establish connection with client.
   print ('Got connection from'+ str(addr))
   print (str(c.recv(1024).decode("UTF-8"))[2:])
   c.send('Thank you for connecting')
   c.close()                # Close the connection
