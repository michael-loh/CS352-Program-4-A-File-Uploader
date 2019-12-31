import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class Server {
	
	private static ServerSocket servSock;
	private static Socket sock;
	private final static int portnum = 5520;
	private static DataInputStream in = null;

	public static void main(String[] args) {
		//create the socket
		try {
			servSock = new ServerSocket( portnum );
		} catch (IOException e) {
			System.out.println(e.toString());
		}
		
		
		try {
			run();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(e.toString());
		}

	}
	
	private static void run() throws Exception {
		
		System.out.println("Server Running");
		sock = null;
		
		while(true) {
			System.out.println("Waiting for Connections.......");
			sock = servSock.accept();
			 
			Date date = new Date();
			System.out.println("Got a Connection: " + date.getTime());
			System.out.println("Connected to: " + sock.getInetAddress() + " Port: " + sock.getPort());
			
			//receive file
			in = new DataInputStream(new BufferedInputStream(sock.getInputStream()));
			
			String filename = getNullTerminatedString();
			System.out.println("Got a file: " + filename);
			
			long sizeOfFile = getSizeOfFile();
			System.out.println("File size: " + sizeOfFile);
			
			getFile(filename);
			System.out.println("Got the file");
			
		}
		
	}
	
	/**
	* This method reads the bytes (terminated by ‘\0’) sent from the Client, one
	* byte at a time and turns the bytes into a String.
	* Set up a loop to repeatedly read bytes until a ‘\0’ is reached.
	 * @throws IOException 
	*/
	private static String getNullTerminatedString() throws IOException{
		byte[] buffer = new byte[1];
		
		String temp = "";
		String fileName = "";
		
		while(!temp.contentEquals("\0")) {
			fileName += temp;
			in.read(buffer);
			temp = new String(buffer);
			
		}
		return fileName;
		
	}
	
	private static long getSizeOfFile() throws IOException {
		
		byte[] buffer = new byte[1];
		
		String temp = "";
		String strSize = "";
		
		while(!temp.contentEquals("\0")) {
			strSize += temp;
			in.read(buffer);
			temp = new String(buffer);
			
		}
		
		long size = Long.parseLong(strSize);
		return size;
	}

	/**
	* This method takes an output file name and its file size, reads the binary
	* data (in a 1024-byte chunk) sent from the Client, and writes to the output
	* file a chunk at a time.
	* Use the FileOutputStream class to write bytes to a binary file
	* Set up a loop to repeatedly read and write chunks.
	 * @throws Exception 
	*/
	private static void getFile(String filename) throws Exception{
		
		
		FileOutputStream fos = new FileOutputStream(filename);
		byte[] buffer = new byte[1024];
		int count = 0;
		while( (count = in.read(buffer, 0, 1024)) != -1) {
			fos.write(buffer, 0, count);
		}
		
	}
	
}






