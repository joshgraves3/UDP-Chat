/*
*	UDP Client Program
*	Sends/receives messages from other clients through a server
*	Receives a line of input from a UDP server
* 	Sends a new message to the other client
* 	Clients message back and forth until they exit the program
*	
*	Team: CIM (Chapman Instant Messager)
*	@author: Josh Graves
* 	Partners: Julien Fournell and Haley Kinoshita
	version: 0.3
*/

import java.io.*;
import java.net.*;

class BlueClient {

	public static void main(String args[]) throws Exception{

		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

		DatagramSocket clientSocket = new DatagramSocket();
		InetAddress IPAddress = InetAddress.getByName("10.109.153.186");
		//10.109.177.160
		byte[] sendData = new byte[1024];
		byte[] receiveData = new byte[1024];
		
		int state = 0;
		String message = "HELLO BLUE";
		String response;
   		String chatString = "";
		
		DatagramPacket sendPacket = null;
		DatagramPacket receivePacket = null;
    	
    	boolean talk = false;
    	String clientName = "BLUE: ";

		while (state < 3){
			sendData = new byte[1024];
			receiveData = new byte[1024];
			switch (state){
				case 0:

					// send initial message to server and wait for response
					sendData = message.getBytes();
					sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress,9876);
					clientSocket.send(sendPacket);

          			System.out.println("State 0 ... connecting to the server.");

					receivePacket = new DatagramPacket(receiveData, receiveData.length);
					clientSocket.receive(receivePacket);
					response = new String(receivePacket.getData());
          			response = response.trim();

          			System.out.println(response); //either 100 or 200

					if (response.substring(0,3).equals("100")) {
						state = 1; //You are first client. wait for second client to connect
            			System.out.println("State 1 ... your are the first to connect to the server.");
            			talk = true;
					}

					else if (response.substring(0,3).equals("200")) {
						state = 2; //you are second client. Wait for message from first client
            			System.out.println("State 2 ... your are the second to connect to the server.");
					}

					break;

				case 1:

					// Waiting for notification that the second client is ready
					receivePacket = new DatagramPacket(receiveData, receiveData.length);
					clientSocket.receive(receivePacket);
					response = new String(receivePacket.getData());
          			response = response.trim();

          			System.out.println(response); // prints either 100 or 200

					if (response.substring(0,3).equals("200")) {
						//get message from user and send it to serve
           				state = 2;
					}

					//state = 2; //transition to state 2: chat mode
					break;

				case 2:
					//Chat mode
					if (talk) {
						System.out.print(clientName);
						response = clientName + inFromUser.readLine();
						sendData = response.getBytes();
						sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);
						clientSocket.send(sendPacket);
						talk = false;
					}

					else {
						//receive message from other client
						receivePacket = new DatagramPacket(receiveData, receiveData.length);
						clientSocket.receive(receivePacket);
						response = new String(receivePacket.getData());
						response = response.trim();

						if (response.length()>= 7 && response.toLowerCase().contains("goodbye")) {
							state = 3; //prepare to exit the while loop
							System.out.println("goodbye");
					  		System.out.println("*** chat has been ended ***");
							break;
						}

						else {
					 		System.out.println(response);
					  		talk = true;
						}
          			}
          			break;
			}
		}
		//close the socket
		clientSocket.close();
	}
}