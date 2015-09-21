import java.io.*;
import java.net.*;
import java.util.*;

/**
 * COMP3331 Computer Networks and Applications 
 * Assignment 1 S1 2015
 * z3352078
 * @author Nelson Wee
 */
public class cdht implements Runnable{
	static int[] port = new int[256];
	static int peerSelf = 0;	//peer number of this		N
	static int peerSucc = 0;	//peer number of first successor N+1
	static int peerSucc2 = 0;	//peer number of second successor N+2
	
	static int prevPeer = 0;	//peer number of first successor N-1
	static int prevPeer2 = 0;	//peer number of second successor N-2

	int destPeer = 0;
	String service; //handles options initializing i.e. UDP/TCP server/client threads
	String type;	//handles options for type i.e. request, return
	String message; //passes the messages to be sent
	
	static Thread UDPclient; //ping clients
	static Thread UDPclient2;
	
	public cdht(String service, int destPeer, String type, String message) {
		this.service = service;
		this.destPeer = destPeer;
		this.type = type;
		this.message = message;
	}
	
	public void run() {
		if (service.equals("UDPserver")) UDPServer();
		else if (service.equals("UDPclient")) UDPClient();
		else if (service.equals("TCPclient")) TCPClient();
		else if (service.equals("TCPserver")) TCPServer();
	}
	
	public static void main(String[] args) {
		//stores port numbers based on peer #
		for (int i = 0; i < 256; i++)
			port[i] = 50000+i;

		if (args.length != 3) {
			System.out.println("usage: java cdht P1 P2 P3");
			return;
		}
		peerSelf = Integer.parseInt(args[0]);		
		peerSucc = Integer.parseInt(args[1]);
		peerSucc2 = Integer.parseInt(args[2]);

		//initialize multiple server/client threads 
		Thread UDPserver = new Thread(new cdht("UDPserver", 0, "", ""));
		UDPclient = new Thread(new cdht("UDPclient",peerSucc, "", "client1"));
		UDPclient2 = new Thread(new cdht("UDPclient",peerSucc2, "", "client2"));
		Thread TCPserver = new Thread(new cdht("TCPserver", 0, "", ""));
		
		UDPserver.start();	
		UDPclient.start();
		UDPclient2.start();
		TCPserver.start();
		
		//Scanner used to parse user input to the terminal 
		Scanner s = new Scanner(System.in);
		while (s.hasNext()) {
			String command = s.next();
			//Handles user file requests
			if (command.equals("request")) {
				int filename = s.nextInt();
				String requestMessage = "request" + " " + peerSelf + " " + filename+" "+peerSelf+"\n";
				Thread TCPclient = new Thread(new cdht("TCPclient", peerSucc, "request", requestMessage));
				TCPclient.start();
			}
			//Handles user command "quit" which updates successors of previous peers 
			else if (command.equals("quit")) {
				String quitMessage = "quit " + peerSelf +" "+ peerSucc + " " + peerSucc2 + "\n";
				Thread TCPclient = new Thread(new cdht("TCPclient", prevPeer, "quit", quitMessage));
				TCPclient.start();
				Thread TCPclient2 = new Thread(new cdht("TCPclient", prevPeer2, "quit", quitMessage));
				TCPclient2.start();
				
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				UDPserver.stop();
				UDPclient.stop();
				UDPclient2.stop();
				TCPserver.stop();
				s.close();
				break;
			}
			//helper command to show the successor and predecessor ports
			else if (command.equals("stats")) {
				System.out.println("This is peer "+peerSelf+
						"\nsucc1: "+peerSucc+" succ2 "+peerSucc2 +
						"\npre1: "+prevPeer+" pre2: "+prevPeer2);
			}
		}
	}
	
