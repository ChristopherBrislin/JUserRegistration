# JUserRegistration
This is a java based middleware for registering MySQL users on a database without giving them direct access to the Admin privileges.

This is my first completed software project - so please take it easy on me! The code is absolute spaghetti. I'm sure I am also tackling a problem that has much better, more elegant solutions, but here we are. 

So this software was originally written to be loaded onto the MySQL host server to facilitate registering new MySQL users without giving them access to the admin credentials. This is to facilitate a future project I have in mind that takes the form of an electronic logbook. 

The basic funtion is that a user creates a SSL connection with the server with the request to either register, remove, change username/email, change password, or reset password. This information is then run through a prepared statement and used to query the MySQL server. The result is then returned to the user and the connection terminated. At the moment the code is in it's absolute base functionality and could be cleaned up significantly. Please feel free to do so - and share the changes.

A few things to make it function correctly: it will require a config.properties text file with the attributes listed in Main, and also a server and client certificate to facilitate the SSL connection. 

Goodluck!


