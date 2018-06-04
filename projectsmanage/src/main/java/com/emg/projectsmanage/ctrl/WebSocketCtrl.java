package com.emg.projectsmanage.ctrl;

import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;

import com.emg.projectsmanage.bean.GetHttpSessionConfigurator;
import com.emg.projectsmanage.common.CommonConstants;
import com.emg.projectsmanage.pojo.MessageModel;

@ServerEndpoint(value = "/socket.web", configurator = GetHttpSessionConfigurator.class)
public class WebSocketCtrl {
	
	private static final Logger logger = LoggerFactory.getLogger(WebSocketCtrl.class);
	
	private static int onlineCount = 0;

	public static HashMap<Integer, WebSocketCtrl> webSocketSet = new HashMap<Integer, WebSocketCtrl>();

	private Integer userID;
	private String userName;
	private Session session;

	@OnOpen
	public void onOpen(Session session, EndpointConfig config) {
		this.session = session;
		HttpSession httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
		this.userID = (Integer) httpSession.getAttribute(CommonConstants.SESSION_USER_ID);
		this.userName = httpSession.getAttribute(CommonConstants.SESSION_USER_NAME).toString();
		webSocketSet.put(this.userID, this);
		addOnlineCount();
		System.out.println(this.userName + " 加入！当前在线人数为" + getOnlineCount());
	}

	@OnClose
	public void onClose() {
		webSocketSet.remove(this.userID);
		subOnlineCount();
		System.out.println(this.userName + " 连接关闭！当前在线人数为" + getOnlineCount());
	}

	@OnMessage
	public void onMessage(String message) {
		try {
			MessageModel messageModel = new MessageModel(message);
			Integer receiverID = messageModel.getReceiver();
			String content = messageModel.getMessage();
			if (webSocketSet.containsKey(receiverID)) {
				WebSocketCtrl item = webSocketSet.get(receiverID);
				System.out.println(this.userName + " 发给 " + item.userName + " 的消息:" + content);
				item.sendMessage(content);
			} else {
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}

	@OnError
	public void onError(Throwable error) {
		System.out.println("发生错误");
		error.printStackTrace();
	}

	public void sendMessage(String message) throws IOException {
		this.session.getBasicRemote().sendText(message);

	}

	public static synchronized int getOnlineCount() {
		return onlineCount;
	}

	public static synchronized void addOnlineCount() {
		WebSocketCtrl.onlineCount++;
	}

	public static synchronized void subOnlineCount() {
		WebSocketCtrl.onlineCount--;
	}

}
