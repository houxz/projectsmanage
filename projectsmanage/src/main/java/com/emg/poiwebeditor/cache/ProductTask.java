package com.emg.poiwebeditor.cache;

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
import org.springframework.stereotype.Service;

import com.emg.poiwebeditor.client.TaskClient;
import com.emg.poiwebeditor.common.RoleType;
import com.emg.poiwebeditor.common.SystemType;
import com.emg.poiwebeditor.common.TypeEnum;
import com.emg.poiwebeditor.dao.projectsmanager.ProjectModelDao;
import com.emg.poiwebeditor.dao.projectsmanager.ProjectsUserModelDao;
import com.emg.poiwebeditor.pojo.ProjectModel;
import com.emg.poiwebeditor.pojo.ProjectModelExample;
import com.emg.poiwebeditor.pojo.ProjectsUserModel;
import com.emg.poiwebeditor.pojo.TaskModel;

@Service
public  class ProductTask {
	private static final Logger logger = LoggerFactory.getLogger(ProductTask.class);
	@Autowired
    private RedisTemplate<String, Object> redisTemplate;
	@Autowired
	private ProjectsUserModelDao projectsUserModelDao;
	@Autowired
	private ProjectModelDao projectModelDao;
	@Autowired
	private TaskClient taskClient;
	//制作任务缓存的消息队列 type
	public static final String TYPE_EDIT_QUENE = "001task";
	//制作中缓存的当前任务type
	public static final String TYPE_EDIT_MAKING = "002task";
	
	//制作任务缓存的消息队列 type
	public static final String TYPE_CHECK_QUENE = "001check";
	//制作中缓存的当前任务type
	public static final String TYPE_CHECK_MAKING = "002check";
	
	//制作任务缓存的消息队列 type
	public static final String TYPE_POLYGONEDIT_QUENE = "011task";
	//制作中缓存的当前任务type
	public static final String TYPE_POLYGONEDIT_MAKING = "012task";
	
	//制作任务缓存的消息队列 type
	public static final String TYPE_POLYGONCHECK_QUENE = "011check";
	//制作中缓存的当前任务type
	public static final String TYPE_POLYGONCHECK_MAKING = "012check";
	
	public static final int STATE_1 = 1;
	
	public static final int PROCESS_5 = 5;
	
	public static final int STATE_0 = 0;
	
	public static final int PROCESS_0 = 0;
	
	@Resource(name="redisTemplate")
	private ListOperations<String, TaskModel> listOps;
	
	@Resource(name="redisTemplate")
	private ValueOperations<String, TaskModel> valueOperations;
	
	// 默认在队列里缓存30个
	public static final int userTaskCache = 20;
    
    /**
     *  首次登陆时加载用户的20个任务，（1） 先看缓存里面有没有，如果有，够不够限定任务数，目前设置为20，如果不够则加载够20， （2）如果缓存里面没有则先去加载用户占用的任务，加载之后如果不足20个，再去加载初始状态的任务，加载满20个任务
     * @param user
     * @param type
     * @param typeInit 拿用户初始状态
     * @param typeUsing 由初始到占用, 点状：1，5
     * @param typeUsed 用户占用状态, 点状：1，5   面状：6，5
     
     * 
     */
    public void loadUserTask(int user, String type, String typemaking, TypeEnum typeInit, TypeEnum typeUsing, TypeEnum typeUsed) {
    	
    	String key = type + "_" + user;
    	if (redisTemplate.hasKey(key)) {
    		String u = key.split("_")[1];
    		int userid = Integer.parseInt(u);
    		if (userid <1) return;
    			// 如果已经存在此消息队列，则先删除些队列，再去加载新的任务，为了避免在消息队列里面去除
    		redisTemplate.delete(key);
    		long length = listOps.size(key);
    		if (length < userTaskCache) {
    			List<TaskModel> tasks = this.getUserTask(user, userTaskCache-length, typeUsed);
    			for (int i = tasks.size() - 1; i >=0 ; i--) {
        			String k = typemaking + "_" + user + "_current";
        	    	if (redisTemplate.hasKey(k) && valueOperations.size(k) > 0) {
        	    		TaskModel current = valueOperations.get(k);
    	    			if (tasks.get(i).getId().equals(current.getId())) {
    	    				tasks.remove(i);
    	    				break;
        				}
        			}
        		}
        		
    			if(tasks != null && !tasks.isEmpty())listOps.leftPushAll(key, tasks);
    			length = listOps.size(key);
    			if (length < userTaskCache) {
    				tasks = this.getUserTask_init(userid, userTaskCache - length, typeInit, typeUsing);
    				if(tasks != null && !tasks.isEmpty())listOps.leftPushAll(key, tasks);
    			}  			
    		}
    	}else {
    		List<TaskModel> tasks = this.getUserTask(user, userTaskCache, typeUsed);
    		if(tasks == null) return;
    		for (int i = tasks.size() - 1; i >=0 ; i--) {
    			String k = typemaking + "_" + user + "_current";
    	    	if (redisTemplate.hasKey(k) && valueOperations.size(k) > 0) {
    	    		TaskModel current = valueOperations.get(k);
	    			if (tasks.get(i).getId().equals(current.getId())) {
	    				tasks.remove(i);
	    				break;
    				}
    			}
    		}
    		
    		if(tasks != null && !tasks.isEmpty())      	listOps.leftPushAll(key, tasks);
        	if (tasks != null && tasks.size() < userTaskCache) {
        		List<TaskModel> tasks2 = this.getUserTask_init(user, userTaskCache - tasks.size(), typeInit, typeUsing);
        		if(tasks2 != null && !tasks2.isEmpty()) listOps.leftPushAll(key, tasks2);
        	}
    	}
		
    }
    
