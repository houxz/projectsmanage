package com.emg.poiwebeditor.cache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.emg.poiwebeditor.client.TaskModelClient;
import com.emg.poiwebeditor.common.RoleType;
import com.emg.poiwebeditor.common.SystemType;
import com.emg.poiwebeditor.dao.projectsmanager.ProjectModelDao;
import com.emg.poiwebeditor.dao.projectsmanager.ProjectsUserModelDao;
import com.emg.poiwebeditor.pojo.ProjectModel;
import com.emg.poiwebeditor.pojo.ProjectModelExample;
import com.emg.poiwebeditor.pojo.ProjectsUserModel;
import com.emg.poiwebeditor.pojo.TaskModel;

/**
 * 定时获取任务
 * 1. 任务采用消息队列listopration, key: type_userid,例：task_1146, 现在type有：task(制作), check（抽检）
 * 2. 当任务在获取之后，一刷新时会自动获取下一任务，但可能当前任务并没有处理完，为了解决这种问题，加了一种机制，拿出来的当前正在处理的任务，单独缓存以String, object的形式进行缓存，
 * 		以防止作业人员频繁刷新， 其中当前制作中的任务key：type_userid_current， 例：task_1146_current， 当提交里把该缓存清掉
 * @author Administrator
 *
 */
@Component
public class ProductTask {
	private static final Logger logger = LoggerFactory.getLogger(ProductTask.class);
	@Autowired
    private RedisTemplate<String, Object> redisTemplate;
	@Autowired
	private ProjectsUserModelDao projectsUserModelDao;
	@Autowired
	private ProjectModelDao projectModelDao;
	@Autowired
	private TaskModelClient taskModelClient;
	//制作任务缓存的消息队列 type
	public static final String TYPE_QUENE = "001task";
	//制作中缓存的当前任务type
	public static final String TYPE_MAKING = "002task";
	
	public static final int STATE_1 = 1;
	
	public static final int PROCESS_5 = 5;
	
	public static final int STATE_0 = 0;
	
	public static final int PROCESS_0 = 0;
	
	@Resource(name="redisTemplate")
	private ListOperations<String, TaskModel> listOps;
	
	@Resource(name="redisTemplate")
	private ValueOperations<String, TaskModel> valueOperations;
	
	// 默认在队列里缓存30个
	private static final int userTaskCache = 20;
	
    public void sendMessage(String channel, Serializable message) {
        redisTemplate.convertAndSend(channel, message);
    }
    

    
    /**
     * 获取所有缓存着的队列ID
     * @return
     */
    @Scheduled(cron = "${scheduler.gettask.dotime}")
    public List<Long> getAllUserId() {
    	Set<String> keys = redisTemplate.keys("task*");
    	
    	for (String key : keys) {
    		// 人员的任务缓存模式key为001task_userid, 每个人都对应着一个消息队列
    		if (key.indexOf("_") < 0 || key.indexOf("001") < 0) continue;
    		String u = key.split("_")[1];
    		int userid = Integer.parseInt(u);
    		if (userid <1) continue;
    		
    		long length = listOps.size(key);
    		if (length < userTaskCache) {
    			List<TaskModel> tasks = this.getUserTask_init(userid, userTaskCache - length);
    			listOps.leftPushAll(key, tasks);
    			/*for(long i =  userTaskCache - length; i < userTaskCache; i++) {
    			TaskModel task = this.getNextEditTask(userid);
				listOps.leftPush(key, task);
    			}*/
    			
    		}
    		
    	}
    	return null;
    }
    
    
    
    /**
     * 首次登陆时加载用户的20个任务
     * @param user
     * @param type
     */
    public void loadUserTask(int user, String type) {
    	
    	String key = type + "_" + user;
    	if (redisTemplate.hasKey(key)) {
    		String u = key.split("_")[1];
    		int userid = Integer.parseInt(u);
    		if (userid <1) return;
    		
    		long length = listOps.size(key);
    		if (length < userTaskCache) {
    			List<TaskModel> tasks = this.getUserTask_init(userid, userTaskCache - length);
    			listOps.leftPushAll(key, tasks);
    			    			
    		}
    	}else {
    		List<TaskModel> tasks = this.getUserTask(user, userTaskCache);
    		if (tasks != null && tasks.size() > 0)      	listOps.leftPushAll(key, tasks);
        	if (tasks.size() < userTaskCache) {
        		List<TaskModel> tasks2 = this.getUserTask_init(user, userTaskCache);
        		listOps.leftPushAll(key, tasks2);
        	}
    	}
		
    }
    
