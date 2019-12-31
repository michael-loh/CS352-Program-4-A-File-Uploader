import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JFileChooser;
import javax.swing.JTextArea;
import javax.swing.JButton;

public class Client extends JFrame implements ActionListener {
	private JTextField hostIP;
	private JTextField port;
	private JFileChooser fileChooser;
	private JTextArea textArea;
	
	private Socket sock = null;
	private OutputStream out = null;
	private DataInputStream in = null;
	private final String nullTerminator = "" + '\0';
	
	public static void main(String[]args) {
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Client frame = new Client();
					frame.setVisible(true);
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
	}
	
	public Client() {
		getContentPane().setFont(new Font("Tahoma", Font.PLAIN, 15));
		getContentPane().setLayout(null);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 620, 630);
		
		JLabel lblNewLabel = new JLabel("Host IP");
		lblNewLabel.setBounds(29, 284, 68, 25);
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
		getContentPane().add(lblNewLabel);
		
		hostIP = new JTextField("localhost");
		hostIP.setBounds(84, 284, 189, 27);
		getContentPane().add(hostIP);
		hostIP.setColumns(10);
		
		
		JLabel lblNewLabel_1 = new JLabel("Port");
		lblNewLabel_1.setBounds(285, 290, 46, 13);
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 15));
		getContentPane().add(lblNewLabel_1);
		
		port = new JTextField("5520");
		port.setBounds(319, 284, 75, 27);
		port.setFont(new Font("Tahoma", Font.PLAIN, 15));
		getContentPane().add(port);
		port.setColumns(10);
		
		fileChooser = new JFileChooser();
		fileChooser.setBounds(28, 10, 551, 236);
		FileNameExtensionFilter filter = new FileNameExtensionFilter("docx", "doc", "MSWord");
		fileChooser.addChoosableFileFilter(filter);
		
		getContentPane().add(fileChooser);
		
		textArea = new JTextArea();
		textArea.setBounds(29, 353, 551, 231);
		textArea.setEditable(false);
		getContentPane().add(textArea);
		
		JLabel lblNewLabel_2 = new JLabel("Log:");
		lblNewLabel_2.setBounds(28, 319, 53, 24);
		lblNewLabel_2.setFont(new Font("Tahoma", Font.PLAIN, 15));
		getContentPane().add(lblNewLabel_2);
		
		JButton btnConnect = new JButton("Connect and Upload");
		btnConnect.setBounds(406, 284, 173, 25);
		btnConnect.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnConnect.addActionListener(this);
		btnConnect.setActionCommand("Connect and Upload");
		getContentPane().add(btnConnect);
	}
	
	public void actionPerformed(ActionEvent ae) {
		
		String action = ae.getActionCommand();
		if(action.equals("Connect and Upload")) {
			int portnum = Integer.parseInt(port.getText());
			String IP = hostIP.getText();
			
			//connect the socket
			try {
				sock = new Socket(IP, portnum);
				textArea.append("Connected.\n");
			} catch (Exception e) {
				textArea.append(e.toString() + "\n");
				return;
			}
			
			//send bytes
			try {
				out = new DataOutputStream(sock.getOutputStream());
				File file = fileChooser.getSelectedFile();
				String fileName = file.getName();
				String filePath = file.getPath();
				sendNullTerminatedString(fileName);
				sendFile(filePath);
	
			} catch (Exception e) {
				// TODO Auto-generated catch block
				textArea.append(e.toString() + "\n");
				return;
			}
			
			
		}
		
	}
	
	/**
	* This method takes a String s (either a file name or a file size,) as a
	* parameter, turns String s into a sequence of bytes ( byte[] ) by calling
	* getBytes() method, and sends the sequence of bytes to the Server. A null
	* character ‘\0’ is sent to the Server right after the byte sequence.
	 * @throws IOException 
	*/
	private void sendNullTerminatedString(String s) throws Exception {
		
		byte[] bytes = s.getBytes();
		
		byte[] endByte = nullTerminator.getBytes();
		
		
		out.write(bytes);
		out.write(endByte);
		
		textArea.append("Sent file name: " + s + "\n");
		

		return;
	}
	
	/**
	* This method takes a full-path file name, decomposes the file into smaller
	* chunks (each with 1024 bytes), and sends the chunks one by one to the
	* Server (loop until all bytes are sent.) A null character ‘\0’ is sent to
	* the Server right after the whole file is sent.
	 * @throws Exception 
	*/
	private void sendFile(String fullPathFileName) throws Exception {
		File file = new File(fullPathFileName);
		
		//send size of file
		long length = file.length();
		
		String len = "" + length + '\0';
		
		byte[] bytes = len.getBytes();
		out.write(bytes);
		textArea.append("Sent file size: " + len + "\n");
		
		//send bytes
		bytes = new byte[1024];
		
		InputStream fileStream = new FileInputStream(file);
		
		int count;
		textArea.append("Sending file.....\n");
		while( (count = fileStream.read(bytes)) > 0 ) {
			out.write(bytes, 0, count);
		}

		textArea.append("File sent. Waiting for Server......\n");

		textArea.append("Upload O.K.");
		
		//close
		fileStream.close();
		out.close();
		return;
	}

}
