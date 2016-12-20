# ChatServerSE

# Features
* Threaded
* Individual channels that can be created by the users
* Easy to use ChatPacket for sending and receiving messages

# Client requirements
* Client must abide by ChatPacket specification (Example can be found from ChatPacket.java)
*   -Sender cannot have # in it's name
*   -Room name starts with an #
*   -Type is always integer, all messages from the client must be of type 2
*   -If the message is a command it must start with an / otherwise it is handled as an message
* Server will responds to most client request with information. These will be designated by type 1.(Example nick change will have the new nickname in the message part of the packet and type will be 1)

# Screenshot
Client connects to the server and says "hello world"
<img src="https://gyazo.com/97d236ef247a510db739a8ca33bb3d67.png">
