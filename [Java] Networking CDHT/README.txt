COMP3331: Computer Networks and Applications

This program implements a part of a peer-to-peer (P2P) protocol circular DHT. Each instance of the program represents a peer in the network.

Utilising both TCP and UDP the program sends messages to every successor and predecessor to both check if the peer is alive, or handle joining/leaving peers. The program also checks each peer to identify which peer should hold a file of specific size.