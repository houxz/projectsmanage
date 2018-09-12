package com.emg.projectsmanage.pojo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CapacityModelExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public CapacityModelExample() {
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

        public Criteria andErrorcountIsNull() {
            addCriterion("errorcount is null");
            return (Criteria) this;
        }

        public Criteria andErrorcountIsNotNull() {
            addCriterion("errorcount is not null");
            return (Criteria) this;
        }

        public Criteria andErrorcountEqualTo(Integer value) {
            addCriterion("errorcount =", value, "errorcount");
            return (Criteria) this;
        }

        public Criteria andErrorcountNotEqualTo(Integer value) {
            addCriterion("errorcount <>", value, "errorcount");
            return (Criteria) this;
        }

        public Criteria andErrorcountGreaterThan(Integer value) {
            addCriterion("errorcount >", value, "errorcount");
            return (Criteria) this;
        }

        public Criteria andErrorcountGreaterThanOrEqualTo(Integer value) {
            addCriterion("errorcount >=", value, "errorcount");
            return (Criteria) this;
        }

        public Criteria andErrorcountLessThan(Integer value) {
            addCriterion("errorcount <", value, "errorcount");
            return (Criteria) this;
        }

        public Criteria andErrorcountLessThanOrEqualTo(Integer value) {
            addCriterion("errorcount <=", value, "errorcount");
            return (Criteria) this;
        }

        public Criteria andErrorcountIn(List<Integer> values) {
            addCriterion("errorcount in", values, "errorcount");
            return (Criteria) this;
        }

        public Criteria andErrorcountNotIn(List<Integer> values) {
            addCriterion("errorcount not in", values, "errorcount");
            return (Criteria) this;
        }

        public Criteria andErrorcountBetween(Integer value1, Integer value2) {
            addCriterion("errorcount between", value1, value2, "errorcount");
            return (Criteria) this;
        }

        public Criteria andErrorcountNotBetween(Integer value1, Integer value2) {
            addCriterion("errorcount not between", value1, value2, "errorcount");
            return (Criteria) this;
        }

        public Criteria andTaskcountIsNull() {
            addCriterion("taskcount is null");
            return (Criteria) this;
        }

        public Criteria andTaskcountIsNotNull() {
            addCriterion("taskcount is not null");
            return (Criteria) this;
        }

        public Criteria andTaskcountEqualTo(Integer value) {
            addCriterion("taskcount =", value, "taskcount");
            return (Criteria) this;
        }

        public Criteria andTaskcountNotEqualTo(Integer value) {
            addCriterion("taskcount <>", value, "taskcount");
            return (Criteria) this;
        }

        public Criteria andTaskcountGreaterThan(Integer value) {
            addCriterion("taskcount >", value, "taskcount");
            return (Criteria) this;
        }

        public Criteria andTaskcountGreaterThanOrEqualTo(Integer value) {
            addCriterion("taskcount >=", value, "taskcount");
            return (Criteria) this;
        }

        public Criteria andTaskcountLessThan(Integer value) {
            addCriterion("taskcount <", value, "taskcount");
            return (Criteria) this;
        }

        public Criteria andTaskcountLessThanOrEqualTo(Integer value) {
            addCriterion("taskcount <=", value, "taskcount");
            return (Criteria) this;
        }

        public Criteria andTaskcountIn(List<Integer> values) {
            addCriterion("taskcount in", values, "taskcount");
            return (Criteria) this;
        }

        public Criteria andTaskcountNotIn(List<Integer> values) {
            addCriterion("taskcount not in", values, "taskcount");
            return (Criteria) this;
        }

        public Criteria andTaskcountBetween(Integer value1, Integer value2) {
            addCriterion("taskcount between", value1, value2, "taskcount");
            return (Criteria) this;
        }

        public Criteria andTaskcountNotBetween(Integer value1, Integer value2) {
            addCriterion("taskcount not between", value1, value2, "taskcount");
            return (Criteria) this;
        }

        public Criteria andModifypoiIsNull() {
            addCriterion("modifypoi is null");
            return (Criteria) this;
        }

        public Criteria andModifypoiIsNotNull() {
            addCriterion("modifypoi is not null");
            return (Criteria) this;
        }

        public Criteria andModifypoiEqualTo(Integer value) {
            addCriterion("modifypoi =", value, "modifypoi");
            return (Criteria) this;
        }

        public Criteria andModifypoiNotEqualTo(Integer value) {
            addCriterion("modifypoi <>", value, "modifypoi");
            return (Criteria) this;
        }

        public Criteria andModifypoiGreaterThan(Integer value) {
            addCriterion("modifypoi >", value, "modifypoi");
            return (Criteria) this;
        }

        public Criteria andModifypoiGreaterThanOrEqualTo(Integer value) {
            addCriterion("modifypoi >=", value, "modifypoi");
            return (Criteria) this;
        }

        public Criteria andModifypoiLessThan(Integer value) {
            addCriterion("modifypoi <", value, "modifypoi");
            return (Criteria) this;
        }

        public Criteria andModifypoiLessThanOrEqualTo(Integer value) {
            addCriterion("modifypoi <=", value, "modifypoi");
            return (Criteria) this;
        }

        public Criteria andModifypoiIn(List<Integer> values) {
            addCriterion("modifypoi in", values, "modifypoi");
            return (Criteria) this;
        }

        public Criteria andModifypoiNotIn(List<Integer> values) {
            addCriterion("modifypoi not in", values, "modifypoi");
            return (Criteria) this;
        }

        public Criteria andModifypoiBetween(Integer value1, Integer value2) {
            addCriterion("modifypoi between", value1, value2, "modifypoi");
            return (Criteria) this;
        }

        public Criteria andModifypoiNotBetween(Integer value1, Integer value2) {
            addCriterion("modifypoi not between", value1, value2, "modifypoi");
            return (Criteria) this;
        }

        public Criteria andCreatepoiIsNull() {
            addCriterion("createpoi is null");
            return (Criteria) this;
        }

        public Criteria andCreatepoiIsNotNull() {
            addCriterion("createpoi is not null");
            return (Criteria) this;
        }

        public Criteria andCreatepoiEqualTo(Integer value) {
            addCriterion("createpoi =", value, "createpoi");
            return (Criteria) this;
        }

        public Criteria andCreatepoiNotEqualTo(Integer value) {
            addCriterion("createpoi <>", value, "createpoi");
            return (Criteria) this;
        }

        public Criteria andCreatepoiGreaterThan(Integer value) {
            addCriterion("createpoi >", value, "createpoi");
            return (Criteria) this;
        }

        public Criteria andCreatepoiGreaterThanOrEqualTo(Integer value) {
            addCriterion("createpoi >=", value, "createpoi");
            return (Criteria) this;
        }

        public Criteria andCreatepoiLessThan(Integer value) {
            addCriterion("createpoi <", value, "createpoi");
            return (Criteria) this;
        }

        public Criteria andCreatepoiLessThanOrEqualTo(Integer value) {
            addCriterion("createpoi <=", value, "createpoi");
            return (Criteria) this;
        }

        public Criteria andCreatepoiIn(List<Integer> values) {
            addCriterion("createpoi in", values, "createpoi");
            return (Criteria) this;
        }

        public Criteria andCreatepoiNotIn(List<Integer> values) {
            addCriterion("createpoi not in", values, "createpoi");
            return (Criteria) this;
        }

        public Criteria andCreatepoiBetween(Integer value1, Integer value2) {
            addCriterion("createpoi between", value1, value2, "createpoi");
            return (Criteria) this;
        }

        public Criteria andCreatepoiNotBetween(Integer value1, Integer value2) {
            addCriterion("createpoi not between", value1, value2, "createpoi");
            return (Criteria) this;
        }

        public Criteria andDeletepoiIsNull() {
            addCriterion("deletepoi is null");
            return (Criteria) this;
        }

        public Criteria andDeletepoiIsNotNull() {
            addCriterion("deletepoi is not null");
            return (Criteria) this;
        }

        public Criteria andDeletepoiEqualTo(Integer value) {
            addCriterion("deletepoi =", value, "deletepoi");
            return (Criteria) this;
        }

        public Criteria andDeletepoiNotEqualTo(Integer value) {
            addCriterion("deletepoi <>", value, "deletepoi");
            return (Criteria) this;
        }

        public Criteria andDeletepoiGreaterThan(Integer value) {
            addCriterion("deletepoi >", value, "deletepoi");
            return (Criteria) this;
        }

        public Criteria andDeletepoiGreaterThanOrEqualTo(Integer value) {
            addCriterion("deletepoi >=", value, "deletepoi");
            return (Criteria) this;
        }

        public Criteria andDeletepoiLessThan(Integer value) {
            addCriterion("deletepoi <", value, "deletepoi");
            return (Criteria) this;
        }

        public Criteria andDeletepoiLessThanOrEqualTo(Integer value) {
            addCriterion("deletepoi <=", value, "deletepoi");
            return (Criteria) this;
        }

        public Criteria andDeletepoiIn(List<Integer> values) {
            addCriterion("deletepoi in", values, "deletepoi");
            return (Criteria) this;
        }

        public Criteria andDeletepoiNotIn(List<Integer> values) {
            addCriterion("deletepoi not in", values, "deletepoi");
            return (Criteria) this;
        }

        public Criteria andDeletepoiBetween(Integer value1, Integer value2) {
            addCriterion("deletepoi between", value1, value2, "deletepoi");
            return (Criteria) this;
        }

        public Criteria andDeletepoiNotBetween(Integer value1, Integer value2) {
            addCriterion("deletepoi not between", value1, value2, "deletepoi");
            return (Criteria) this;
        }

        public Criteria andConfirmpoiIsNull() {
            addCriterion("confirmpoi is null");
            return (Criteria) this;
        }

        public Criteria andConfirmpoiIsNotNull() {
            addCriterion("confirmpoi is not null");
            return (Criteria) this;
        }

        public Criteria andConfirmpoiEqualTo(Integer value) {
            addCriterion("confirmpoi =", value, "confirmpoi");
            return (Criteria) this;
        }

        public Criteria andConfirmpoiNotEqualTo(Integer value) {
            addCriterion("confirmpoi <>", value, "confirmpoi");
            return (Criteria) this;
        }

        public Criteria andConfirmpoiGreaterThan(Integer value) {
            addCriterion("confirmpoi >", value, "confirmpoi");
            return (Criteria) this;
        }

        public Criteria andConfirmpoiGreaterThanOrEqualTo(Integer value) {
            addCriterion("confirmpoi >=", value, "confirmpoi");
            return (Criteria) this;
        }

        public Criteria andConfirmpoiLessThan(Integer value) {
            addCriterion("confirmpoi <", value, "confirmpoi");
            return (Criteria) this;
        }

        public Criteria andConfirmpoiLessThanOrEqualTo(Integer value) {
            addCriterion("confirmpoi <=", value, "confirmpoi");
            return (Criteria) this;
        }

        public Criteria andConfirmpoiIn(List<Integer> values) {
            addCriterion("confirmpoi in", values, "confirmpoi");
            return (Criteria) this;
        }

        public Criteria andConfirmpoiNotIn(List<Integer> values) {
            addCriterion("confirmpoi not in", values, "confirmpoi");
            return (Criteria) this;
        }

        public Criteria andConfirmpoiBetween(Integer value1, Integer value2) {
            addCriterion("confirmpoi between", value1, value2, "confirmpoi");
            return (Criteria) this;
        }

        public Criteria andConfirmpoiNotBetween(Integer value1, Integer value2) {
            addCriterion("confirmpoi not between", value1, value2, "confirmpoi");
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

        public Criteria andVisualerrorcountEqualTo(Integer value) {
            addCriterion("visualerrorcount =", value, "visualerrorcount");
            return (Criteria) this;
        }

        public Criteria andVisualerrorcountNotEqualTo(Integer value) {
            addCriterion("visualerrorcount <>", value, "visualerrorcount");
            return (Criteria) this;
        }

        public Criteria andVisualerrorcountGreaterThan(Integer value) {
            addCriterion("visualerrorcount >", value, "visualerrorcount");
            return (Criteria) this;
        }

        public Criteria andVisualerrorcountGreaterThanOrEqualTo(Integer value) {
            addCriterion("visualerrorcount >=", value, "visualerrorcount");
            return (Criteria) this;
        }

        public Criteria andVisualerrorcountLessThan(Integer value) {
            addCriterion("visualerrorcount <", value, "visualerrorcount");
            return (Criteria) this;
        }

        public Criteria andVisualerrorcountLessThanOrEqualTo(Integer value) {
            addCriterion("visualerrorcount <=", value, "visualerrorcount");
            return (Criteria) this;
        }

        public Criteria andVisualerrorcountIn(List<Integer> values) {
            addCriterion("visualerrorcount in", values, "visualerrorcount");
            return (Criteria) this;
        }

        public Criteria andVisualerrorcountNotIn(List<Integer> values) {
            addCriterion("visualerrorcount not in", values, "visualerrorcount");
            return (Criteria) this;
        }

        public Criteria andVisualerrorcountBetween(Integer value1, Integer value2) {
            addCriterion("visualerrorcount between", value1, value2, "visualerrorcount");
            return (Criteria) this;
        }

        public Criteria andVisualerrorcountNotBetween(Integer value1, Integer value2) {
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