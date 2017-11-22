package com.emg.projectsmanage.pojo;

import java.io.Serializable;
import java.util.Map;

import net.sf.json.JSONObject;

public class MessageModel implements Serializable {
	private static final long serialVersionUID = -6827068167089820798L;

	private Long id;

	private Integer type;

	private Integer sender;

	private String sendername;

	private Integer receiver;

	private String receivername;

	private String message;

	private Integer checked;

	private String createtime;

	@SuppressWarnings("unchecked")
	public MessageModel(String message) {
		try {
			Map<String, Object> mapObj = (Map<String, Object>) JSONObject.fromObject(message);
			if (mapObj.containsKey("id"))
				this.id = (Long) mapObj.get("id");
			if (mapObj.containsKey("type"))
				this.type = (Integer) mapObj.get("type");
			if (mapObj.containsKey("sender"))
				this.sender = (Integer) mapObj.get("sender");
			if (mapObj.containsKey("sendername"))
				this.sendername = mapObj.get("sendername").toString();
			if (mapObj.containsKey("receiver"))
				this.receiver = (Integer) mapObj.get("receiver");
			if (mapObj.containsKey("receivername"))
				this.receivername = mapObj.get("receivername").toString();
			if (mapObj.containsKey("message"))
				this.message = mapObj.get("message").toString();
			if (mapObj.containsKey("checked"))
				this.checked = (Integer) mapObj.get("checked");
		} catch (Exception e) {
		}
	}

	public MessageModel() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getSender() {
		return sender;
	}

	public void setSender(Integer sender) {
		this.sender = sender;
	}

	public String getSendername() {
		return sendername;
	}

	public void setSendername(String sendername) {
		this.sendername = sendername == null ? null : sendername.trim();
	}

	public Integer getReceiver() {
		return receiver;
	}

	public void setReceiver(Integer receiver) {
		this.receiver = receiver;
	}

	public String getReceivername() {
		return receivername;
	}

	public void setReceivername(String receivername) {
		this.receivername = receivername == null ? null : receivername.trim();
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message == null ? null : message.trim();
	}

	public Integer getChecked() {
		return checked;
	}

	public void setChecked(Integer checked) {
		this.checked = checked;
	}

	public String getCreatetime() {
		return createtime;
	}

	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}
}