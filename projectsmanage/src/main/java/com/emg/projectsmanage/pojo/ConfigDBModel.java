package com.emg.projectsmanage.pojo;

public class ConfigDBModel {
    private Integer id;

    private String ip;

    private Integer dbtype;

    private String dbschema;
    
    private String connname;

    private String dbname;

    private String user;

    private String password;

    private String port;
    
    //add by lianhr begin 2019/02/12
    private Integer online;
    //add by lianhr end
    
    //add by lianhr begin 2019/03/06
    private String remarkname;
    //add by lianhr edn

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip == null ? null : ip.trim();
    }

    public Integer getDbtype() {
        return dbtype;
    }

    public void setDbtype(Integer dbtype) {
        this.dbtype = dbtype;
    }

    public String getConnname() {
        return connname;
    }

    public void setConnname(String connname) {
        this.connname = connname == null ? null : connname.trim();
    }

    public String getDbname() {
        return dbname;
    }

    public void setDbname(String dbname) {
        this.dbname = dbname == null ? null : dbname.trim();
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user == null ? null : user.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port == null ? null : port.trim();
    }

	public String getDbschema() {
		return dbschema;
	}

	public void setDbschema(String dbschema) {
		this.dbschema = dbschema;
	}
	
	//add by lianhr begin 2018/02/12
	public Integer getOnline() {
        return online;
    }

    public void setOnline(Integer online) {
        this.online = online;
    }
	//add by lianhr end

    
    //add by lianhr begin 2019/03/06
    public String getRemarkname() {
    	return remarkname;
    }
    
    public void setRemarkname(String remarkname) {
    	this.remarkname = remarkname;
    }
    
    //add by lianhr end
}