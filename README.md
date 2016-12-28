# ChatServerSE

# Features
* Threaded
* Individual channels that can be created by the users
* Easy to use ChatPacket for validating input, encoding and decoding messages

# Usage
Server recognizes the following commands:
* /connect  //no parameter
* /join "name"
* /nick "new nick"
* /part     //no parameter
* /partall  //no parameter
* /quit     //no parameter

# To-Do
- [ x ] Standardized format for ChatPacket(eg.JSON)
- [ ] Better feedback for client requests
- [ ] More server side checks
- [ ] Allow private messages from user to user
- [ ] Use hashmap/hashset instead of arraylist

# Client requirements
* Client must abide by ChatPacket specification (Example can be found from ChatPacket.java)
*   -Room name starts with an #
*   -Type is either "message" or "response". Client messages are always of type "message"
*   -If the message is a command it must start with an / otherwise it is handled as an message
* Server will responds to most client request with information. These will be designated by type "response".(Example nick change will have the new nickname in the message part of the packet and type will be "response")

# Screenshot
<img src="https://gyazo.com/97d236ef247a510db739a8ca33bb3d67.png">

# Dependencies
* google-gson https://github.com/google/gson
