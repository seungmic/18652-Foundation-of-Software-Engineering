
import java.io.IOException;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonWriter;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

@ServerEndpoint("/chatroomServerEndpoint")
public class ChatRoomFinal {
	static Set<Session> users = Collections.synchronizedSet(new HashSet<Session>());
	@OnOpen
	public void onOpen(Session userSession) {
		users.add(userSession);
	}
	@OnMessage
	public void onMessage(String message, Session userSession) throws IOException {
		
		String username = (String) userSession.getUserProperties().get("username");

		if (username==null && message != null) {
			userSession.getUserProperties().put("username", message);
			userSession.getBasicRemote().sendText(buildJsonData("Hello, "+message, ". Welcome to ChatApp!"));
		} else {

			Iterator<Session> iterator = users.iterator();
			DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Calendar cal = Calendar.getInstance();
			String d = (df.format(cal.getTime()));
			while (iterator.hasNext()) iterator.next().getBasicRemote().sendText(buildJsonData(username, message + ", " + d));
		}
		
		
	}
	@OnClose
	public void onClose(Session userSession) {

		users.remove(userSession);
	}
	
	private String buildJsonData(String username, String message) {  
		JsonObject jObject = Json.createObjectBuilder().add("message", username+" "+message).build();
		StringWriter sw = new StringWriter();
		try (JsonWriter jw = Json.createWriter(sw)) {jw.write(jObject);}
		return sw.toString();
	}
}
