package com.emg.projectsmanage.pojo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CapacityQualityModelExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public CapacityQualityModelExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andIdIsNull() {
            addCriterion("id is null");
            return (Criteria) this;
        }

        public Criteria andIdIsNotNull() {
            addCriterion("id is not null");
            return (Criteria) this;
        }

        public Criteria andIdEqualTo(Long value) {
            addCriterion("id =", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotEqualTo(Long value) {
            addCriterion("id <>", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThan(Long value) {
            addCriterion("id >", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThanOrEqualTo(Long value) {
            addCriterion("id >=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThan(Long value) {
            addCriterion("id <", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThanOrEqualTo(Long value) {
            addCriterion("id <=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdIn(List<Long> values) {
            addCriterion("id in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotIn(List<Long> values) {
            addCriterion("id not in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdBetween(Long value1, Long value2) {
            addCriterion("id between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotBetween(Long value1, Long value2) {
            addCriterion("id not between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andTasktypeIsNull() {
            addCriterion("tasktype is null");
            return (Criteria) this;
        }

        public Criteria andTasktypeIsNotNull() {
            addCriterion("tasktype is not null");
            return (Criteria) this;
        }

        public Criteria andTasktypeEqualTo(Integer value) {
            addCriterion("tasktype =", value, "tasktype");
            return (Criteria) this;
        }

        public Criteria andTasktypeNotEqualTo(Integer value) {
            addCriterion("tasktype <>", value, "tasktype");
            return (Criteria) this;
        }

        public Criteria andTasktypeGreaterThan(Integer value) {
            addCriterion("tasktype >", value, "tasktype");
            return (Criteria) this;
        }

        public Criteria andTasktypeGreaterThanOrEqualTo(Integer value) {
            addCriterion("tasktype >=", value, "tasktype");
            return (Criteria) this;
        }

        public Criteria andTasktypeLessThan(Integer value) {
            addCriterion("tasktype <", value, "tasktype");
            return (Criteria) this;
        }

        public Criteria andTasktypeLessThanOrEqualTo(Integer value) {
            addCriterion("tasktype <=", value, "tasktype");
            return (Criteria) this;
        }

        public Criteria andTasktypeIn(List<Integer> values) {
            addCriterion("tasktype in", values, "tasktype");
            return (Criteria) this;
        }

        public Criteria andTasktypeNotIn(List<Integer> values) {
            addCriterion("tasktype not in", values, "tasktype");
            return (Criteria) this;
        }

        public Criteria andTasktypeBetween(Integer value1, Integer value2) {
            addCriterion("tasktype between", value1, value2, "tasktype");
            return (Criteria) this;
        }

        public Criteria andTasktypeNotBetween(Integer value1, Integer value2) {
            addCriterion("tasktype not between", value1, value2, "tasktype");
            return (Criteria) this;
        }

        public Criteria andProcessidIsNull() {
            addCriterion("processid is null");
            return (Criteria) this;
        }

        public Criteria andProcessidIsNotNull() {
            addCriterion("processid is not null");
            return (Criteria) this;
        }

        public Criteria andProcessidEqualTo(Long value) {
            addCriterion("processid =", value, "processid");
            return (Criteria) this;
        }

        public Criteria andProcessidNotEqualTo(Long value) {
            addCriterion("processid <>", value, "processid");
            return (Criteria) this;
        }

        public Criteria andProcessidGreaterThan(Long value) {
            addCriterion("processid >", value, "processid");
            return (Criteria) this;
        }

        public Criteria andProcessidGreaterThanOrEqualTo(Long value) {
            addCriterion("processid >=", value, "processid");
            return (Criteria) this;
        }

        public Criteria andProcessidLessThan(Long value) {
            addCriterion("processid <", value, "processid");
            return (Criteria) this;
        }

        public Criteria andProcessidLessThanOrEqualTo(Long value) {
            addCriterion("processid <=", value, "processid");
            return (Criteria) this;
        }

        public Criteria andProcessidIn(List<Long> values) {
            addCriterion("processid in", values, "processid");
            return (Criteria) this;
        }

        public Criteria andProcessidNotIn(List<Long> values) {
            addCriterion("processid not in", values, "processid");
            return (Criteria) this;
        }

        public Criteria andProcessidBetween(Long value1, Long value2) {
            addCriterion("processid between", value1, value2, "processid");
            return (Criteria) this;
        }

        public Criteria andProcessidNotBetween(Long value1, Long value2) {
            addCriterion("processid not between", value1, value2, "processid");
            return (Criteria) this;
        }

        public Criteria andProcessnameIsNull() {
            addCriterion("processname is null");
            return (Criteria) this;
        }

        public Criteria andProcessnameIsNotNull() {
            addCriterion("processname is not null");
            return (Criteria) this;
        }

        public Criteria andProcessnameEqualTo(String value) {
            addCriterion("processname =", value, "processname");
            return (Criteria) this;
        }

        public Criteria andProcessnameNotEqualTo(String value) {
            addCriterion("processname <>", value, "processname");
            return (Criteria) this;
        }

        public Criteria andProcessnameGreaterThan(String value) {
            addCriterion("processname >", value, "processname");
            return (Criteria) this;
        }

        public Criteria andProcessnameGreaterThanOrEqualTo(String value) {
            addCriterion("processname >=", value, "processname");
            return (Criteria) this;
        }

        public Criteria andProcessnameLessThan(String value) {
            addCriterion("processname <", value, "processname");
            return (Criteria) this;
        }

        public Criteria andProcessnameLessThanOrEqualTo(String value) {
            addCriterion("processname <=", value, "processname");
            return (Criteria) this;
        }

        public Criteria andProcessnameLike(String value) {
            addCriterion("processname like", value, "processname");
            return (Criteria) this;
        }

        public Criteria andProcessnameNotLike(String value) {
            addCriterion("processname not like", value, "processname");
            return (Criteria) this;
        }

        public Criteria andProcessnameIn(List<String> values) {
            addCriterion("processname in", values, "processname");
            return (Criteria) this;
        }

        public Criteria andProcessnameNotIn(List<String> values) {
            addCriterion("processname not in", values, "processname");
            return (Criteria) this;
        }

        public Criteria andProcessnameBetween(String value1, String value2) {
            addCriterion("processname between", value1, value2, "processname");
            return (Criteria) this;
        }

        public Criteria andProcessnameNotBetween(String value1, String value2) {
            addCriterion("processname not between", value1, value2, "processname");
            return (Criteria) this;
        }

        public Criteria andProjectidIsNull() {
            addCriterion("projectid is null");
            return (Criteria) this;
        }

        public Criteria andProjectidIsNotNull() {
            addCriterion("projectid is not null");
            return (Criteria) this;
        }

        public Criteria andProjectidEqualTo(Long value) {
            addCriterion("projectid =", value, "projectid");
            return (Criteria) this;
        }

        public Criteria andProjectidNotEqualTo(Long value) {
            addCriterion("projectid <>", value, "projectid");
            return (Criteria) this;
        }

        public Criteria andProjectidGreaterThan(Long value) {
            addCriterion("projectid >", value, "projectid");
            return (Criteria) this;
        }

        public Criteria andProjectidGreaterThanOrEqualTo(Long value) {
            addCriterion("projectid >=", value, "projectid");
            return (Criteria) this;
        }

        public Criteria andProjectidLessThan(Long value) {
            addCriterion("projectid <", value, "projectid");
            return (Criteria) this;
        }

        public Criteria andProjectidLessThanOrEqualTo(Long value) {
            addCriterion("projectid <=", value, "projectid");
            return (Criteria) this;
        }

        public Criteria andProjectidIn(List<Long> values) {
            addCriterion("projectid in", values, "projectid");
            return (Criteria) this;
        }

        public Criteria andProjectidNotIn(List<Long> values) {
            addCriterion("projectid not in", values, "projectid");
            return (Criteria) this;
        }

        public Criteria andProjectidBetween(Long value1, Long value2) {
            addCriterion("projectid between", value1, value2, "projectid");
            return (Criteria) this;
        }

        public Criteria andProjectidNotBetween(Long value1, Long value2) {
            addCriterion("projectid not between", value1, value2, "projectid");
            return (Criteria) this;
        }

        public Criteria andErrortypeIsNull() {
            addCriterion("errortype is null");
            return (Criteria) this;
        }

        public Criteria andErrortypeIsNotNull() {
            addCriterion("errortype is not null");
            return (Criteria) this;
        }

        public Criteria andErrortypeEqualTo(Long value) {
            addCriterion("errortype =", value, "errortype");
            return (Criteria) this;
        }

        public Criteria andErrortypeNotEqualTo(Long value) {
            addCriterion("errortype <>", value, "errortype");
            return (Criteria) this;
        }

        public Criteria andErrortypeGreaterThan(Long value) {
            addCriterion("errortype >", value, "errortype");
            return (Criteria) this;
        }

        public Criteria andErrortypeGreaterThanOrEqualTo(Long value) {
            addCriterion("errortype >=", value, "errortype");
            return (Criteria) this;
        }

        public Criteria andErrortypeLessThan(Long value) {
            addCriterion("errortype <", value, "errortype");
            return (Criteria) this;
        }

        public Criteria andErrortypeLessThanOrEqualTo(Long value) {
            addCriterion("errortype <=", value, "errortype");
            return (Criteria) this;
        }

        public Criteria andErrortypeIn(List<Long> values) {
            addCriterion("errortype in", values, "errortype");
            return (Criteria) this;
        }

        public Criteria andErrortypeNotIn(List<Long> values) {
            addCriterion("errortype not in", values, "errortype");
            return (Criteria) this;
        }

        public Criteria andErrortypeBetween(Long value1, Long value2) {
            addCriterion("errortype between", value1, value2, "errortype");
            return (Criteria) this;
        }

        public Criteria andErrortypeNotBetween(Long value1, Long value2) {
            addCriterion("errortype not between", value1, value2, "errortype");
            return (Criteria) this;
        }

        public Criteria andUseridIsNull() {
            addCriterion("userid is null");
            return (Criteria) this;
        }

        public Criteria andUseridIsNotNull() {
            addCriterion("userid is not null");
            return (Criteria) this;
        }

        public Criteria andUseridEqualTo(Integer value) {
            addCriterion("userid =", value, "userid");
            return (Criteria) this;
        }

        public Criteria andUseridNotEqualTo(Integer value) {
            addCriterion("userid <>", value, "userid");
            return (Criteria) this;
        }

        public Criteria andUseridGreaterThan(Integer value) {
            addCriterion("userid >", value, "userid");
            return (Criteria) this;
        }

        public Criteria andUseridGreaterThanOrEqualTo(Integer value) {
            addCriterion("userid >=", value, "userid");
            return (Criteria) this;
        }

        public Criteria andUseridLessThan(Integer value) {
            addCriterion("userid <", value, "userid");
            return (Criteria) this;
        }

        public Criteria andUseridLessThanOrEqualTo(Integer value) {
            addCriterion("userid <=", value, "userid");
            return (Criteria) this;
        }

        public Criteria andUseridIn(List<Integer> values) {
            addCriterion("userid in", values, "userid");
            return (Criteria) this;
        }

        public Criteria andUseridNotIn(List<Integer> values) {
            addCriterion("userid not in", values, "userid");
            return (Criteria) this;
        }

        public Criteria andUseridBetween(Integer value1, Integer value2) {
            addCriterion("userid between", value1, value2, "userid");
            return (Criteria) this;
        }

        public Criteria andUseridNotBetween(Integer value1, Integer value2) {
            addCriterion("userid not between", value1, value2, "userid");
            return (Criteria) this;
        }

        public Criteria andUsernameIsNull() {
            addCriterion("username is null");
            return (Criteria) this;
        }

        public Criteria andUsernameIsNotNull() {
            addCriterion("username is not null");
            return (Criteria) this;
        }

        public Criteria andUsernameEqualTo(String value) {
            addCriterion("username =", value, "username");
            return (Criteria) this;
        }

        public Criteria andUsernameNotEqualTo(String value) {
            addCriterion("username <>", value, "username");
            return (Criteria) this;
        }

        public Criteria andUsernameGreaterThan(String value) {
            addCriterion("username >", value, "username");
            return (Criteria) this;
        }

        public Criteria andUsernameGreaterThanOrEqualTo(String value) {
            addCriterion("username >=", value, "username");
            return (Criteria) this;
        }

        public Criteria andUsernameLessThan(String value) {
            addCriterion("username <", value, "username");
            return (Criteria) this;
        }

        public Criteria andUsernameLessThanOrEqualTo(String value) {
            addCriterion("username <=", value, "username");
            return (Criteria) this;
        }

        public Criteria andUsernameLike(String value) {
            addCriterion("username like", value, "username");
            return (Criteria) this;
        }

        public Criteria andUsernameNotLike(String value) {
            addCriterion("username not like", value, "username");
            return (Criteria) this;
        }

        public Criteria andUsernameIn(List<String> values) {
            addCriterion("username in", values, "username");
            return (Criteria) this;
        }

        public Criteria andUsernameNotIn(List<String> values) {
            addCriterion("username not in", values, "username");
            return (Criteria) this;
        }

        public Criteria andUsernameBetween(String value1, String value2) {
            addCriterion("username between", value1, value2, "username");
            return (Criteria) this;
        }

        public Criteria andUsernameNotBetween(String value1, String value2) {
            addCriterion("username not between", value1, value2, "username");
            return (Criteria) this;
        }

        public Criteria andRoleidIsNull() {
            addCriterion("roleid is null");
            return (Criteria) this;
        }

        public Criteria andRoleidIsNotNull() {
            addCriterion("roleid is not null");
            return (Criteria) this;
        }

        public Criteria andRoleidEqualTo(Integer value) {
            addCriterion("roleid =", value, "roleid");
            return (Criteria) this;
        }

        public Criteria andRoleidNotEqualTo(Integer value) {
            addCriterion("roleid <>", value, "roleid");
            return (Criteria) this;
        }

        public Criteria andRoleidGreaterThan(Integer value) {
            addCriterion("roleid >", value, "roleid");
            return (Criteria) this;
        }

        public Criteria andRoleidGreaterThanOrEqualTo(Integer value) {
            addCriterion("roleid >=", value, "roleid");
            return (Criteria) this;
        }

        public Criteria andRoleidLessThan(Integer value) {
            addCriterion("roleid <", value, "roleid");
            return (Criteria) this;
        }

        public Criteria andRoleidLessThanOrEqualTo(Integer value) {
            addCriterion("roleid <=", value, "roleid");
            return (Criteria) this;
        }

        public Criteria andRoleidIn(List<Integer> values) {
            addCriterion("roleid in", values, "roleid");
            return (Criteria) this;
        }

        public Criteria andRoleidNotIn(List<Integer> values) {
            addCriterion("roleid not in", values, "roleid");
            return (Criteria) this;
        }

        public Criteria andRoleidBetween(Integer value1, Integer value2) {
            addCriterion("roleid between", value1, value2, "roleid");
            return (Criteria) this;
        }

        public Criteria andRoleidNotBetween(Integer value1, Integer value2) {
            addCriterion("roleid not between", value1, value2, "roleid");
            return (Criteria) this;
        }

        public Criteria andTimeIsNull() {
            addCriterion("time is null");
            return (Criteria) this;
        }

        public Criteria andTimeIsNotNull() {
            addCriterion("time is not null");
            return (Criteria) this;
        }

        public Criteria andTimeEqualTo(String value) {
            addCriterion("time =", value, "time");
            return (Criteria) this;
        }

        public Criteria andTimeNotEqualTo(String value) {
            addCriterion("time <>", value, "time");
            return (Criteria) this;
        }

        public Criteria andTimeGreaterThan(String value) {
            addCriterion("time >", value, "time");
            return (Criteria) this;
        }

        public Criteria andTimeGreaterThanOrEqualTo(String value) {
            addCriterion("time >=", value, "time");
            return (Criteria) this;
        }

        public Criteria andTimeLessThan(String value) {
            addCriterion("time <", value, "time");
            return (Criteria) this;
        }

        public Criteria andTimeLessThanOrEqualTo(String value) {
            addCriterion("time <=", value, "time");
            return (Criteria) this;
        }

        public Criteria andTimeLike(String value) {
            addCriterion("time like", value, "time");
            return (Criteria) this;
        }

        public Criteria andTimeNotLike(String value) {
            addCriterion("time not like", value, "time");
            return (Criteria) this;
        }

        public Criteria andTimeIn(List<String> values) {
            addCriterion("time in", values, "time");
            return (Criteria) this;
        }

        public Criteria andTimeNotIn(List<String> values) {
            addCriterion("time not in", values, "time");
            return (Criteria) this;
        }

        public Criteria andTimeBetween(String value1, String value2) {
            addCriterion("time between", value1, value2, "time");
            return (Criteria) this;
        }

        public Criteria andTimeNotBetween(String value1, String value2) {
            addCriterion("time not between", value1, value2, "time");
            return (Criteria) this;
        }

        public Criteria andIsworkIsNull() {
            addCriterion("iswork is null");
            return (Criteria) this;
        }

        public Criteria andIsworkIsNotNull() {
            addCriterion("iswork is not null");
            return (Criteria) this;
        }

        public Criteria andIsworkEqualTo(Byte value) {
            addCriterion("iswork =", value, "iswork");
            return (Criteria) this;
        }

        public Criteria andIsworkNotEqualTo(Byte value) {
            addCriterion("iswork <>", value, "iswork");
            return (Criteria) this;
        }

        public Criteria andIsworkGreaterThan(Byte value) {
            addCriterion("iswork >", value, "iswork");
            return (Criteria) this;
        }

        public Criteria andIsworkGreaterThanOrEqualTo(Byte value) {
            addCriterion("iswork >=", value, "iswork");
            return (Criteria) this;
        }

        public Criteria andIsworkLessThan(Byte value) {
            addCriterion("iswork <", value, "iswork");
            return (Criteria) this;
        }

        public Criteria andIsworkLessThanOrEqualTo(Byte value) {
            addCriterion("iswork <=", value, "iswork");
            return (Criteria) this;
        }

        public Criteria andIsworkIn(List<Byte> values) {
            addCriterion("iswork in", values, "iswork");
            return (Criteria) this;
        }

        public Criteria andIsworkNotIn(List<Byte> values) {
            addCriterion("iswork not in", values, "iswork");
            return (Criteria) this;
        }

        public Criteria andIsworkBetween(Byte value1, Byte value2) {
            addCriterion("iswork between", value1, value2, "iswork");
            return (Criteria) this;
        }

        public Criteria andIsworkNotBetween(Byte value1, Byte value2) {
            addCriterion("iswork not between", value1, value2, "iswork");
            return (Criteria) this;
        }

        public Criteria andCountIsNull() {
            addCriterion("count is null");
            return (Criteria) this;
        }

        public Criteria andCountIsNotNull() {
            addCriterion("count is not null");
            return (Criteria) this;
        }

        public Criteria andCountEqualTo(Long value) {
            addCriterion("count =", value, "count");
            return (Criteria) this;
        }

        public Criteria andCountNotEqualTo(Long value) {
            addCriterion("count <>", value, "count");
            return (Criteria) this;
        }

        public Criteria andCountGreaterThan(Long value) {
            addCriterion("count >", value, "count");
            return (Criteria) this;
        }

        public Criteria andCountGreaterThanOrEqualTo(Long value) {
            addCriterion("count >=", value, "count");
            return (Criteria) this;
        }

        public Criteria andCountLessThan(Long value) {
            addCriterion("count <", value, "count");
            return (Criteria) this;
        }

        public Criteria andCountLessThanOrEqualTo(Long value) {
            addCriterion("count <=", value, "count");
            return (Criteria) this;
        }

        public Criteria andCountIn(List<Long> values) {
            addCriterion("count in", values, "count");
            return (Criteria) this;
        }

        public Criteria andCountNotIn(List<Long> values) {
            addCriterion("count not in", values, "count");
            return (Criteria) this;
        }

        public Criteria andCountBetween(Long value1, Long value2) {
            addCriterion("count between", value1, value2, "count");
            return (Criteria) this;
        }

        public Criteria andCountNotBetween(Long value1, Long value2) {
            addCriterion("count not between", value1, value2, "count");
            return (Criteria) this;
        }

        public Criteria andErrorcountIsNull() {
            addCriterion("errorcount is null");
            return (Criteria) this;
        }

        public Criteria andErrorcountIsNotNull() {
            addCriterion("errorcount is not null");
            return (Criteria) this;
        }

        public Criteria andErrorcountEqualTo(Long value) {
            addCriterion("errorcount =", value, "errorcount");
            return (Criteria) this;
        }

        public Criteria andErrorcountNotEqualTo(Long value) {
            addCriterion("errorcount <>", value, "errorcount");
            return (Criteria) this;
        }

        public Criteria andErrorcountGreaterThan(Long value) {
            addCriterion("errorcount >", value, "errorcount");
            return (Criteria) this;
        }

        public Criteria andErrorcountGreaterThanOrEqualTo(Long value) {
            addCriterion("errorcount >=", value, "errorcount");
            return (Criteria) this;
        }

        public Criteria andErrorcountLessThan(Long value) {
            addCriterion("errorcount <", value, "errorcount");
            return (Criteria) this;
        }

        public Criteria andErrorcountLessThanOrEqualTo(Long value) {
            addCriterion("errorcount <=", value, "errorcount");
            return (Criteria) this;
        }

        public Criteria andErrorcountIn(List<Long> values) {
            addCriterion("errorcount in", values, "errorcount");
            return (Criteria) this;
        }

        public Criteria andErrorcountNotIn(List<Long> values) {
            addCriterion("errorcount not in", values, "errorcount");
            return (Criteria) this;
        }

        public Criteria andErrorcountBetween(Long value1, Long value2) {
            addCriterion("errorcount between", value1, value2, "errorcount");
            return (Criteria) this;
        }

        public Criteria andErrorcountNotBetween(Long value1, Long value2) {
            addCriterion("errorcount not between", value1, value2, "errorcount");
            return (Criteria) this;
        }

        public Criteria andVisualerrorcountIsNull() {
            addCriterion("visualerrorcount is null");
            return (Criteria) this;
        }

        public Criteria andVisualerrorcountIsNotNull() {
            addCriterion("visualerrorcount is not null");
            return (Criteria) this;
        }

        public Criteria andVisualerrorcountEqualTo(Long value) {
            addCriterion("visualerrorcount =", value, "visualerrorcount");
            return (Criteria) this;
        }

        public Criteria andVisualerrorcountNotEqualTo(Long value) {
            addCriterion("visualerrorcount <>", value, "visualerrorcount");
            return (Criteria) this;
        }

        public Criteria andVisualerrorcountGreaterThan(Long value) {
            addCriterion("visualerrorcount >", value, "visualerrorcount");
            return (Criteria) this;
        }

        public Criteria andVisualerrorcountGreaterThanOrEqualTo(Long value) {
            addCriterion("visualerrorcount >=", value, "visualerrorcount");
            return (Criteria) this;
        }

        public Criteria andVisualerrorcountLessThan(Long value) {
            addCriterion("visualerrorcount <", value, "visualerrorcount");
            return (Criteria) this;
        }

        public Criteria andVisualerrorcountLessThanOrEqualTo(Long value) {
            addCriterion("visualerrorcount <=", value, "visualerrorcount");
            return (Criteria) this;
        }

        public Criteria andVisualerrorcountIn(List<Long> values) {
            addCriterion("visualerrorcount in", values, "visualerrorcount");
            return (Criteria) this;
        }

        public Criteria andVisualerrorcountNotIn(List<Long> values) {
            addCriterion("visualerrorcount not in", values, "visualerrorcount");
            return (Criteria) this;
        }

        public Criteria andVisualerrorcountBetween(Long value1, Long value2) {
            addCriterion("visualerrorcount between", value1, value2, "visualerrorcount");
            return (Criteria) this;
        }

        public Criteria andVisualerrorcountNotBetween(Long value1, Long value2) {
            addCriterion("visualerrorcount not between", value1, value2, "visualerrorcount");
            return (Criteria) this;
        }

        public Criteria andCreatetimeIsNull() {
            addCriterion("createtime is null");
            return (Criteria) this;
        }

        public Criteria andCreatetimeIsNotNull() {
            addCriterion("createtime is not null");
            return (Criteria) this;
        }

        public Criteria andCreatetimeEqualTo(Date value) {
            addCriterion("createtime =", value, "createtime");
            return (Criteria) this;
        }

        public Criteria andCreatetimeNotEqualTo(Date value) {
            addCriterion("createtime <>", value, "createtime");
            return (Criteria) this;
        }

        public Criteria andCreatetimeGreaterThan(Date value) {
            addCriterion("createtime >", value, "createtime");
            return (Criteria) this;
        }

        public Criteria andCreatetimeGreaterThanOrEqualTo(Date value) {
            addCriterion("createtime >=", value, "createtime");
            return (Criteria) this;
        }

        public Criteria andCreatetimeLessThan(Date value) {
            addCriterion("createtime <", value, "createtime");
            return (Criteria) this;
        }

        public Criteria andCreatetimeLessThanOrEqualTo(Date value) {
            addCriterion("createtime <=", value, "createtime");
            return (Criteria) this;
        }

        public Criteria andCreatetimeIn(List<Date> values) {
            addCriterion("createtime in", values, "createtime");
            return (Criteria) this;
        }

        public Criteria andCreatetimeNotIn(List<Date> values) {
            addCriterion("createtime not in", values, "createtime");
            return (Criteria) this;
        }

        public Criteria andCreatetimeBetween(Date value1, Date value2) {
            addCriterion("createtime between", value1, value2, "createtime");
            return (Criteria) this;
        }

        public Criteria andCreatetimeNotBetween(Date value1, Date value2) {
            addCriterion("createtime not between", value1, value2, "createtime");
            return (Criteria) this;
        }
    }

    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}