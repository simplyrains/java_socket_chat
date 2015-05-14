import java.net.*;
import java.util.LinkedList;
import java.io.*;
public class ChatServerThread extends Thread {
	
	private ChatServer       server    	 = null;
	private Socket           socket    	 = null;
	private int              ID        	 = -1;
	private String			 username	 = null;
	private BufferedReader   socketIn  	 = null;
	private PrintWriter 	 socketOut   = null;
	private boolean			 running     = false;
	private boolean			 online      = true;
	private String			 groupName	 = "None";
	private LinkedList<ChatMessage> unreadMessage = null;
	
	public ChatServerThread(ChatServer _server, Socket _socket) {
		super();
		server = _server;
		socket = _socket;
		ID     = socket.getPort();
		username = ID+"";
		unreadMessage = new LinkedList<ChatMessage>();
	}
	
	//Encapsulate message with some data before sending
	public void prepareToSend(ChatMessage msg) {
		if(online) send(msg.toString());
		else{
			unreadMessage.add(msg);
		}
	}
	public void sendSystemStatus(String msg){
		send("*"+msg);
	}
	public void sendTerminateStatus(){
		send(".BYE");
	}
	
	//Send anything (ChatMessage/Status/Warning/etc.)
	public void send(String msg) {
		socketOut.println(msg.toString());
	}
	
	public void stopExecution(){
		running=false;
	}
	public int getID() {
		return ID;
	}
	public String getGroupName() {
		return groupName;
	}
	public String getUsername() {
		return username;
	}
	
	public void run() {
		running=true;
		System.out.println("Server Thread " + ID + " running.");
		//Handles incoming requests
		while (running) {
			try {
				String input = socketIn.readLine();
				int firstTab = input.indexOf("\t");
				String modeString = "";
				String messageString = "";

				if(firstTab!=-1){
					modeString = input.substring(0,firstTab);
					messageString = input.substring(firstTab);
				}
				else{
					modeString = input;
				}

				if (modeString.equals("CHAT")) {
					server.broadcast(groupName, username, messageString);
				}
				else if (modeString.equals("CREATE")) {
					server.createGroup(messageString);
				} 
				else if (modeString.equals("LISTGROUP")) {
					sendSystemStatus("List of Groups: "+server.getAllGroup());
				} 
				
				else if (modeString.equals("JOIN")) {
					if(!server.checkGroup(messageString)){
						sendSystemStatus("Error: Cannot join group ["+messageString+"]. Group not exists.");
					}
					else{
						sendSystemStatus("Joins group: "+messageString);
						groupName=messageString;
						online=true;
					}
				} 
				else if (modeString.equals("LEAVE")) {
					server.broadcast(groupName, username, "<Leaved>");
					sendSystemStatus("Leaves group: "+groupName);
					groupName="None";
					online=false;
				}

				else if (modeString.equals("SETUSERNAME")) {
					username=messageString;
					sendSystemStatus("Your username is now: "+username);
				}
				else if (modeString.equals("ENTER")) {
					online=true;
					sendSystemStatus("Enters group: "+groupName);
				} 
				else if (modeString.equals("STOPMSG")) {
					sendSystemStatus("Stop receving message from group: "+groupName);
					online=false;
				} 
				
				else if (modeString.equals("GETUNREAD")) {
					while(!unreadMessage.isEmpty()){
						prepareToSend(unreadMessage.removeFirst());
					}
				} 
				
				else if (modeString.equals("BYE")){
					server.removeClient(ID);
				}
				else{
					sendSystemStatus("Incorrect input.");
					continue;
				}
			}
			catch(IOException ioe) {
				System.out.println(ID + " ERROR reading: " + ioe.getMessage());
				server.removeClient(ID);
			}
		}
	}
	
	public void openSocketConnection() throws IOException {
		socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		socketOut = new PrintWriter(socket.getOutputStream(), true);
	}
	
	public void close() throws IOException {
		sendTerminateStatus();
		if (socket != null)    socket.close();
		if (socketIn != null)  socketIn.close();
		if (socketOut != null) socketOut.close();
	}
}