	public void TCPClient(){
		try {
			/* Sends file requests by calling on successor
			 * Format: "[command] [peer requesting] [filename] \n"
			 */
			if (type.equals("request")) {
				Scanner s = new Scanner(message);
				s.next(); //skips over command
				s.nextInt(); //skips over req peer
				int filename = s.nextInt(); 
				System.out.println("    File "+filenameOutput(filename)+" is not stored here.");
				System.out.println("File request message has been forwarded to my successor.");
				s.close();
			}

			//send message to designated peer
			Socket clientSocket = new Socket(InetAddress.getByName("localhost"), port[destPeer]);
			DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
			outToServer.writeBytes(message);
			
			//receives confirmation message from peer
			BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			String FromServer = inFromServer.readLine();
			//System.out.println(FromServer);
			
			clientSocket.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * The server that listens to the TCP commands from the user and responds accordingly.
	 */
	public void TCPServer() {
		try {
			ServerSocket welcomeSocket = new ServerSocket(port[peerSelf]);
			while(true) {
				Socket connectionSocket = welcomeSocket.accept();
				//Reads input from requesting TCP connection								
			    BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			    String clientSentence = inFromClient.readLine();
			    //Responds to requesting connection
			    DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
			    outToClient.writeBytes("TCP connection with Peer "+peerSelf+" Established\n");
			    
				/*
				 *  //Helper response to check connection
				 * System.out.println("TCP connection from " + connectionSocket);
			     * System.out.println("Received: "+clientSentence);
			     */
			    
			    //scans the given message and parses it to obtain the variables
			    Scanner parser = new Scanner(clientSentence);
			    String command = parser.next();
			    
			    if (command.equals("request")) {
			    	int requestID = parser.nextInt();
			    	int filename = parser.nextInt();
			    	int peerPrev = parser.nextInt();
			    			    
				    //If file isnt here, forward request to next one
					if (!isFileHere(filename, peerPrev)) {
						String requestForward = "request" + " " + requestID + " " + filename+" "+peerSelf+"\n";
						//calls next successor
						Thread TCPclient = new Thread(new cdht("TCPclient", peerSucc, "request", requestForward));
						TCPclient.start();
					}
					//if file is found, send response back to the requestee
					else {
						System.out.println("    File "+filenameOutput(filename)+" is here.");
						System.out.println("A response message, destined for peer "+requestID+" has been sent.");
						
						String requestResponse = "response" +" " + peerSelf + " "+ filename + "\n";
						Thread TCPclient = new Thread(new cdht("TCPclient", requestID, "return", requestResponse));
						TCPclient.start();
					}
			    }
			    //Receives the response from the peer who has the file
			    else if (command.equals("response")) {
			    	int originPeer = parser.nextInt();
			    	int filename = parser.nextInt();    	
			    			
			    	System.out.println("Received a response message from peer "+ originPeer + ", which has the file "+ filenameOutput(filename)+".");
			    }
			    //Updates successors when one peer leaves
			    else if (command.equals("quit")) {
			    	int quitPeer = parser.nextInt();
			    	int newSucc = parser.nextInt();
			    	int newSucc2 = parser.nextInt();
			    	
			    	if (quitPeer == peerSucc) { 
			    		peerSucc = newSucc;
			    		peerSucc2 = newSucc2;
			    	}
			    	else if (quitPeer == peerSucc2) peerSucc2 = newSucc;
			    	
			    	System.out.println("    Peer "+quitPeer+" will depart from the network.");
			    	System.out.println("My first successor is now peer "+peerSucc+".");
			    	System.out.println("My second successor is now peer "+peerSucc2+".");
			    	//halts previous thread ping clients
			    	UDPclient.stop();
			    	UDPclient2.stop();
			    	//restarts the ping clients with updates successors
			    	UDPclient = new Thread(new cdht("UDPclient",peerSucc, "", "client1"));
					UDPclient2 = new Thread(new cdht("UDPclient",peerSucc2, "", "client2"));
					UDPclient.start();
					UDPclient2.start();
			    }
			    
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * The client that sends ping requests to its successors over UDP.
	 */
	public void UDPClient() {
		try {
			Thread.sleep(5000); //delay for initial setup
			while (true) {
				DatagramSocket clientSocket = new DatagramSocket();		
				InetAddress IPAddress = InetAddress.getByName("localhost");
				byte[] sendData = new byte[1024];				
				String sData = "pingMsg "+message+" "+peerSelf+"\n";
				sendData = sData.getBytes();
				
				DatagramPacket sendPkt = new DatagramPacket(sendData, sendData.length, IPAddress, port[destPeer]);	
				clientSocket.send(sendPkt);
				
				DatagramPacket rcvPkt = new DatagramPacket(new byte[1024], 1024);
				clientSocket.receive(rcvPkt);
				
				String peerNum = new String(rcvPkt.getData());
				System.out.println("A ping response message was received from Peer "+peerNum+".");
				
				Thread.sleep(60000); //ping every 60s
				
				//clientSocket.close();
			}
		} catch (IOException|InterruptedException e) {
			e.printStackTrace();
		}
	}
		
	/**
	 * The server to listen and respond to ping requests over UDP.
	 * Stores the peer's 2 predecessors from the requests.
	 */
	public void UDPServer() {
		try {
			DatagramSocket serverSocket = new DatagramSocket(port[peerSelf]);
			
			while (true) {
				DatagramPacket rcvPkt = new DatagramPacket(new byte[1024], 1024);
				serverSocket.receive(rcvPkt);
				
				int peerNum = 0;
				Scanner s = new Scanner(new String(rcvPkt.getData()));
				s.next(); //pingMsg
				String pre = s.next(); //from predecessor 1 or 2
				peerNum = s.nextInt();				
				System.out.println("A ping request message was received from Peer " + peerNum + ".");
				
				if (pre.equals("client1")) 
					prevPeer = peerNum; //peer number
				if (pre.equals("client2"))
					prevPeer2 = peerNum;
				s.close();
				
							
				byte[] sendData = new byte[1024];
				sendData = (peerSelf+"").getBytes();
				DatagramPacket sendPkt = new DatagramPacket(sendData, sendData.length, rcvPkt.getAddress(), rcvPkt.getPort());
				serverSocket.send(sendPkt);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//HELPER METHODS
	/**
	 * Method to check if the file would belong to this peer. 
	 * Done by comparing the diff of peer and prev peer with the file hash.   
	 * @param filename
	 * @param prevPeer
	 * @return
	 */
	boolean isFileHere(int filename, int prevPeer) {
        int hash = filename % 256;
        
        /* multiplies the difference between the hash and the peer and previous peer.
         * if the result is 0 (peer 4 hash 4), 
         * or result < 0 and peer is larger than previous (peer 4 peer 5, hash 5 = -1),
         * or result > 0 and peer is smaller than previous (peer 15 peer 1, hash 16 = 15) then returns true.
         */
        int multDiff = (hash - peerSelf) * (hash - prevPeer);
        if((multDiff == 0) || 
        		(peerSelf > prevPeer && multDiff < 0 ) ||
        		(peerSelf < prevPeer && multDiff > 0 ))
        	return true;
        
        return false;
	}
	/**
	 * Helper method to correctly display the filenames from integer values 
	 * @param filename
	 * @return
	 */
	String filenameOutput(int filename) {
    	String name = filename+"";
    	if (filename < 10) name = "000"+filename;
    	else if (filename < 100)name = "00" + filename;
    	else if (filename < 1000)name = "0" + filename;
    	return name;
	}
}