    /**
     * 获取需要执行的下一个任务， 先从缓存的当前正在执行的任务里面拿，如果没有，再从消息队列里面拿
     * @param user
     * @param type消息队列的type
     * @param 制作中的type
     * @return
     */
    public TaskModel popUserTask(int user, String type, String typemaking) {
    	String key = typemaking + "_" + user + "_current";
    	if (redisTemplate.hasKey(key)) {
    		return valueOperations.get(key);
    	}else {
    		String k2  = type + "_" + user;
        	TaskModel task = listOps.rightPop(k2);
        	valueOperations.set(key, task);
        	if (listOps.size(k2) < 3) {
        		this.loadUserTask(user, type);
        	}
        	return task;
    	}
    	
    	
    }
    
    /**
     * 清除消息队列里面缓存的消息
     * @param user
     * @param type
     * @return
     */
    public void removeUserTask(int user, String type, int state, int process) throws Exception{
    	String key = type + "_" + user;
    	redisTemplate.delete(key);
    	
    	taskModelClient.initTaskState(user, state, process);
    }
    
    /**
     * 当提交任务的时候清除缓存的当前正在制作的任务
     * @param user
     * @param type
     * @return
     */
    public void removeCurrentUserTask(int user, String type) throws Exception{
    	String key = type + "_" + user + "_current";
    	redisTemplate.delete(key);
    	
    }
    
    /**
     * 初始状态，拿用户占用的任务
     * @param userid
     * @param num
     * @return
     */
    private List<TaskModel> getUserTask(Integer userid, long num) {
    	List<TaskModel> tasks = null;
		try {
			List<Long> _myProjectIDs = this.getProjectIds(userid);
			if (_myProjectIDs != null && !_myProjectIDs.isEmpty()) {
				tasks = taskModelClient.selectUserTask(_myProjectIDs, userid, num);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return tasks;
	}
    
    /**
     * 轮循拿用户任务，拿初始状态的
     * @param userid
     * @param num
     * @return
     */
    private List<TaskModel> getUserTask_init(Integer userid, long num) {
    	List<TaskModel> tasks = null;
		try {
			List<Long> _myProjectIDs = this.getProjectIds(userid);
			if (_myProjectIDs != null && !_myProjectIDs.isEmpty()) {
				tasks = taskModelClient.selectUserInitTask(_myProjectIDs, userid, num);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return tasks;
	}
    
    private List<Long> getProjectIds(int userid) {
    	List<Long> _myProjectIDs = new ArrayList<Long>();
    	try {
			RoleType roleType = RoleType.ROLE_WORKER;
			SystemType systemType = SystemType.poi_polymerize;
			
			List<ProjectModel> myProjects = new ArrayList<ProjectModel>();
			
			ProjectsUserModel record = new ProjectsUserModel();
			record.setUserid(userid);
			record.setRoleid(roleType.getValue());
			
			List<ProjectsUserModel> projectsUserModels = projectsUserModelDao.queryProjectUsers(record);
			List<Long> myProjectIDs = new ArrayList<Long>();
			myProjectIDs.add(-1L);
			for (ProjectsUserModel projectsUserModel : projectsUserModels) {
				myProjectIDs.add(Long.valueOf(projectsUserModel.getPid()));
			}

			ProjectModelExample example = new ProjectModelExample();
			example.or()
				.andSystemidEqualTo(systemType.getValue())
				.andOverstateEqualTo(1)
				.andOwnerEqualTo(1)
				.andIdIn(myProjectIDs);
			example.setOrderByClause("priority DESC, id");
			myProjects.addAll(projectModelDao.selectByExample(example));
			
			example.clear();
			example.or()
				.andSystemidEqualTo(systemType.getValue())
				.andOverstateEqualTo(1)
				.andOwnerEqualTo(0);
			example.setOrderByClause("priority DESC, id");
			myProjects.addAll(projectModelDao.selectByExample(example));
			
			if (myProjects != null && !myProjects.isEmpty()) {
				
				for (ProjectModel myProject : myProjects) {
					_myProjectIDs.add(myProject.getId());
				}
				
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
    	return _myProjectIDs;
    }

}
