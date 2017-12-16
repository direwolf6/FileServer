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

import javax.swing.JOptionPane;

public class Server {

	public final static int SOCKET_PORT = 3031;  // you may change this
	public final static String FILE_TO_SEND = "c:/temp/source.jpg";  // you may change this
	public final static String FOLDER_TO_STORE = "C:/server/";
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
		try {
			servsock = new ServerSocket(SOCKET_PORT);
			while (true) {
				System.out.println("Waiting...");
				try {
					sock = servsock.accept();
					System.out.println("Accepted connection : " + sock);



					byte [] mybytearray2  = new byte [1];
					InputStream is = sock.getInputStream();
					int bytesRead = is.read(mybytearray2,0,mybytearray2.length);
					
					if(mybytearray2[0] == 6){
						System.out.println("here we are");
					}

					else if(mybytearray2[0] == 8){
						byte[] mybytearrayName  = new byte [NAME_SIZE];
						InputStream is2 = sock.getInputStream();
						is2.read(mybytearrayName,0,mybytearrayName.length);
						
						String name = new String(mybytearrayName);
						//name = name.trim();
						System.out.println(name);

						byte [] mybytearray  = new byte [FILE_SIZE];
						InputStream is4 = sock.getInputStream();
						fos = new FileOutputStream(FOLDER_TO_STORE + name);
						bos = new BufferedOutputStream(fos);
						bytesRead = is4.read(mybytearray,0,mybytearray.length);
						int current = bytesRead;

						do {
							bytesRead =
									is.read(mybytearray, current, (mybytearray.length-current));
							if(bytesRead >= 0) current += bytesRead;
							//System.out.println(bytesRead);
						} while(bytesRead != -1);

						System.out.println(mybytearray.length + "   " + current);
						bos.write(mybytearray, 0 , current);
						bos.flush();
						//mybytearray2[0] = 6;
					}



					else{// send file
						
						byte[] mybytearrayName  = new byte [NAME_SIZE];
						InputStream is2 = sock.getInputStream();
						is2.read(mybytearrayName,0,mybytearrayName.length);
						
						String name = new String(mybytearrayName);
						name = name.trim();
						System.out.println(name);
						
							
						File myFile = new File (FOLDER_TO_STORE + name);
						byte [] mybytearray  = new byte [(int)myFile.length()];
						fis = new FileInputStream(myFile);
						bis = new BufferedInputStream(fis);
						bis.read(mybytearray,0,mybytearray.length);
						os = sock.getOutputStream();
						System.out.println("Sending " + FILE_TO_SEND + "(" + mybytearray.length + " bytes)");
						os.write(mybytearray,0,mybytearray.length);
						os.flush();
						System.out.println("Done.");
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
	static String convertStreamToString(java.io.InputStream is) {
		java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
		return s.hasNext() ? s.next() : "";
	}
}