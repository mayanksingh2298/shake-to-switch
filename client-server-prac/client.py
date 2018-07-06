#!/usr/bin/python           # This is client.py file

import socket               # Import socket module

s = socket.socket()         # Create a socket object
host = socket.gethostname() # Get local machine name
port = 12346            # Reserve a port for your service.

s.connect((host, port))
s.send("Hello from server")
print (str(s.recv(1024)))
s.close                     # Close the socket when done