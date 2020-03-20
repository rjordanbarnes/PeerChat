# PeerChat
Peer-to-peer chat application that uses a Rendezvous server for peer discovery.

Project created for UW CSE 461 in Winter 2020.

## Launching
To run the Rendezvous server
```
$ ./rendezvous.sh PORT
```
To launch the GUI chat application
```
$ ./chatGUI.sh
```
To launch the command line chat application
```
$ ./chat.sh RENDEZVOUSADDRESS RENDEZVOUSPORT PEERNAME OPTIONALPEERPORT
```

## How It Works
The Rendezvous server is a directory that maps Peer Names (think username) to an IP/Port.
When peers connect to the Rendezvous server, the server registers the peer's name with their
IP address (as seen by the server) and the port the peer is listening on for other peer connections.
The list of peers that the Rendezvous server knows about can be obtained, allowing them
to initiate a TCP connection with the peer they want to chat with. The actual chatting occurs
over this peer-to-peer connection, the server is only required for peer discovery.

## Video Showcase
https://www.youtube.com/watch?v=OAQoWMd9Wyw
