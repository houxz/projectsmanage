package com.emg.poiwebeditor.cache;

import redis.clients.jedis.HostAndPort;

public class RedisShard extends HostAndPort {
	
	private static final long serialVersionUID = -6156368110346929741L;
	
	private String password;

	public RedisShard(String host, int port, String password) {
		super(host, port);
		this.password = password;
	}
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
