package com.emg.projectsmanage.pojo;

import java.util.ArrayList;
import java.util.List;

public class ProjectModelExample {
    protected String orderByClause;
    
    protected Integer limit;
    
    protected Integer offset;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public ProjectModelExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }
    
    public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}
	
	public Integer getOffset() {
		return offset;
	}

	public void setOffset(Integer offset) {
		this.offset = offset;
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

        public Criteria andProtypeIsNull() {
            addCriterion("protype is null");
            return (Criteria) this;
        }

        public Criteria andProtypeIsNotNull() {
            addCriterion("protype is not null");
            return (Criteria) this;
        }

        public Criteria andProtypeEqualTo(Integer value) {
            addCriterion("protype =", value, "protype");
            return (Criteria) this;
        }

        public Criteria andProtypeNotEqualTo(Integer value) {
            addCriterion("protype <>", value, "protype");
            return (Criteria) this;
        }

        public Criteria andProtypeGreaterThan(Integer value) {
            addCriterion("protype >", value, "protype");
            return (Criteria) this;
        }

        public Criteria andProtypeGreaterThanOrEqualTo(Integer value) {
            addCriterion("protype >=", value, "protype");
            return (Criteria) this;
        }

        public Criteria andProtypeLessThan(Integer value) {
            addCriterion("protype <", value, "protype");
            return (Criteria) this;
        }

        public Criteria andProtypeLessThanOrEqualTo(Integer value) {
            addCriterion("protype <=", value, "protype");
            return (Criteria) this;
        }

        public Criteria andProtypeIn(List<Integer> values) {
            addCriterion("protype in", values, "protype");
            return (Criteria) this;
        }

        public Criteria andProtypeNotIn(List<Integer> values) {
            addCriterion("protype not in", values, "protype");
            return (Criteria) this;
        }

        public Criteria andProtypeBetween(Integer value1, Integer value2) {
            addCriterion("protype between", value1, value2, "protype");
            return (Criteria) this;
        }

        public Criteria andProtypeNotBetween(Integer value1, Integer value2) {
            addCriterion("protype not between", value1, value2, "protype");
            return (Criteria) this;
        }

        public Criteria andPdifficultyIsNull() {
            addCriterion("pdifficulty is null");
            return (Criteria) this;
        }

        public Criteria andPdifficultyIsNotNull() {
            addCriterion("pdifficulty is not null");
            return (Criteria) this;
        }

        public Criteria andPdifficultyEqualTo(Integer value) {
            addCriterion("pdifficulty =", value, "pdifficulty");
            return (Criteria) this;
        }

        public Criteria andPdifficultyNotEqualTo(Integer value) {
            addCriterion("pdifficulty <>", value, "pdifficulty");
            return (Criteria) this;
        }

        public Criteria andPdifficultyGreaterThan(Integer value) {
            addCriterion("pdifficulty >", value, "pdifficulty");
            return (Criteria) this;
        }

        public Criteria andPdifficultyGreaterThanOrEqualTo(Integer value) {
            addCriterion("pdifficulty >=", value, "pdifficulty");
            return (Criteria) this;
        }

        public Criteria andPdifficultyLessThan(Integer value) {
            addCriterion("pdifficulty <", value, "pdifficulty");
            return (Criteria) this;
        }

        public Criteria andPdifficultyLessThanOrEqualTo(Integer value) {
            addCriterion("pdifficulty <=", value, "pdifficulty");
            return (Criteria) this;
        }

        public Criteria andPdifficultyIn(List<Integer> values) {
            addCriterion("pdifficulty in", values, "pdifficulty");
            return (Criteria) this;
        }

        public Criteria andPdifficultyNotIn(List<Integer> values) {
            addCriterion("pdifficulty not in", values, "pdifficulty");
            return (Criteria) this;
        }

        public Criteria andPdifficultyBetween(Integer value1, Integer value2) {
            addCriterion("pdifficulty between", value1, value2, "pdifficulty");
            return (Criteria) this;
        }

        public Criteria andPdifficultyNotBetween(Integer value1, Integer value2) {
            addCriterion("pdifficulty not between", value1, value2, "pdifficulty");
            return (Criteria) this;
        }

        public Criteria andPriorityIsNull() {
            addCriterion("priority is null");
            return (Criteria) this;
        }

        public Criteria andPriorityIsNotNull() {
            addCriterion("priority is not null");
            return (Criteria) this;
        }

        public Criteria andPriorityEqualTo(Integer value) {
            addCriterion("priority =", value, "priority");
            return (Criteria) this;
        }

        public Criteria andPriorityNotEqualTo(Integer value) {
            addCriterion("priority <>", value, "priority");
            return (Criteria) this;
        }

        public Criteria andPriorityGreaterThan(Integer value) {
            addCriterion("priority >", value, "priority");
            return (Criteria) this;
        }

        public Criteria andPriorityGreaterThanOrEqualTo(Integer value) {
            addCriterion("priority >=", value, "priority");
            return (Criteria) this;
        }

        public Criteria andPriorityLessThan(Integer value) {
            addCriterion("priority <", value, "priority");
            return (Criteria) this;
        }

        public Criteria andPriorityLessThanOrEqualTo(Integer value) {
            addCriterion("priority <=", value, "priority");
            return (Criteria) this;
        }

        public Criteria andPriorityIn(List<Integer> values) {
            addCriterion("priority in", values, "priority");
            return (Criteria) this;
        }

        public Criteria andPriorityNotIn(List<Integer> values) {
            addCriterion("priority not in", values, "priority");
            return (Criteria) this;
        }

        public Criteria andPriorityBetween(Integer value1, Integer value2) {
            addCriterion("priority between", value1, value2, "priority");
            return (Criteria) this;
        }

        public Criteria andPriorityNotBetween(Integer value1, Integer value2) {
            addCriterion("priority not between", value1, value2, "priority");
            return (Criteria) this;
        }

        public Criteria andTasknumIsNull() {
            addCriterion("tasknum is null");
            return (Criteria) this;
        }

        public Criteria andTasknumIsNotNull() {
            addCriterion("tasknum is not null");
            return (Criteria) this;
        }

        public Criteria andTasknumEqualTo(Integer value) {
            addCriterion("tasknum =", value, "tasknum");
            return (Criteria) this;
        }

        public Criteria andTasknumNotEqualTo(Integer value) {
            addCriterion("tasknum <>", value, "tasknum");
            return (Criteria) this;
        }

        public Criteria andTasknumGreaterThan(Integer value) {
            addCriterion("tasknum >", value, "tasknum");
            return (Criteria) this;
        }

        public Criteria andTasknumGreaterThanOrEqualTo(Integer value) {
            addCriterion("tasknum >=", value, "tasknum");
            return (Criteria) this;
        }

        public Criteria andTasknumLessThan(Integer value) {
            addCriterion("tasknum <", value, "tasknum");
            return (Criteria) this;
        }

        public Criteria andTasknumLessThanOrEqualTo(Integer value) {
            addCriterion("tasknum <=", value, "tasknum");
            return (Criteria) this;
        }

        public Criteria andTasknumIn(List<Integer> values) {
            addCriterion("tasknum in", values, "tasknum");
            return (Criteria) this;
        }

        public Criteria andTasknumNotIn(List<Integer> values) {
            addCriterion("tasknum not in", values, "tasknum");
            return (Criteria) this;
        }

        public Criteria andTasknumBetween(Integer value1, Integer value2) {
            addCriterion("tasknum between", value1, value2, "tasknum");
            return (Criteria) this;
        }

        public Criteria andTasknumNotBetween(Integer value1, Integer value2) {
            addCriterion("tasknum not between", value1, value2, "tasknum");
            return (Criteria) this;
        }

        public Criteria andSystemidIsNull() {
            addCriterion("systemid is null");
            return (Criteria) this;
        }

        public Criteria andSystemidIsNotNull() {
            addCriterion("systemid is not null");
            return (Criteria) this;
        }

        public Criteria andSystemidEqualTo(Integer value) {
            addCriterion("systemid =", value, "systemid");
            return (Criteria) this;
        }

        public Criteria andSystemidNotEqualTo(Integer value) {
            addCriterion("systemid <>", value, "systemid");
            return (Criteria) this;
        }

        public Criteria andSystemidGreaterThan(Integer value) {
            addCriterion("systemid >", value, "systemid");
            return (Criteria) this;
        }

        public Criteria andSystemidGreaterThanOrEqualTo(Integer value) {
            addCriterion("systemid >=", value, "systemid");
            return (Criteria) this;
        }

        public Criteria andSystemidLessThan(Integer value) {
            addCriterion("systemid <", value, "systemid");
            return (Criteria) this;
        }

        public Criteria andSystemidLessThanOrEqualTo(Integer value) {
            addCriterion("systemid <=", value, "systemid");
            return (Criteria) this;
        }

        public Criteria andSystemidIn(List<Integer> values) {
            addCriterion("systemid in", values, "systemid");
            return (Criteria) this;
        }

        public Criteria andSystemidNotIn(List<Integer> values) {
            addCriterion("systemid not in", values, "systemid");
            return (Criteria) this;
        }

        public Criteria andSystemidBetween(Integer value1, Integer value2) {
            addCriterion("systemid between", value1, value2, "systemid");
            return (Criteria) this;
        }

        public Criteria andSystemidNotBetween(Integer value1, Integer value2) {
            addCriterion("systemid not between", value1, value2, "systemid");
            return (Criteria) this;
        }

        public Criteria andDescriptionIsNull() {
            addCriterion("description is null");
            return (Criteria) this;
        }

        public Criteria andDescriptionIsNotNull() {
            addCriterion("description is not null");
            return (Criteria) this;
        }

        public Criteria andDescriptionEqualTo(String value) {
            addCriterion("description =", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionNotEqualTo(String value) {
            addCriterion("description <>", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionGreaterThan(String value) {
            addCriterion("description >", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionGreaterThanOrEqualTo(String value) {
            addCriterion("description >=", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionLessThan(String value) {
            addCriterion("description <", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionLessThanOrEqualTo(String value) {
            addCriterion("description <=", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionLike(String value) {
            addCriterion("description like", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionNotLike(String value) {
            addCriterion("description not like", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionIn(List<String> values) {
            addCriterion("description in", values, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionNotIn(List<String> values) {
            addCriterion("description not in", values, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionBetween(String value1, String value2) {
            addCriterion("description between", value1, value2, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionNotBetween(String value1, String value2) {
            addCriterion("description not between", value1, value2, "description");
            return (Criteria) this;
        }

        public Criteria andCreatebyIsNull() {
            addCriterion("createby is null");
            return (Criteria) this;
        }

        public Criteria andCreatebyIsNotNull() {
            addCriterion("createby is not null");
            return (Criteria) this;
        }

        public Criteria andCreatebyEqualTo(Integer value) {
            addCriterion("createby =", value, "createby");
            return (Criteria) this;
        }

        public Criteria andCreatebyNotEqualTo(Integer value) {
            addCriterion("createby <>", value, "createby");
            return (Criteria) this;
        }

        public Criteria andCreatebyGreaterThan(Integer value) {
            addCriterion("createby >", value, "createby");
            return (Criteria) this;
        }

        public Criteria andCreatebyGreaterThanOrEqualTo(Integer value) {
            addCriterion("createby >=", value, "createby");
            return (Criteria) this;
        }

        public Criteria andCreatebyLessThan(Integer value) {
            addCriterion("createby <", value, "createby");
            return (Criteria) this;
        }

        public Criteria andCreatebyLessThanOrEqualTo(Integer value) {
            addCriterion("createby <=", value, "createby");
            return (Criteria) this;
        }

        public Criteria andCreatebyIn(List<Integer> values) {
            addCriterion("createby in", values, "createby");
            return (Criteria) this;
        }

        public Criteria andCreatebyNotIn(List<Integer> values) {
            addCriterion("createby not in", values, "createby");
            return (Criteria) this;
        }

        public Criteria andCreatebyBetween(Integer value1, Integer value2) {
            addCriterion("createby between", value1, value2, "createby");
            return (Criteria) this;
        }

        public Criteria andCreatebyNotBetween(Integer value1, Integer value2) {
            addCriterion("createby not between", value1, value2, "createby");
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

        public Criteria andCreatetimeEqualTo(String value) {
            addCriterion("createtime =", value, "createtime");
            return (Criteria) this;
        }

        public Criteria andCreatetimeNotEqualTo(String value) {
            addCriterion("createtime <>", value, "createtime");
            return (Criteria) this;
        }

        public Criteria andCreatetimeGreaterThan(String value) {
            addCriterion("createtime >", value, "createtime");
            return (Criteria) this;
        }

        public Criteria andCreatetimeGreaterThanOrEqualTo(String value) {
            addCriterion("createtime >=", value, "createtime");
            return (Criteria) this;
        }

        public Criteria andCreatetimeLessThan(String value) {
            addCriterion("createtime <", value, "createtime");
            return (Criteria) this;
        }

        public Criteria andCreatetimeLessThanOrEqualTo(String value) {
            addCriterion("createtime <=", value, "createtime");
            return (Criteria) this;
        }

        public Criteria andCreatetimeIn(List<String> values) {
            addCriterion("createtime in", values, "createtime");
            return (Criteria) this;
        }

        public Criteria andCreatetimeNotIn(List<String> values) {
            addCriterion("createtime not in", values, "createtime");
            return (Criteria) this;
        }

        public Criteria andCreatetimeBetween(String value1, String value2) {
            addCriterion("createtime between", value1, value2, "createtime");
            return (Criteria) this;
        }

        public Criteria andCreatetimeNotBetween(String value1, String value2) {
            addCriterion("createtime not between", value1, value2, "createtime");
            return (Criteria) this;
        }

        public Criteria andAreaIsNull() {
            addCriterion("area is null");
            return (Criteria) this;
        }

        public Criteria andAreaIsNotNull() {
            addCriterion("area is not null");
            return (Criteria) this;
        }

        public Criteria andAreaEqualTo(String value) {
            addCriterion("area =", value, "area");
            return (Criteria) this;
        }

        public Criteria andAreaNotEqualTo(String value) {
            addCriterion("area <>", value, "area");
            return (Criteria) this;
        }

        public Criteria andAreaGreaterThan(String value) {
            addCriterion("area >", value, "area");
            return (Criteria) this;
        }

        public Criteria andAreaGreaterThanOrEqualTo(String value) {
            addCriterion("area >=", value, "area");
            return (Criteria) this;
        }

        public Criteria andAreaLessThan(String value) {
            addCriterion("area <", value, "area");
            return (Criteria) this;
        }

        public Criteria andAreaLessThanOrEqualTo(String value) {
            addCriterion("area <=", value, "area");
            return (Criteria) this;
        }

        public Criteria andAreaLike(String value) {
            addCriterion("area like", value, "area");
            return (Criteria) this;
        }

        public Criteria andAreaNotLike(String value) {
            addCriterion("area not like", value, "area");
            return (Criteria) this;
        }

        public Criteria andAreaIn(List<String> values) {
            addCriterion("area in", values, "area");
            return (Criteria) this;
        }

        public Criteria andAreaNotIn(List<String> values) {
            addCriterion("area not in", values, "area");
            return (Criteria) this;
        }

        public Criteria andAreaBetween(String value1, String value2) {
            addCriterion("area between", value1, value2, "area");
            return (Criteria) this;
        }

        public Criteria andAreaNotBetween(String value1, String value2) {
            addCriterion("area not between", value1, value2, "area");
            return (Criteria) this;
        }

        public Criteria andNameIsNull() {
            addCriterion("name is null");
            return (Criteria) this;
        }

        public Criteria andNameIsNotNull() {
            addCriterion("name is not null");
            return (Criteria) this;
        }

        public Criteria andNameEqualTo(String value) {
            addCriterion("name =", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameNotEqualTo(String value) {
            addCriterion("name <>", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameGreaterThan(String value) {
            addCriterion("name >", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameGreaterThanOrEqualTo(String value) {
            addCriterion("name >=", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameLessThan(String value) {
            addCriterion("name <", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameLessThanOrEqualTo(String value) {
            addCriterion("name <=", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameLike(String value) {
            addCriterion("name like", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameNotLike(String value) {
            addCriterion("name not like", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameIn(List<String> values) {
            addCriterion("name in", values, "name");
            return (Criteria) this;
        }

        public Criteria andNameNotIn(List<String> values) {
            addCriterion("name not in", values, "name");
            return (Criteria) this;
        }

        public Criteria andNameBetween(String value1, String value2) {
            addCriterion("name between", value1, value2, "name");
            return (Criteria) this;
        }

        public Criteria andNameNotBetween(String value1, String value2) {
            addCriterion("name not between", value1, value2, "name");
            return (Criteria) this;
        }

        public Criteria andOwnerIsNull() {
            addCriterion("owner is null");
            return (Criteria) this;
        }

        public Criteria andOwnerIsNotNull() {
            addCriterion("owner is not null");
            return (Criteria) this;
        }

        public Criteria andOwnerEqualTo(Integer value) {
            addCriterion("owner =", value, "owner");
            return (Criteria) this;
        }

        public Criteria andOwnerNotEqualTo(Integer value) {
            addCriterion("owner <>", value, "owner");
            return (Criteria) this;
        }

        public Criteria andOwnerGreaterThan(Integer value) {
            addCriterion("owner >", value, "owner");
            return (Criteria) this;
        }

        public Criteria andOwnerGreaterThanOrEqualTo(Integer value) {
            addCriterion("owner >=", value, "owner");
            return (Criteria) this;
        }

        public Criteria andOwnerLessThan(Integer value) {
            addCriterion("owner <", value, "owner");
            return (Criteria) this;
        }

        public Criteria andOwnerLessThanOrEqualTo(Integer value) {
            addCriterion("owner <=", value, "owner");
            return (Criteria) this;
        }

        public Criteria andOwnerIn(List<Integer> values) {
            addCriterion("owner in", values, "owner");
            return (Criteria) this;
        }

        public Criteria andOwnerNotIn(List<Integer> values) {
            addCriterion("owner not in", values, "owner");
            return (Criteria) this;
        }

        public Criteria andOwnerBetween(Integer value1, Integer value2) {
            addCriterion("owner between", value1, value2, "owner");
            return (Criteria) this;
        }

        public Criteria andOwnerNotBetween(Integer value1, Integer value2) {
            addCriterion("owner not between", value1, value2, "owner");
            return (Criteria) this;
        }

        public Criteria andOverprogressIsNull() {
            addCriterion("overprogress is null");
            return (Criteria) this;
        }

        public Criteria andOverprogressIsNotNull() {
            addCriterion("overprogress is not null");
            return (Criteria) this;
        }

        public Criteria andOverprogressEqualTo(String value) {
            addCriterion("overprogress =", value, "overprogress");
            return (Criteria) this;
        }

        public Criteria andOverprogressNotEqualTo(String value) {
            addCriterion("overprogress <>", value, "overprogress");
            return (Criteria) this;
        }

        public Criteria andOverprogressGreaterThan(String value) {
            addCriterion("overprogress >", value, "overprogress");
            return (Criteria) this;
        }

        public Criteria andOverprogressGreaterThanOrEqualTo(String value) {
            addCriterion("overprogress >=", value, "overprogress");
            return (Criteria) this;
        }

        public Criteria andOverprogressLessThan(String value) {
            addCriterion("overprogress <", value, "overprogress");
            return (Criteria) this;
        }

        public Criteria andOverprogressLessThanOrEqualTo(String value) {
            addCriterion("overprogress <=", value, "overprogress");
            return (Criteria) this;
        }

        public Criteria andOverprogressLike(String value) {
            addCriterion("overprogress like", value, "overprogress");
            return (Criteria) this;
        }

        public Criteria andOverprogressNotLike(String value) {
            addCriterion("overprogress not like", value, "overprogress");
            return (Criteria) this;
        }

        public Criteria andOverprogressIn(List<String> values) {
            addCriterion("overprogress in", values, "overprogress");
            return (Criteria) this;
        }

        public Criteria andOverprogressNotIn(List<String> values) {
            addCriterion("overprogress not in", values, "overprogress");
            return (Criteria) this;
        }

        public Criteria andOverprogressBetween(String value1, String value2) {
            addCriterion("overprogress between", value1, value2, "overprogress");
            return (Criteria) this;
        }

        public Criteria andOverprogressNotBetween(String value1, String value2) {
            addCriterion("overprogress not between", value1, value2, "overprogress");
            return (Criteria) this;
        }

        public Criteria andOverstateIsNull() {
            addCriterion("overstate is null");
            return (Criteria) this;
        }

        public Criteria andOverstateIsNotNull() {
            addCriterion("overstate is not null");
            return (Criteria) this;
        }

        public Criteria andOverstateEqualTo(Integer value) {
            addCriterion("overstate =", value, "overstate");
            return (Criteria) this;
        }

        public Criteria andOverstateNotEqualTo(Integer value) {
            addCriterion("overstate <>", value, "overstate");
            return (Criteria) this;
        }

        public Criteria andOverstateGreaterThan(Integer value) {
            addCriterion("overstate >", value, "overstate");
            return (Criteria) this;
        }

        public Criteria andOverstateGreaterThanOrEqualTo(Integer value) {
            addCriterion("overstate >=", value, "overstate");
            return (Criteria) this;
        }

        public Criteria andOverstateLessThan(Integer value) {
            addCriterion("overstate <", value, "overstate");
            return (Criteria) this;
        }

        public Criteria andOverstateLessThanOrEqualTo(Integer value) {
            addCriterion("overstate <=", value, "overstate");
            return (Criteria) this;
        }

        public Criteria andOverstateIn(List<Integer> values) {
            addCriterion("overstate in", values, "overstate");
            return (Criteria) this;
        }

        public Criteria andOverstateNotIn(List<Integer> values) {
            addCriterion("overstate not in", values, "overstate");
            return (Criteria) this;
        }

        public Criteria andOverstateBetween(Integer value1, Integer value2) {
            addCriterion("overstate between", value1, value2, "overstate");
            return (Criteria) this;
        }

        public Criteria andOverstateNotBetween(Integer value1, Integer value2) {
            addCriterion("overstate not between", value1, value2, "overstate");
            return (Criteria) this;
        }

        public Criteria andPoitotckdislIsNull() {
            addCriterion("poitotckdisl is null");
            return (Criteria) this;
        }

        public Criteria andPoitotckdislIsNotNull() {
            addCriterion("poitotckdisl is not null");
            return (Criteria) this;
        }

        public Criteria andPoitotckdislEqualTo(Double value) {
            addCriterion("poitotckdisl =", value, "poitotckdisl");
            return (Criteria) this;
        }

        public Criteria andPoitotckdislNotEqualTo(Double value) {
            addCriterion("poitotckdisl <>", value, "poitotckdisl");
            return (Criteria) this;
        }

        public Criteria andPoitotckdislGreaterThan(Double value) {
            addCriterion("poitotckdisl >", value, "poitotckdisl");
            return (Criteria) this;
        }

        public Criteria andPoitotckdislGreaterThanOrEqualTo(Double value) {
            addCriterion("poitotckdisl >=", value, "poitotckdisl");
            return (Criteria) this;
        }

        public Criteria andPoitotckdislLessThan(Double value) {
            addCriterion("poitotckdisl <", value, "poitotckdisl");
            return (Criteria) this;
        }

        public Criteria andPoitotckdislLessThanOrEqualTo(Double value) {
            addCriterion("poitotckdisl <=", value, "poitotckdisl");
            return (Criteria) this;
        }

        public Criteria andPoitotckdislIn(List<Double> values) {
            addCriterion("poitotckdisl in", values, "poitotckdisl");
            return (Criteria) this;
        }

        public Criteria andPoitotckdislNotIn(List<Double> values) {
            addCriterion("poitotckdisl not in", values, "poitotckdisl");
            return (Criteria) this;
        }

        public Criteria andPoitotckdislBetween(Double value1, Double value2) {
            addCriterion("poitotckdisl between", value1, value2, "poitotckdisl");
            return (Criteria) this;
        }

        public Criteria andPoitotckdislNotBetween(Double value1, Double value2) {
            addCriterion("poitotckdisl not between", value1, value2, "poitotckdisl");
            return (Criteria) this;
        }

        public Criteria andPoitotckdisrIsNull() {
            addCriterion("poitotckdisr is null");
            return (Criteria) this;
        }

        public Criteria andPoitotckdisrIsNotNull() {
            addCriterion("poitotckdisr is not null");
            return (Criteria) this;
        }

        public Criteria andPoitotckdisrEqualTo(Double value) {
            addCriterion("poitotckdisr =", value, "poitotckdisr");
            return (Criteria) this;
        }

        public Criteria andPoitotckdisrNotEqualTo(Double value) {
            addCriterion("poitotckdisr <>", value, "poitotckdisr");
            return (Criteria) this;
        }

        public Criteria andPoitotckdisrGreaterThan(Double value) {
            addCriterion("poitotckdisr >", value, "poitotckdisr");
            return (Criteria) this;
        }

        public Criteria andPoitotckdisrGreaterThanOrEqualTo(Double value) {
            addCriterion("poitotckdisr >=", value, "poitotckdisr");
            return (Criteria) this;
        }

        public Criteria andPoitotckdisrLessThan(Double value) {
            addCriterion("poitotckdisr <", value, "poitotckdisr");
            return (Criteria) this;
        }

        public Criteria andPoitotckdisrLessThanOrEqualTo(Double value) {
            addCriterion("poitotckdisr <=", value, "poitotckdisr");
            return (Criteria) this;
        }

        public Criteria andPoitotckdisrIn(List<Double> values) {
            addCriterion("poitotckdisr in", values, "poitotckdisr");
            return (Criteria) this;
        }

        public Criteria andPoitotckdisrNotIn(List<Double> values) {
            addCriterion("poitotckdisr not in", values, "poitotckdisr");
            return (Criteria) this;
        }

        public Criteria andPoitotckdisrBetween(Double value1, Double value2) {
            addCriterion("poitotckdisr between", value1, value2, "poitotckdisr");
            return (Criteria) this;
        }

        public Criteria andPoitotckdisrNotBetween(Double value1, Double value2) {
            addCriterion("poitotckdisr not between", value1, value2, "poitotckdisr");
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