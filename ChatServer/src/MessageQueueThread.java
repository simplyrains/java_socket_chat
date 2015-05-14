import java.util.ArrayList;

public class MessageQueueThread extends Thread {
	private boolean			 			running     = false;
	private ArrayList<ChatMessage>		 messageQueue = null;
	
	public MessageQueueThread() {
		super();
		messageQueue = new ArrayList<ChatMessage>();
	}

	
	public void enqueue(ChatMessage newMessage){
		messageQueue.add(newMessage);
	}
	public void run() {
		System.out.print("MessageQueue is Running.");
		running=true;
		while (running) {
			System.out.println(".");
			if(!messageQueue.isEmpty()){
				ChatMessage message = messageQueue.remove(0);
				ChatServerThread client = message.getClient();
				client.prepareToSend(message);
			}
		}
	}
}