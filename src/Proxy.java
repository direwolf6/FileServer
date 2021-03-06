import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.JOptionPane;

public class Proxy {

	public final static int SOCKET_PORT = 3030;
	public final static int SOCKET_PORT_TO_DB = 3000;
	public final static String FOLDER_TO_STORE = "C:/Users/Cade/Cache/";
	public final static String SERVER = "127.0.0.1";
	public final static int FILE_SIZE = 902238600;
	public final static int NAME_SIZE = 2048;

	public static void main (String [] args ) throws IOException {
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		OutputStream os = null;
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		ServerSocket servsock = null;
		Socket sock = null;
		Socket socktoDB = null;
		try {
			servsock = new ServerSocket(SOCKET_PORT);
			new File(FOLDER_TO_STORE).mkdirs();
			while (true) {
				System.out.println("Waiting...");
				try {
					sock = servsock.accept();
					socktoDB = new Socket(SERVER, SOCKET_PORT_TO_DB); 
					System.out.println("Accepted connection : " + sock);



					byte [] mybytearray2  = new byte [1];
					InputStream is = sock.getInputStream();
					int bytesRead = is.read(mybytearray2,0,mybytearray2.length);

					if(mybytearray2[0] == 6){
						System.out.println("here we are");
					}

					else if(mybytearray2[0] == 8){// sending Name of file onto DB to get back the server socket

						byte[] mybytearrayName  = new byte [NAME_SIZE];//get the filename from Client
						InputStream is2 = sock.getInputStream();
						is2.read(mybytearrayName,0,mybytearrayName.length);	
						String name = new String(mybytearrayName);
						name = name.trim();
						System.out.println("Proxy" + name);
						

						os = socktoDB.getOutputStream();// send 7 byte to directory to see if file already exists
						byte[] ba = new byte[1];
						ba[0] = 7;
						os.write(ba ,0,1);
						os.flush();

						byte [] mybytearrayNameToDB  = new byte [name.length()];//send filename to directory
						mybytearrayNameToDB = name.getBytes();
						os = socktoDB.getOutputStream();
						System.out.println("Sending " + name + "(" + mybytearrayNameToDB.length + " bytes)");
						os.write(mybytearrayNameToDB,0,mybytearrayNameToDB.length);
						os.flush();
						System.out.println("Done.");

						byte[] mybytearraySocket  = new byte [NAME_SIZE];//take in the port number of the server
						InputStream isSocket = socktoDB.getInputStream();
						isSocket.read(mybytearraySocket,0,mybytearraySocket.length);
						String socketNum = new String(mybytearraySocket);
						socketNum = socketNum.trim();
						System.out.println(socketNum);
						
						socktoDB.close();

						if(socketNum.equals("NOPE")){//file does not exist already in directory and we will assign it one

							socktoDB = new Socket(SERVER, SOCKET_PORT_TO_DB); 

							os = socktoDB.getOutputStream();// send the byte (8) to directory
							os.write(mybytearray2 ,0,1);
							os.flush();

							os = socktoDB.getOutputStream();
							System.out.println("Sending " + name + "(" + mybytearrayNameToDB.length + " bytes)");
							os.write(mybytearrayNameToDB,0,mybytearrayNameToDB.length);
							os.flush();
							System.out.println("Done.");


							String serverPort = "";
							int randomNum = ThreadLocalRandom.current().nextInt(1, 3+1);

							if(randomNum == 1) serverPort = "3031";
							else if(randomNum == 2) serverPort = "3032";
							else serverPort = "3033";

							byte [] mybytearray  = new byte [serverPort.length()]; //send server socket to client
							mybytearray = serverPort.getBytes();
							os = sock.getOutputStream();
							System.out.println("Sending " + serverPort + "(" + mybytearray.length + " bytes)");
							os.write(mybytearray,0,mybytearray.length);
							os.flush();	

							os = socktoDB.getOutputStream(); // send server socket to directory server
							System.out.println("Sending " + serverPort + "(" + mybytearray.length + " bytes)");
							os.write(mybytearray,0,mybytearray.length);
							os.flush();

						}


						else{
							byte [] mybytearray  = new byte [socketNum.length()]; //send server socket to client
							mybytearray = socketNum.getBytes();
							os = sock.getOutputStream();
							System.out.println("Sending " + socketNum + "(" + mybytearray.length + " bytes)");
							os.write(mybytearray,0,mybytearray.length);
							os.flush();	
						}
					}



					else{// client wants to download from server

						os = socktoDB.getOutputStream();// send the byte (7) to directory
						os.write(mybytearray2 ,0,1);
						os.flush();

						byte[] mybytearrayName  = new byte [NAME_SIZE];//receive filename from client
						InputStream is2 = sock.getInputStream();
						is2.read(mybytearrayName,0,mybytearrayName.length);
						String name = new String(mybytearrayName);
						name = name.trim();
						System.out.println(name);

						byte [] mybytearrayNameToDB  = new byte [name.length()];//send to filename to directory
						mybytearrayNameToDB = name.getBytes();
						os = socktoDB.getOutputStream();
						System.out.println("Sending " + name + "(" + mybytearrayNameToDB.length + " bytes)");
						os.write(mybytearrayNameToDB,0,mybytearrayNameToDB.length);
						os.flush();
						System.out.println("Done.");

						byte[] mybytearraySocket  = new byte [NAME_SIZE];//take in the port number of the server
						InputStream isSocket = socktoDB.getInputStream();
						isSocket.read(mybytearraySocket,0,mybytearraySocket.length);
						String socketNum = new String(mybytearraySocket);
						socketNum = socketNum.trim();
						//int serverSocketNumber = Integer.parseInt(socketNum);
						System.out.println(socketNum);

						byte [] mybytearray  = new byte [socketNum.length()]; //send server socket to client
						mybytearray = socketNum.getBytes();
						os = sock.getOutputStream();
						System.out.println("Sending " + socketNum + "(" + mybytearray.length + " bytes)");
						os.write(mybytearray,0,mybytearray.length);
						os.flush();	

					}
				}
				finally {
					if (bis != null) bis.close();
					if (os != null) os.close();
					if (sock!=null) sock.close();
				}
			}
		}
		finally {
			if (servsock != null) servsock.close();
		}
	}
	public static String convertStreamToString(java.io.InputStream is) {
		java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
		return s.hasNext() ? s.next() : "";
	}

}