    /**
     *  获取需要执行的下一个任务， 先从缓存的当前正在执行的任务里面拿，如果没有，再从消息队列里面拿
     * @param user
     * @param type 消息队列的标识
     * @param typemaking 缓存里面当前任务的标识
     * @param typeInit 初始状态的任务
     * @param typeUsed 已经占用状态的任务
     * @return
     */
    public TaskModel popUserTask(int user, String type, String typemaking, TypeEnum typeInit,  TypeEnum typeUsing, TypeEnum typeUsed, int index) {
    	//用来缓解递归调用太多次，当此人消息队列中做完了，又在重新分配新任务的时候，及时获取到新分配的任务
    	if (index > 1) return null;
    	String key = typemaking + "_" + user + "_current";
    	
    	// for test
    	Long vnum2 = valueOperations.size(key);
    	
    	if (redisTemplate.hasKey(key) && valueOperations.size(key) > 0) {
    		return valueOperations.get(key);
    	}else {
    		String k2  = type + "_" + user;
        	TaskModel task = listOps.rightPop(k2);
        	
        	if ( task == null) {
        		this.loadUserTask(user, type,typemaking, typeInit, typeUsing, typeUsed);
        	}
        	if (task == null) task = this.popUserTask(user, type, typemaking, typeInit,typeUsing, typeUsed, ++index);
        	valueOperations.set(key, task);
        	Long vnum = valueOperations.size(key);
        	if (listOps.size(k2) < 3 ) {
        		/*LoadTask thread = new LoadTask(user, type,typemaking, typeInit, typeUsed);
        		thread.start();*/
        		loadUserTask(user, type,typemaking, typeInit, typeUsing,typeUsed);
        	}
        	return task;
    	}
    	
    	
    }
    
    /**
     * 加载任务
     * @author Administrator
     *
     */
    class LoadTask extends Thread {
    	int user; String type; String typemaking; TypeEnum typeInit; TypeEnum typeUsed;TypeEnum typeUsing;
    	public LoadTask(int user, String type, String typemaking, TypeEnum typeInit, TypeEnum typeUsed, TypeEnum typeUsing) {
    		this.user = user;
    		this.type = type;
    		this.typemaking = typemaking;
    		this.typeInit = typeInit;
    		this.typeUsed = typeUsed;
    		this.typeUsing = typeUsing;
    	}
    	
    	public void run() {
    		logger.debug("load user: " + user + " tasks");
    		loadUserTask(user, type,typemaking, typeInit, typeUsing, typeUsed);
    	}
    }
    
   /**
    *  获取当前正在执行的任务
    * @param user
    * @param typemaking
    * @return
    */
    public TaskModel popCurrentTask(int user, String typemaking) {
    	String key = typemaking + "_" + user + "_current";
    	if (redisTemplate.hasKey(key) && valueOperations.size(key) > 0) {
    		return valueOperations.get(key);
    	}
    	return null;
    }
    
