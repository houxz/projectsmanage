package com.emg.projectsmanage.pojo.edittask;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TaskModelExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public TaskModelExample() {
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

        public Criteria andBlockidIsNull() {
            addCriterion("blockid is null");
            return (Criteria) this;
        }

        public Criteria andBlockidIsNotNull() {
            addCriterion("blockid is not null");
            return (Criteria) this;
        }

        public Criteria andBlockidEqualTo(Long value) {
            addCriterion("blockid =", value, "blockid");
            return (Criteria) this;
        }

        public Criteria andBlockidNotEqualTo(Long value) {
            addCriterion("blockid <>", value, "blockid");
            return (Criteria) this;
        }

        public Criteria andBlockidGreaterThan(Long value) {
            addCriterion("blockid >", value, "blockid");
            return (Criteria) this;
        }

        public Criteria andBlockidGreaterThanOrEqualTo(Long value) {
            addCriterion("blockid >=", value, "blockid");
            return (Criteria) this;
        }

        public Criteria andBlockidLessThan(Long value) {
            addCriterion("blockid <", value, "blockid");
            return (Criteria) this;
        }

        public Criteria andBlockidLessThanOrEqualTo(Long value) {
            addCriterion("blockid <=", value, "blockid");
            return (Criteria) this;
        }

        public Criteria andBlockidIn(List<Long> values) {
            addCriterion("blockid in", values, "blockid");
            return (Criteria) this;
        }

        public Criteria andBlockidNotIn(List<Long> values) {
            addCriterion("blockid not in", values, "blockid");
            return (Criteria) this;
        }

        public Criteria andBlockidBetween(Long value1, Long value2) {
            addCriterion("blockid between", value1, value2, "blockid");
            return (Criteria) this;
        }

        public Criteria andBlockidNotBetween(Long value1, Long value2) {
            addCriterion("blockid not between", value1, value2, "blockid");
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

        public Criteria andFlowtypeIsNull() {
            addCriterion("flowtype is null");
            return (Criteria) this;
        }

        public Criteria andFlowtypeIsNotNull() {
            addCriterion("flowtype is not null");
            return (Criteria) this;
        }

        public Criteria andFlowtypeEqualTo(Integer value) {
            addCriterion("flowtype =", value, "flowtype");
            return (Criteria) this;
        }

        public Criteria andFlowtypeNotEqualTo(Integer value) {
            addCriterion("flowtype <>", value, "flowtype");
            return (Criteria) this;
        }

        public Criteria andFlowtypeGreaterThan(Integer value) {
            addCriterion("flowtype >", value, "flowtype");
            return (Criteria) this;
        }

        public Criteria andFlowtypeGreaterThanOrEqualTo(Integer value) {
            addCriterion("flowtype >=", value, "flowtype");
            return (Criteria) this;
        }

        public Criteria andFlowtypeLessThan(Integer value) {
            addCriterion("flowtype <", value, "flowtype");
            return (Criteria) this;
        }

        public Criteria andFlowtypeLessThanOrEqualTo(Integer value) {
            addCriterion("flowtype <=", value, "flowtype");
            return (Criteria) this;
        }

        public Criteria andFlowtypeIn(List<Integer> values) {
            addCriterion("flowtype in", values, "flowtype");
            return (Criteria) this;
        }

        public Criteria andFlowtypeNotIn(List<Integer> values) {
            addCriterion("flowtype not in", values, "flowtype");
            return (Criteria) this;
        }

        public Criteria andFlowtypeBetween(Integer value1, Integer value2) {
            addCriterion("flowtype between", value1, value2, "flowtype");
            return (Criteria) this;
        }

        public Criteria andFlowtypeNotBetween(Integer value1, Integer value2) {
            addCriterion("flowtype not between", value1, value2, "flowtype");
            return (Criteria) this;
        }

        public Criteria andStateIsNull() {
            addCriterion("state is null");
            return (Criteria) this;
        }

        public Criteria andStateIsNotNull() {
            addCriterion("state is not null");
            return (Criteria) this;
        }

        public Criteria andStateEqualTo(Integer value) {
            addCriterion("state =", value, "state");
            return (Criteria) this;
        }

        public Criteria andStateNotEqualTo(Integer value) {
            addCriterion("state <>", value, "state");
            return (Criteria) this;
        }

        public Criteria andStateGreaterThan(Integer value) {
            addCriterion("state >", value, "state");
            return (Criteria) this;
        }

        public Criteria andStateGreaterThanOrEqualTo(Integer value) {
            addCriterion("state >=", value, "state");
            return (Criteria) this;
        }

        public Criteria andStateLessThan(Integer value) {
            addCriterion("state <", value, "state");
            return (Criteria) this;
        }

        public Criteria andStateLessThanOrEqualTo(Integer value) {
            addCriterion("state <=", value, "state");
            return (Criteria) this;
        }

        public Criteria andStateIn(List<Integer> values) {
            addCriterion("state in", values, "state");
            return (Criteria) this;
        }

        public Criteria andStateNotIn(List<Integer> values) {
            addCriterion("state not in", values, "state");
            return (Criteria) this;
        }

        public Criteria andStateBetween(Integer value1, Integer value2) {
            addCriterion("state between", value1, value2, "state");
            return (Criteria) this;
        }

        public Criteria andStateNotBetween(Integer value1, Integer value2) {
            addCriterion("state not between", value1, value2, "state");
            return (Criteria) this;
        }

        public Criteria andProcessIsNull() {
            addCriterion("process is null");
            return (Criteria) this;
        }

        public Criteria andProcessIsNotNull() {
            addCriterion("process is not null");
            return (Criteria) this;
        }

        public Criteria andProcessEqualTo(Integer value) {
            addCriterion("process =", value, "process");
            return (Criteria) this;
        }

        public Criteria andProcessNotEqualTo(Integer value) {
            addCriterion("process <>", value, "process");
            return (Criteria) this;
        }

        public Criteria andProcessGreaterThan(Integer value) {
            addCriterion("process >", value, "process");
            return (Criteria) this;
        }

        public Criteria andProcessGreaterThanOrEqualTo(Integer value) {
            addCriterion("process >=", value, "process");
            return (Criteria) this;
        }

        public Criteria andProcessLessThan(Integer value) {
            addCriterion("process <", value, "process");
            return (Criteria) this;
        }

        public Criteria andProcessLessThanOrEqualTo(Integer value) {
            addCriterion("process <=", value, "process");
            return (Criteria) this;
        }

        public Criteria andProcessIn(List<Integer> values) {
            addCriterion("process in", values, "process");
            return (Criteria) this;
        }

        public Criteria andProcessNotIn(List<Integer> values) {
            addCriterion("process not in", values, "process");
            return (Criteria) this;
        }

        public Criteria andProcessBetween(Integer value1, Integer value2) {
            addCriterion("process between", value1, value2, "process");
            return (Criteria) this;
        }

        public Criteria andProcessNotBetween(Integer value1, Integer value2) {
            addCriterion("process not between", value1, value2, "process");
            return (Criteria) this;
        }

        public Criteria andEditidIsNull() {
            addCriterion("editid is null");
            return (Criteria) this;
        }

        public Criteria andEditidIsNotNull() {
            addCriterion("editid is not null");
            return (Criteria) this;
        }

        public Criteria andEditidEqualTo(Integer value) {
            addCriterion("editid =", value, "editid");
            return (Criteria) this;
        }

        public Criteria andEditidNotEqualTo(Integer value) {
            addCriterion("editid <>", value, "editid");
            return (Criteria) this;
        }

        public Criteria andEditidGreaterThan(Integer value) {
            addCriterion("editid >", value, "editid");
            return (Criteria) this;
        }

        public Criteria andEditidGreaterThanOrEqualTo(Integer value) {
            addCriterion("editid >=", value, "editid");
            return (Criteria) this;
        }

        public Criteria andEditidLessThan(Integer value) {
            addCriterion("editid <", value, "editid");
            return (Criteria) this;
        }

        public Criteria andEditidLessThanOrEqualTo(Integer value) {
            addCriterion("editid <=", value, "editid");
            return (Criteria) this;
        }

        public Criteria andEditidIn(List<Integer> values) {
            addCriterion("editid in", values, "editid");
            return (Criteria) this;
        }

        public Criteria andEditidNotIn(List<Integer> values) {
            addCriterion("editid not in", values, "editid");
            return (Criteria) this;
        }

        public Criteria andEditidBetween(Integer value1, Integer value2) {
            addCriterion("editid between", value1, value2, "editid");
            return (Criteria) this;
        }

        public Criteria andEditidNotBetween(Integer value1, Integer value2) {
            addCriterion("editid not between", value1, value2, "editid");
            return (Criteria) this;
        }

        public Criteria andCheckidIsNull() {
            addCriterion("checkid is null");
            return (Criteria) this;
        }

        public Criteria andCheckidIsNotNull() {
            addCriterion("checkid is not null");
            return (Criteria) this;
        }

        public Criteria andCheckidEqualTo(Integer value) {
            addCriterion("checkid =", value, "checkid");
            return (Criteria) this;
        }

        public Criteria andCheckidNotEqualTo(Integer value) {
            addCriterion("checkid <>", value, "checkid");
            return (Criteria) this;
        }

        public Criteria andCheckidGreaterThan(Integer value) {
            addCriterion("checkid >", value, "checkid");
            return (Criteria) this;
        }

        public Criteria andCheckidGreaterThanOrEqualTo(Integer value) {
            addCriterion("checkid >=", value, "checkid");
            return (Criteria) this;
        }

        public Criteria andCheckidLessThan(Integer value) {
            addCriterion("checkid <", value, "checkid");
            return (Criteria) this;
        }

        public Criteria andCheckidLessThanOrEqualTo(Integer value) {
            addCriterion("checkid <=", value, "checkid");
            return (Criteria) this;
        }

        public Criteria andCheckidIn(List<Integer> values) {
            addCriterion("checkid in", values, "checkid");
            return (Criteria) this;
        }

        public Criteria andCheckidNotIn(List<Integer> values) {
            addCriterion("checkid not in", values, "checkid");
            return (Criteria) this;
        }

        public Criteria andCheckidBetween(Integer value1, Integer value2) {
            addCriterion("checkid between", value1, value2, "checkid");
            return (Criteria) this;
        }

        public Criteria andCheckidNotBetween(Integer value1, Integer value2) {
            addCriterion("checkid not between", value1, value2, "checkid");
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

        public Criteria andReferdataIsNull() {
            addCriterion("referdata is null");
            return (Criteria) this;
        }

        public Criteria andReferdataIsNotNull() {
            addCriterion("referdata is not null");
            return (Criteria) this;
        }

        public Criteria andReferdataEqualTo(String value) {
            addCriterion("referdata =", value, "referdata");
            return (Criteria) this;
        }

        public Criteria andReferdataNotEqualTo(String value) {
            addCriterion("referdata <>", value, "referdata");
            return (Criteria) this;
        }

        public Criteria andReferdataGreaterThan(String value) {
            addCriterion("referdata >", value, "referdata");
            return (Criteria) this;
        }

        public Criteria andReferdataGreaterThanOrEqualTo(String value) {
            addCriterion("referdata >=", value, "referdata");
            return (Criteria) this;
        }

        public Criteria andReferdataLessThan(String value) {
            addCriterion("referdata <", value, "referdata");
            return (Criteria) this;
        }

        public Criteria andReferdataLessThanOrEqualTo(String value) {
            addCriterion("referdata <=", value, "referdata");
            return (Criteria) this;
        }

        public Criteria andReferdataLike(String value) {
            addCriterion("referdata like", value, "referdata");
            return (Criteria) this;
        }

        public Criteria andReferdataNotLike(String value) {
            addCriterion("referdata not like", value, "referdata");
            return (Criteria) this;
        }

        public Criteria andReferdataIn(List<String> values) {
            addCriterion("referdata in", values, "referdata");
            return (Criteria) this;
        }

        public Criteria andReferdataNotIn(List<String> values) {
            addCriterion("referdata not in", values, "referdata");
            return (Criteria) this;
        }

        public Criteria andReferdataBetween(String value1, String value2) {
            addCriterion("referdata between", value1, value2, "referdata");
            return (Criteria) this;
        }

        public Criteria andReferdataNotBetween(String value1, String value2) {
            addCriterion("referdata not between", value1, value2, "referdata");
            return (Criteria) this;
        }

        public Criteria andBatchidIsNull() {
            addCriterion("batchid is null");
            return (Criteria) this;
        }

        public Criteria andBatchidIsNotNull() {
            addCriterion("batchid is not null");
            return (Criteria) this;
        }

        public Criteria andBatchidEqualTo(Long value) {
            addCriterion("batchid =", value, "batchid");
            return (Criteria) this;
        }

        public Criteria andBatchidNotEqualTo(Long value) {
            addCriterion("batchid <>", value, "batchid");
            return (Criteria) this;
        }

        public Criteria andBatchidGreaterThan(Long value) {
            addCriterion("batchid >", value, "batchid");
            return (Criteria) this;
        }

        public Criteria andBatchidGreaterThanOrEqualTo(Long value) {
            addCriterion("batchid >=", value, "batchid");
            return (Criteria) this;
        }

        public Criteria andBatchidLessThan(Long value) {
            addCriterion("batchid <", value, "batchid");
            return (Criteria) this;
        }

        public Criteria andBatchidLessThanOrEqualTo(Long value) {
            addCriterion("batchid <=", value, "batchid");
            return (Criteria) this;
        }

        public Criteria andBatchidIn(List<Long> values) {
            addCriterion("batchid in", values, "batchid");
            return (Criteria) this;
        }

        public Criteria andBatchidNotIn(List<Long> values) {
            addCriterion("batchid not in", values, "batchid");
            return (Criteria) this;
        }

        public Criteria andBatchidBetween(Long value1, Long value2) {
            addCriterion("batchid between", value1, value2, "batchid");
            return (Criteria) this;
        }

        public Criteria andBatchidNotBetween(Long value1, Long value2) {
            addCriterion("batchid not between", value1, value2, "batchid");
            return (Criteria) this;
        }

        public Criteria andEditlistIsNull() {
            addCriterion("editlist is null");
            return (Criteria) this;
        }

        public Criteria andEditlistIsNotNull() {
            addCriterion("editlist is not null");
            return (Criteria) this;
        }

        public Criteria andEditlistEqualTo(String value) {
            addCriterion("editlist =", value, "editlist");
            return (Criteria) this;
        }

        public Criteria andEditlistNotEqualTo(String value) {
            addCriterion("editlist <>", value, "editlist");
            return (Criteria) this;
        }

        public Criteria andEditlistGreaterThan(String value) {
            addCriterion("editlist >", value, "editlist");
            return (Criteria) this;
        }

        public Criteria andEditlistGreaterThanOrEqualTo(String value) {
            addCriterion("editlist >=", value, "editlist");
            return (Criteria) this;
        }

        public Criteria andEditlistLessThan(String value) {
            addCriterion("editlist <", value, "editlist");
            return (Criteria) this;
        }

        public Criteria andEditlistLessThanOrEqualTo(String value) {
            addCriterion("editlist <=", value, "editlist");
            return (Criteria) this;
        }

        public Criteria andEditlistLike(String value) {
            addCriterion("editlist like", value, "editlist");
            return (Criteria) this;
        }

        public Criteria andEditlistNotLike(String value) {
            addCriterion("editlist not like", value, "editlist");
            return (Criteria) this;
        }

        public Criteria andEditlistIn(List<String> values) {
            addCriterion("editlist in", values, "editlist");
            return (Criteria) this;
        }

        public Criteria andEditlistNotIn(List<String> values) {
            addCriterion("editlist not in", values, "editlist");
            return (Criteria) this;
        }

        public Criteria andEditlistBetween(String value1, String value2) {
            addCriterion("editlist between", value1, value2, "editlist");
            return (Criteria) this;
        }

        public Criteria andEditlistNotBetween(String value1, String value2) {
            addCriterion("editlist not between", value1, value2, "editlist");
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

        public Criteria andRankIsNull() {
            addCriterion("rank is null");
            return (Criteria) this;
        }

        public Criteria andRankIsNotNull() {
            addCriterion("rank is not null");
            return (Criteria) this;
        }

        public Criteria andRankEqualTo(Integer value) {
            addCriterion("rank =", value, "rank");
            return (Criteria) this;
        }

        public Criteria andRankNotEqualTo(Integer value) {
            addCriterion("rank <>", value, "rank");
            return (Criteria) this;
        }

        public Criteria andRankGreaterThan(Integer value) {
            addCriterion("rank >", value, "rank");
            return (Criteria) this;
        }

        public Criteria andRankGreaterThanOrEqualTo(Integer value) {
            addCriterion("rank >=", value, "rank");
            return (Criteria) this;
        }

        public Criteria andRankLessThan(Integer value) {
            addCriterion("rank <", value, "rank");
            return (Criteria) this;
        }

        public Criteria andRankLessThanOrEqualTo(Integer value) {
            addCriterion("rank <=", value, "rank");
            return (Criteria) this;
        }

        public Criteria andRankIn(List<Integer> values) {
            addCriterion("rank in", values, "rank");
            return (Criteria) this;
        }

        public Criteria andRankNotIn(List<Integer> values) {
            addCriterion("rank not in", values, "rank");
            return (Criteria) this;
        }

        public Criteria andRankBetween(Integer value1, Integer value2) {
            addCriterion("rank between", value1, value2, "rank");
            return (Criteria) this;
        }

        public Criteria andRankNotBetween(Integer value1, Integer value2) {
            addCriterion("rank not between", value1, value2, "rank");
            return (Criteria) this;
        }

        public Criteria andOperatetimeIsNull() {
            addCriterion("operatetime is null");
            return (Criteria) this;
        }

        public Criteria andOperatetimeIsNotNull() {
            addCriterion("operatetime is not null");
            return (Criteria) this;
        }

        public Criteria andOperatetimeEqualTo(Date value) {
            addCriterion("operatetime =", value, "operatetime");
            return (Criteria) this;
        }

        public Criteria andOperatetimeNotEqualTo(Date value) {
            addCriterion("operatetime <>", value, "operatetime");
            return (Criteria) this;
        }

        public Criteria andOperatetimeGreaterThan(Date value) {
            addCriterion("operatetime >", value, "operatetime");
            return (Criteria) this;
        }

        public Criteria andOperatetimeGreaterThanOrEqualTo(Date value) {
            addCriterion("operatetime >=", value, "operatetime");
            return (Criteria) this;
        }

        public Criteria andOperatetimeLessThan(Date value) {
            addCriterion("operatetime <", value, "operatetime");
            return (Criteria) this;
        }

        public Criteria andOperatetimeLessThanOrEqualTo(Date value) {
            addCriterion("operatetime <=", value, "operatetime");
            return (Criteria) this;
        }

        public Criteria andOperatetimeIn(List<Date> values) {
            addCriterion("operatetime in", values, "operatetime");
            return (Criteria) this;
        }

        public Criteria andOperatetimeNotIn(List<Date> values) {
            addCriterion("operatetime not in", values, "operatetime");
            return (Criteria) this;
        }

        public Criteria andOperatetimeBetween(Date value1, Date value2) {
            addCriterion("operatetime between", value1, value2, "operatetime");
            return (Criteria) this;
        }

        public Criteria andOperatetimeNotBetween(Date value1, Date value2) {
            addCriterion("operatetime not between", value1, value2, "operatetime");
            return (Criteria) this;
        }

        public Criteria andSystypeIsNull() {
            addCriterion("systype is null");
            return (Criteria) this;
        }

        public Criteria andSystypeIsNotNull() {
            addCriterion("systype is not null");
            return (Criteria) this;
        }

        public Criteria andSystypeEqualTo(Integer value) {
            addCriterion("systype =", value, "systype");
            return (Criteria) this;
        }

        public Criteria andSystypeNotEqualTo(Integer value) {
            addCriterion("systype <>", value, "systype");
            return (Criteria) this;
        }

        public Criteria andSystypeGreaterThan(Integer value) {
            addCriterion("systype >", value, "systype");
            return (Criteria) this;
        }

        public Criteria andSystypeGreaterThanOrEqualTo(Integer value) {
            addCriterion("systype >=", value, "systype");
            return (Criteria) this;
        }

        public Criteria andSystypeLessThan(Integer value) {
            addCriterion("systype <", value, "systype");
            return (Criteria) this;
        }

        public Criteria andSystypeLessThanOrEqualTo(Integer value) {
            addCriterion("systype <=", value, "systype");
            return (Criteria) this;
        }

        public Criteria andSystypeIn(List<Integer> values) {
            addCriterion("systype in", values, "systype");
            return (Criteria) this;
        }

        public Criteria andSystypeNotIn(List<Integer> values) {
            addCriterion("systype not in", values, "systype");
            return (Criteria) this;
        }

        public Criteria andSystypeBetween(Integer value1, Integer value2) {
            addCriterion("systype between", value1, value2, "systype");
            return (Criteria) this;
        }

        public Criteria andSystypeNotBetween(Integer value1, Integer value2) {
            addCriterion("systype not between", value1, value2, "systype");
            return (Criteria) this;
        }

        public Criteria andStarttimeIsNull() {
            addCriterion("starttime is null");
            return (Criteria) this;
        }

        public Criteria andStarttimeIsNotNull() {
            addCriterion("starttime is not null");
            return (Criteria) this;
        }

        public Criteria andStarttimeEqualTo(Date value) {
            addCriterion("starttime =", value, "starttime");
            return (Criteria) this;
        }

        public Criteria andStarttimeNotEqualTo(Date value) {
            addCriterion("starttime <>", value, "starttime");
            return (Criteria) this;
        }

        public Criteria andStarttimeGreaterThan(Date value) {
            addCriterion("starttime >", value, "starttime");
            return (Criteria) this;
        }

        public Criteria andStarttimeGreaterThanOrEqualTo(Date value) {
            addCriterion("starttime >=", value, "starttime");
            return (Criteria) this;
        }

        public Criteria andStarttimeLessThan(Date value) {
            addCriterion("starttime <", value, "starttime");
            return (Criteria) this;
        }

        public Criteria andStarttimeLessThanOrEqualTo(Date value) {
            addCriterion("starttime <=", value, "starttime");
            return (Criteria) this;
        }

        public Criteria andStarttimeIn(List<Date> values) {
            addCriterion("starttime in", values, "starttime");
            return (Criteria) this;
        }

        public Criteria andStarttimeNotIn(List<Date> values) {
            addCriterion("starttime not in", values, "starttime");
            return (Criteria) this;
        }

        public Criteria andStarttimeBetween(Date value1, Date value2) {
            addCriterion("starttime between", value1, value2, "starttime");
            return (Criteria) this;
        }

        public Criteria andStarttimeNotBetween(Date value1, Date value2) {
            addCriterion("starttime not between", value1, value2, "starttime");
            return (Criteria) this;
        }

        public Criteria andEndtimeIsNull() {
            addCriterion("endtime is null");
            return (Criteria) this;
        }

        public Criteria andEndtimeIsNotNull() {
            addCriterion("endtime is not null");
            return (Criteria) this;
        }

        public Criteria andEndtimeEqualTo(Date value) {
            addCriterion("endtime =", value, "endtime");
            return (Criteria) this;
        }

        public Criteria andEndtimeNotEqualTo(Date value) {
            addCriterion("endtime <>", value, "endtime");
            return (Criteria) this;
        }

        public Criteria andEndtimeGreaterThan(Date value) {
            addCriterion("endtime >", value, "endtime");
            return (Criteria) this;
        }

        public Criteria andEndtimeGreaterThanOrEqualTo(Date value) {
            addCriterion("endtime >=", value, "endtime");
            return (Criteria) this;
        }

        public Criteria andEndtimeLessThan(Date value) {
            addCriterion("endtime <", value, "endtime");
            return (Criteria) this;
        }

        public Criteria andEndtimeLessThanOrEqualTo(Date value) {
            addCriterion("endtime <=", value, "endtime");
            return (Criteria) this;
        }

        public Criteria andEndtimeIn(List<Date> values) {
            addCriterion("endtime in", values, "endtime");
            return (Criteria) this;
        }

        public Criteria andEndtimeNotIn(List<Date> values) {
            addCriterion("endtime not in", values, "endtime");
            return (Criteria) this;
        }

        public Criteria andEndtimeBetween(Date value1, Date value2) {
            addCriterion("endtime between", value1, value2, "endtime");
            return (Criteria) this;
        }

        public Criteria andEndtimeNotBetween(Date value1, Date value2) {
            addCriterion("endtime not between", value1, value2, "endtime");
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

        public Criteria andIpIsNull() {
            addCriterion("ip is null");
            return (Criteria) this;
        }

        public Criteria andIpIsNotNull() {
            addCriterion("ip is not null");
            return (Criteria) this;
        }

        public Criteria andIpEqualTo(String value) {
            addCriterion("ip =", value, "ip");
            return (Criteria) this;
        }

        public Criteria andIpNotEqualTo(String value) {
            addCriterion("ip <>", value, "ip");
            return (Criteria) this;
        }

        public Criteria andIpGreaterThan(String value) {
            addCriterion("ip >", value, "ip");
            return (Criteria) this;
        }

        public Criteria andIpGreaterThanOrEqualTo(String value) {
            addCriterion("ip >=", value, "ip");
            return (Criteria) this;
        }

        public Criteria andIpLessThan(String value) {
            addCriterion("ip <", value, "ip");
            return (Criteria) this;
        }

        public Criteria andIpLessThanOrEqualTo(String value) {
            addCriterion("ip <=", value, "ip");
            return (Criteria) this;
        }

        public Criteria andIpLike(String value) {
            addCriterion("ip like", value, "ip");
            return (Criteria) this;
        }

        public Criteria andIpNotLike(String value) {
            addCriterion("ip not like", value, "ip");
            return (Criteria) this;
        }

        public Criteria andIpIn(List<String> values) {
            addCriterion("ip in", values, "ip");
            return (Criteria) this;
        }

        public Criteria andIpNotIn(List<String> values) {
            addCriterion("ip not in", values, "ip");
            return (Criteria) this;
        }

        public Criteria andIpBetween(String value1, String value2) {
            addCriterion("ip between", value1, value2, "ip");
            return (Criteria) this;
        }

        public Criteria andIpNotBetween(String value1, String value2) {
            addCriterion("ip not between", value1, value2, "ip");
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