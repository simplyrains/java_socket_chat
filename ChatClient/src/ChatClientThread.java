import java.net.*;
import java.io.*;
public class ChatClientThread extends Thread {
	private Socket           socket   = null;
	private ChatClient       client   = null;
	private BufferedReader   socketIn = null;
	private boolean	running = false;
	
	
	public ChatClientThread(ChatClient _client, Socket _socket) {
		client   = _client;
		socket   = _socket;
		openSocketConnection();
		start();
	}
	public void openSocketConnection() {
		try {
			socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		}
		catch(IOException ioe) {
			System.out.println("Error getting input stream: " + ioe);
			client.stop();
		}
	}
	
	public void closeSocketConnection() {
		try {
			if (socketIn != null) socketIn.close();
		}
		catch(IOException ioe) {
			System.out.println("Error closing input stream: " + ioe);
		}
	}
	
	public void stopExecution(){
		running=false;
	}

	public void run() {
		running = true;
		while (running) {
			try {
				client.handle(socketIn.readLine());
			}
			catch(IOException ioe) {
				System.out.println("Listening error: " + ioe.getMessage());
				client.stop();
			}
		}
	}
}