    /**
     * 清除消息队列里面缓存的消息
     * @param user
     * @param type
     * @return
     */
    public void removeUserTask(int user, String type, TypeEnum typeEnum) throws Exception{
    	String key = type + "_" + user;
    	redisTemplate.delete(key);
    	
    	// taskClient.initTaskState(user, typeEnum);
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
    private List<TaskModel> getUserTask(Integer userid, long num, TypeEnum type) {
    	List<TaskModel> tasks = new ArrayList<TaskModel>();
		try {
			List<Long> _myProjectIDs = this.getProjectIds(userid);
			if (_myProjectIDs != null && !_myProjectIDs.isEmpty()) {
				tasks = taskClient.selectUserTask(_myProjectIDs, userid, num, type);
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
    private List<TaskModel> getUserTask_init(Integer userid, long num, TypeEnum init, TypeEnum using) {
    	List<TaskModel> tasks = new ArrayList<TaskModel>();
		try {
			List<Long> _myProjectIDs = this.getProjectIds(userid);
			if (_myProjectIDs != null && !_myProjectIDs.isEmpty()) {
				tasks = taskClient.selectUserInitTask(_myProjectIDs, userid, num,init, using);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return tasks;
	}
    
    public List<Long> getProjectIds(int userid) {
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
    
    /**
     * 清除消息队列
     * @param queueKey
     * @param type
     * @param typeState
     * @throws Exception
     */
    private void clearQueue(String queueKey, String type, TypeEnum typeState) throws Exception{
    	Set<String> keys = redisTemplate.keys(queueKey);
    	if(keys != null) { 
	    	for (String key : keys) {
	    		String userid = key.split("_")[1];
	    		if (userid == null || userid.isEmpty()) continue;
	    		int uid = Integer.parseInt(userid);
	    		this.removeUserTask(uid, type, typeState);
	    	}
    	}
    }
    
    /**
     * 删除当前正在制作中的任务
     * @param queueKey
     * @param type
     * @throws Exception
     */
    private void clearCurrentQueue(String queueKey, String type) throws Exception{
    	Set<String> editCurrentKeys = redisTemplate.keys(queueKey);
    	if(editCurrentKeys != null) {
	    	for (String key : editCurrentKeys) {
	    		String userid = key.split("_")[1];
	    		if (userid == null || userid.isEmpty()) continue;
	    		int uid = Integer.parseInt(userid);
	    		this.removeCurrentUserTask(uid, type);
	    	}
    	}
    }
    
    
    /**
     * 删除缓存里面所有消息队列
     */
    public void removeUserTask() throws Exception{
    	// 点状POI
    	this.clearQueue("*001task_*", ProductTask.TYPE_EDIT_QUENE, TypeEnum.edit_using);
    	this.clearQueue("*001check_*", ProductTask.TYPE_CHECK_QUENE, TypeEnum.check_using);
    	this.clearCurrentQueue("*002check_*", ProductTask.TYPE_CHECK_MAKING);
    	this.clearCurrentQueue("*002task_*", ProductTask.TYPE_EDIT_MAKING);
    	
    	//面状POI
    	this.clearQueue("*011task_*", ProductTask.TYPE_POLYGONEDIT_QUENE, TypeEnum.polygon_edit_using);
    	this.clearQueue("*011check_*", ProductTask.TYPE_POLYGONCHECK_QUENE, TypeEnum.polygon_check_using);
    	this.clearCurrentQueue("*012check_*", ProductTask.TYPE_POLYGONCHECK_MAKING);
    	this.clearCurrentQueue("*012task_*", ProductTask.TYPE_POLYGONEDIT_MAKING);
    }
    
    /**
	 * 用来校正当前任务是否可以编辑
	 * @param taskdb 
	 * @param userid
	 * @return
	 */
	public boolean canEdit(TaskModel taskdb, int userid, String type) throws Exception {
		if (taskdb == null || ((taskdb.getState() == 2 || taskdb.getState() == 3)  && taskdb.getProcess() == 5)) {
			TaskModel temptask = this.popCurrentTask(userid, type);
			if (temptask != null && taskdb != null && taskdb.getId().equals(temptask.getId())) this.removeCurrentUserTask(userid, type);
			return false;
		}else if(taskdb != null  && taskdb.getProcess() == 5 && taskdb.getEditid() != userid) {
			TaskModel temptask = this.popCurrentTask(userid, type);
			if (temptask != null && taskdb != null && taskdb.getId().equals(temptask.getId())) this.removeCurrentUserTask(userid, type);
			return false;
		}else if(taskdb != null && taskdb.getProcess() == 7 && taskdb.getCheckid() != userid ) {
			TaskModel temptask = this.popCurrentTask(userid,type);
			if (temptask != null && taskdb != null && taskdb.getId().equals(temptask.getId())) this.removeCurrentUserTask(userid, type);
			return false;
		}
		return true;
	}

}