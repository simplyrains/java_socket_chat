import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatMessage {

	private ChatServerThread client = null;
	private String message = null;
	private String groupName = "";
	private Date time = null;
	private String sender = null;
	private DateFormat lDateFormat = new SimpleDateFormat("hh:mm:ss");

	public ChatMessage(ChatServerThread client_, String message_, String groupName_, Date time_, String sender_){
		client=client_;
		message=message_;
		groupName=groupName_;
		time=time_;
		sender=sender_;
	}
	
	public ChatServerThread getClient() {
		return client;
	}

	public String getMessage() {
		return message;
	}

	public String toString(){
		return lDateFormat.format(time)+" | "+ sender + ": " + message;
	}
	
	public String getGroupName() {
		return groupName;
	}

	public void setClient(ChatServerThread client) {
		this.client = client;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
}
