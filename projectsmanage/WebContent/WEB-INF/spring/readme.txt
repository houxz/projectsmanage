使用注意事项
1） 数据库连接修改        连接 北京yiyi    还是正式库  ?
   1.1 root-context.xml 中
   	<context:property-placeholder location="WEB-INF/spring/db41.101.properties" ignore-unresolvable="true"/>
	<context:property-placeholder location="WEB-INF/spring/webapi_yiyi.properties" ignore-unresolvable="true"/>
	
	1.2  servlet-context.xml 中
	<context:property-placeholder location="WEB-INF/spring/webapi_yiyi.properties" ignore-unresolvable="true"/>
	
2)  定期器获取项目 -项目数据库名称 配置  ( 连接的是北京yiyi 还是正式库 )
    scheduler.properties  为了减少错误配置，修改为两个文件 scheduler_yy.properties  scheduler_yiyi.properties
    # bj yiyi ceshi 
	project.projectdbname=projectmanager
	project.processdbname=process

