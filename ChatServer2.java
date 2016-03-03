import java.io.*;
import java.net.*;

class ChatServer2
{
    public static void main(String args[]) throws Exception
    {
        // Variables
        DatagramSocket serverSocket = null;

        int port = 0, port1 = 0, port2 = 0;
        InetAddress IPAddress=null, IPAddress1=null, IPAddress2=null;

        DatagramPacket receivePacket = null;
        DatagramPacket sendPacket1 = null;
        DatagramPacket sendPacket2 = null;

        String message = "";
        String response = "";

        int state = 0;

        byte[] receiveData = new byte[1024];
        byte[] sendData  = new byte[1024];
        byte[] messageBytes = new byte[1024];

        // Trying to open the server socket
        try
        {
            serverSocket = new DatagramSocket(9876);
            System.out.println("Starting Server");
        }

        catch(Exception e)
        {
            System.out.println("Failed to open UDP socket");
            System.out.println("Closing Server");
            System.exit(0);
        }

        // Main Code
        while(state<3)
        {
            receiveData = new byte[1024];
            sendData  = new byte[1024];
            switch(state)
            {
                case 0: // Wait for the first connection
                    System.out.println(state);
                    receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    serverSocket.receive(receivePacket);
                    message = new String(receivePacket.getData());
                    message = message.trim();
                    if (message.toUpperCase().contains("HELLO RED") || message.toUpperCase().contains("HELLO BLUE"))
                    {
                        System.out.println(message);
                        response = "100";
                        sendData = response.getBytes();
                        IPAddress1 = receivePacket.getAddress();
                        port1 = receivePacket.getPort();
                        sendPacket1 = new DatagramPacket(sendData, sendData.length, IPAddress1, port1);
                        serverSocket.send(sendPacket1);
                        state = 1;
                    }
                    break;

                case 1: // Wait for second connection
                    System.out.println(state);
                    receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    serverSocket.receive(receivePacket);
                    message = new String(receivePacket.getData());
                    message = message.trim();
                    if (message.toUpperCase().contains("HELLO RED") || message.toUpperCase().contains("HELLO BLUE"))
                    {
                        System.out.println(message);
                        response = "200";
                        sendData = response.getBytes();
                        IPAddress2 = receivePacket.getAddress();
                        port2 = receivePacket.getPort();
                        sendPacket1 = new DatagramPacket(sendData, sendData.length, IPAddress1, port1);
                        serverSocket.send(sendPacket1);
                        sendPacket2 = new DatagramPacket(sendData, sendData.length, IPAddress2, port2);
                        serverSocket.send(sendPacket2);
                        state = 2;
                    }
                    break;

                case 2: // Send messages to and from the clients
                    System.out.println(state);
                    receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    serverSocket.receive(receivePacket);
                    message = new String(receivePacket.getData());
                    message = message.trim();
                    System.out.println(message);

                    // Check to see if either client sent "Goodbye"
                    if (message.toUpperCase().contains("GOODBYE"))
                    {
                        state = 3;
                        break;
                    }
                    IPAddress = receivePacket.getAddress();
                    port = receivePacket.getPort();

                    sendPacket1 = new DatagramPacket(sendData, sendData.length, IPAddress1, port1);
                    sendPacket2 = new DatagramPacket(sendData, sendData.length, IPAddress2, port2);

                    if ((port==port1)&&(IPAddress.equals(IPAddress1)))
                    {
                        sendData = receivePacket.getData();
                        sendPacket2 = new DatagramPacket(sendData, sendData.length, IPAddress2, port2);
                        serverSocket.send(sendPacket2);
                    }
                    else
                    {
                        sendData = receivePacket.getData();
                        sendPacket1 = new DatagramPacket(sendData, sendData.length, IPAddress1, port1);
                        serverSocket.send(sendPacket1);
                    }
            }
        }
        sendData = "Goodbye".getBytes();
        sendPacket1 = new DatagramPacket(sendData, sendData.length, IPAddress1, port1);
        serverSocket.send(sendPacket1);
        sendPacket2 = new DatagramPacket(sendData, sendData.length, IPAddress2, port2);
        serverSocket.send(sendPacket2);
        serverSocket.close();
    }
}