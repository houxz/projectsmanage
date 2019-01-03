package com.emg.projectsmanage.pojo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ErrorsTaskModelExample {
    protected String orderByClause;

    protected boolean distinct;
    
    protected Integer limit;
    
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

	protected Integer offset;

    protected List<Criteria> oredCriteria;

    public ErrorsTaskModelExample() {
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

        public Criteria andQctaskIsNull() {
            addCriterion("qctask is null");
            return (Criteria) this;
        }

        public Criteria andQctaskIsNotNull() {
            addCriterion("qctask is not null");
            return (Criteria) this;
        }

        public Criteria andQctaskEqualTo(Integer value) {
            addCriterion("qctask =", value, "qctask");
            return (Criteria) this;
        }

        public Criteria andQctaskNotEqualTo(Integer value) {
            addCriterion("qctask <>", value, "qctask");
            return (Criteria) this;
        }

        public Criteria andQctaskGreaterThan(Integer value) {
            addCriterion("qctask >", value, "qctask");
            return (Criteria) this;
        }

        public Criteria andQctaskGreaterThanOrEqualTo(Integer value) {
            addCriterion("qctask >=", value, "qctask");
            return (Criteria) this;
        }

        public Criteria andQctaskLessThan(Integer value) {
            addCriterion("qctask <", value, "qctask");
            return (Criteria) this;
        }

        public Criteria andQctaskLessThanOrEqualTo(Integer value) {
            addCriterion("qctask <=", value, "qctask");
            return (Criteria) this;
        }

        public Criteria andQctaskIn(List<Integer> values) {
            addCriterion("qctask in", values, "qctask");
            return (Criteria) this;
        }

        public Criteria andQctaskNotIn(List<Integer> values) {
            addCriterion("qctask not in", values, "qctask");
            return (Criteria) this;
        }

        public Criteria andQctaskBetween(Integer value1, Integer value2) {
            addCriterion("qctask between", value1, value2, "qctask");
            return (Criteria) this;
        }

        public Criteria andQctaskNotBetween(Integer value1, Integer value2) {
            addCriterion("qctask not between", value1, value2, "qctask");
            return (Criteria) this;
        }

        public Criteria andErrorsrcIsNull() {
            addCriterion("errorsrc is null");
            return (Criteria) this;
        }

        public Criteria andErrorsrcIsNotNull() {
            addCriterion("errorsrc is not null");
            return (Criteria) this;
        }

        public Criteria andErrorsrcEqualTo(Integer value) {
            addCriterion("errorsrc =", value, "errorsrc");
            return (Criteria) this;
        }

        public Criteria andErrorsrcNotEqualTo(Integer value) {
            addCriterion("errorsrc <>", value, "errorsrc");
            return (Criteria) this;
        }

        public Criteria andErrorsrcGreaterThan(Integer value) {
            addCriterion("errorsrc >", value, "errorsrc");
            return (Criteria) this;
        }

        public Criteria andErrorsrcGreaterThanOrEqualTo(Integer value) {
            addCriterion("errorsrc >=", value, "errorsrc");
            return (Criteria) this;
        }

        public Criteria andErrorsrcLessThan(Integer value) {
            addCriterion("errorsrc <", value, "errorsrc");
            return (Criteria) this;
        }

        public Criteria andErrorsrcLessThanOrEqualTo(Integer value) {
            addCriterion("errorsrc <=", value, "errorsrc");
            return (Criteria) this;
        }

        public Criteria andErrorsrcIn(List<Integer> values) {
            addCriterion("errorsrc in", values, "errorsrc");
            return (Criteria) this;
        }

        public Criteria andErrorsrcNotIn(List<Integer> values) {
            addCriterion("errorsrc not in", values, "errorsrc");
            return (Criteria) this;
        }

        public Criteria andErrorsrcBetween(Integer value1, Integer value2) {
            addCriterion("errorsrc between", value1, value2, "errorsrc");
            return (Criteria) this;
        }

        public Criteria andErrorsrcNotBetween(Integer value1, Integer value2) {
            addCriterion("errorsrc not between", value1, value2, "errorsrc");
            return (Criteria) this;
        }

        public Criteria andErrortarIsNull() {
            addCriterion("errortar is null");
            return (Criteria) this;
        }

        public Criteria andErrortarIsNotNull() {
            addCriterion("errortar is not null");
            return (Criteria) this;
        }

        public Criteria andErrortarEqualTo(Integer value) {
            addCriterion("errortar =", value, "errortar");
            return (Criteria) this;
        }

        public Criteria andErrortarNotEqualTo(Integer value) {
            addCriterion("errortar <>", value, "errortar");
            return (Criteria) this;
        }

        public Criteria andErrortarGreaterThan(Integer value) {
            addCriterion("errortar >", value, "errortar");
            return (Criteria) this;
        }

        public Criteria andErrortarGreaterThanOrEqualTo(Integer value) {
            addCriterion("errortar >=", value, "errortar");
            return (Criteria) this;
        }

        public Criteria andErrortarLessThan(Integer value) {
            addCriterion("errortar <", value, "errortar");
            return (Criteria) this;
        }

        public Criteria andErrortarLessThanOrEqualTo(Integer value) {
            addCriterion("errortar <=", value, "errortar");
            return (Criteria) this;
        }

        public Criteria andErrortarIn(List<Integer> values) {
            addCriterion("errortar in", values, "errortar");
            return (Criteria) this;
        }

        public Criteria andErrortarNotIn(List<Integer> values) {
            addCriterion("errortar not in", values, "errortar");
            return (Criteria) this;
        }

        public Criteria andErrortarBetween(Integer value1, Integer value2) {
            addCriterion("errortar between", value1, value2, "errortar");
            return (Criteria) this;
        }

        public Criteria andErrortarNotBetween(Integer value1, Integer value2) {
            addCriterion("errortar not between", value1, value2, "errortar");
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

        public Criteria andMinerroridIsNull() {
            addCriterion("minerrorid is null");
            return (Criteria) this;
        }

        public Criteria andMinerroridIsNotNull() {
            addCriterion("minerrorid is not null");
            return (Criteria) this;
        }

        public Criteria andMinerroridEqualTo(Long value) {
            addCriterion("minerrorid =", value, "minerrorid");
            return (Criteria) this;
        }

        public Criteria andMinerroridNotEqualTo(Long value) {
            addCriterion("minerrorid <>", value, "minerrorid");
            return (Criteria) this;
        }

        public Criteria andMinerroridGreaterThan(Long value) {
            addCriterion("minerrorid >", value, "minerrorid");
            return (Criteria) this;
        }

        public Criteria andMinerroridGreaterThanOrEqualTo(Long value) {
            addCriterion("minerrorid >=", value, "minerrorid");
            return (Criteria) this;
        }

        public Criteria andMinerroridLessThan(Long value) {
            addCriterion("minerrorid <", value, "minerrorid");
            return (Criteria) this;
        }

        public Criteria andMinerroridLessThanOrEqualTo(Long value) {
            addCriterion("minerrorid <=", value, "minerrorid");
            return (Criteria) this;
        }

        public Criteria andMinerroridIn(List<Long> values) {
            addCriterion("minerrorid in", values, "minerrorid");
            return (Criteria) this;
        }

        public Criteria andMinerroridNotIn(List<Long> values) {
            addCriterion("minerrorid not in", values, "minerrorid");
            return (Criteria) this;
        }

        public Criteria andMinerroridBetween(Long value1, Long value2) {
            addCriterion("minerrorid between", value1, value2, "minerrorid");
            return (Criteria) this;
        }

        public Criteria andMinerroridNotBetween(Long value1, Long value2) {
            addCriterion("minerrorid not between", value1, value2, "minerrorid");
            return (Criteria) this;
        }

        public Criteria andCurerroridIsNull() {
            addCriterion("curerrorid is null");
            return (Criteria) this;
        }

        public Criteria andCurerroridIsNotNull() {
            addCriterion("curerrorid is not null");
            return (Criteria) this;
        }

        public Criteria andCurerroridEqualTo(Long value) {
            addCriterion("curerrorid =", value, "curerrorid");
            return (Criteria) this;
        }

        public Criteria andCurerroridNotEqualTo(Long value) {
            addCriterion("curerrorid <>", value, "curerrorid");
            return (Criteria) this;
        }

        public Criteria andCurerroridGreaterThan(Long value) {
            addCriterion("curerrorid >", value, "curerrorid");
            return (Criteria) this;
        }

        public Criteria andCurerroridGreaterThanOrEqualTo(Long value) {
            addCriterion("curerrorid >=", value, "curerrorid");
            return (Criteria) this;
        }

        public Criteria andCurerroridLessThan(Long value) {
            addCriterion("curerrorid <", value, "curerrorid");
            return (Criteria) this;
        }

        public Criteria andCurerroridLessThanOrEqualTo(Long value) {
            addCriterion("curerrorid <=", value, "curerrorid");
            return (Criteria) this;
        }

        public Criteria andCurerroridIn(List<Long> values) {
            addCriterion("curerrorid in", values, "curerrorid");
            return (Criteria) this;
        }

        public Criteria andCurerroridNotIn(List<Long> values) {
            addCriterion("curerrorid not in", values, "curerrorid");
            return (Criteria) this;
        }

        public Criteria andCurerroridBetween(Long value1, Long value2) {
            addCriterion("curerrorid between", value1, value2, "curerrorid");
            return (Criteria) this;
        }

        public Criteria andCurerroridNotBetween(Long value1, Long value2) {
            addCriterion("curerrorid not between", value1, value2, "curerrorid");
            return (Criteria) this;
        }

        public Criteria andMaxerroridIsNull() {
            addCriterion("maxerrorid is null");
            return (Criteria) this;
        }

        public Criteria andMaxerroridIsNotNull() {
            addCriterion("maxerrorid is not null");
            return (Criteria) this;
        }

        public Criteria andMaxerroridEqualTo(Long value) {
            addCriterion("maxerrorid =", value, "maxerrorid");
            return (Criteria) this;
        }

        public Criteria andMaxerroridNotEqualTo(Long value) {
            addCriterion("maxerrorid <>", value, "maxerrorid");
            return (Criteria) this;
        }

        public Criteria andMaxerroridGreaterThan(Long value) {
            addCriterion("maxerrorid >", value, "maxerrorid");
            return (Criteria) this;
        }

        public Criteria andMaxerroridGreaterThanOrEqualTo(Long value) {
            addCriterion("maxerrorid >=", value, "maxerrorid");
            return (Criteria) this;
        }

        public Criteria andMaxerroridLessThan(Long value) {
            addCriterion("maxerrorid <", value, "maxerrorid");
            return (Criteria) this;
        }

        public Criteria andMaxerroridLessThanOrEqualTo(Long value) {
            addCriterion("maxerrorid <=", value, "maxerrorid");
            return (Criteria) this;
        }

        public Criteria andMaxerroridIn(List<Long> values) {
            addCriterion("maxerrorid in", values, "maxerrorid");
            return (Criteria) this;
        }

        public Criteria andMaxerroridNotIn(List<Long> values) {
            addCriterion("maxerrorid not in", values, "maxerrorid");
            return (Criteria) this;
        }

        public Criteria andMaxerroridBetween(Long value1, Long value2) {
            addCriterion("maxerrorid between", value1, value2, "maxerrorid");
            return (Criteria) this;
        }

        public Criteria andMaxerroridNotBetween(Long value1, Long value2) {
            addCriterion("maxerrorid not between", value1, value2, "maxerrorid");
            return (Criteria) this;
        }

        public Criteria andDotasktimeIsNull() {
            addCriterion("dotasktime is null");
            return (Criteria) this;
        }

        public Criteria andDotasktimeIsNotNull() {
            addCriterion("dotasktime is not null");
            return (Criteria) this;
        }

        public Criteria andDotasktimeEqualTo(String value) {
            addCriterion("dotasktime =", value, "dotasktime");
            return (Criteria) this;
        }

        public Criteria andDotasktimeNotEqualTo(String value) {
            addCriterion("dotasktime <>", value, "dotasktime");
            return (Criteria) this;
        }

        public Criteria andDotasktimeGreaterThan(String value) {
            addCriterion("dotasktime >", value, "dotasktime");
            return (Criteria) this;
        }

        public Criteria andDotasktimeGreaterThanOrEqualTo(String value) {
            addCriterion("dotasktime >=", value, "dotasktime");
            return (Criteria) this;
        }

        public Criteria andDotasktimeLessThan(String value) {
            addCriterion("dotasktime <", value, "dotasktime");
            return (Criteria) this;
        }

        public Criteria andDotasktimeLessThanOrEqualTo(String value) {
            addCriterion("dotasktime <=", value, "dotasktime");
            return (Criteria) this;
        }

        public Criteria andDotasktimeLike(String value) {
            addCriterion("dotasktime like", value, "dotasktime");
            return (Criteria) this;
        }

        public Criteria andDotasktimeNotLike(String value) {
            addCriterion("dotasktime not like", value, "dotasktime");
            return (Criteria) this;
        }

        public Criteria andDotasktimeIn(List<String> values) {
            addCriterion("dotasktime in", values, "dotasktime");
            return (Criteria) this;
        }

        public Criteria andDotasktimeNotIn(List<String> values) {
            addCriterion("dotasktime not in", values, "dotasktime");
            return (Criteria) this;
        }

        public Criteria andDotasktimeBetween(String value1, String value2) {
            addCriterion("dotasktime between", value1, value2, "dotasktime");
            return (Criteria) this;
        }

        public Criteria andDotasktimeNotBetween(String value1, String value2) {
            addCriterion("dotasktime not between", value1, value2, "dotasktime");
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

        public Criteria andErrorsetidIsNull() {
            addCriterion("errorsetid is null");
            return (Criteria) this;
        }

        public Criteria andErrorsetidIsNotNull() {
            addCriterion("errorsetid is not null");
            return (Criteria) this;
        }

        public Criteria andErrorsetidEqualTo(Long value) {
            addCriterion("errorsetid =", value, "errorsetid");
            return (Criteria) this;
        }

        public Criteria andErrorsetidNotEqualTo(Long value) {
            addCriterion("errorsetid <>", value, "errorsetid");
            return (Criteria) this;
        }

        public Criteria andErrorsetidGreaterThan(Long value) {
            addCriterion("errorsetid >", value, "errorsetid");
            return (Criteria) this;
        }

        public Criteria andErrorsetidGreaterThanOrEqualTo(Long value) {
            addCriterion("errorsetid >=", value, "errorsetid");
            return (Criteria) this;
        }

        public Criteria andErrorsetidLessThan(Long value) {
            addCriterion("errorsetid <", value, "errorsetid");
            return (Criteria) this;
        }

        public Criteria andErrorsetidLessThanOrEqualTo(Long value) {
            addCriterion("errorsetid <=", value, "errorsetid");
            return (Criteria) this;
        }

        public Criteria andErrorsetidIn(List<Long> values) {
            addCriterion("errorsetid in", values, "errorsetid");
            return (Criteria) this;
        }

        public Criteria andErrorsetidNotIn(List<Long> values) {
            addCriterion("errorsetid not in", values, "errorsetid");
            return (Criteria) this;
        }

        public Criteria andErrorsetidBetween(Long value1, Long value2) {
            addCriterion("errorsetid between", value1, value2, "errorsetid");
            return (Criteria) this;
        }

        public Criteria andErrorsetidNotBetween(Long value1, Long value2) {
            addCriterion("errorsetid not between", value1, value2, "errorsetid");
            return (Criteria) this;
        }

        public Criteria andErrorsetnameIsNull() {
            addCriterion("errorsetname is null");
            return (Criteria) this;
        }

        public Criteria andErrorsetnameIsNotNull() {
            addCriterion("errorsetname is not null");
            return (Criteria) this;
        }

        public Criteria andErrorsetnameEqualTo(String value) {
            addCriterion("errorsetname =", value, "errorsetname");
            return (Criteria) this;
        }

        public Criteria andErrorsetnameNotEqualTo(String value) {
            addCriterion("errorsetname <>", value, "errorsetname");
            return (Criteria) this;
        }

        public Criteria andErrorsetnameGreaterThan(String value) {
            addCriterion("errorsetname >", value, "errorsetname");
            return (Criteria) this;
        }

        public Criteria andErrorsetnameGreaterThanOrEqualTo(String value) {
            addCriterion("errorsetname >=", value, "errorsetname");
            return (Criteria) this;
        }

        public Criteria andErrorsetnameLessThan(String value) {
            addCriterion("errorsetname <", value, "errorsetname");
            return (Criteria) this;
        }

        public Criteria andErrorsetnameLessThanOrEqualTo(String value) {
            addCriterion("errorsetname <=", value, "errorsetname");
            return (Criteria) this;
        }

        public Criteria andErrorsetnameLike(String value) {
            addCriterion("errorsetname like", value, "errorsetname");
            return (Criteria) this;
        }

        public Criteria andErrorsetnameNotLike(String value) {
            addCriterion("errorsetname not like", value, "errorsetname");
            return (Criteria) this;
        }

        public Criteria andErrorsetnameIn(List<String> values) {
            addCriterion("errorsetname in", values, "errorsetname");
            return (Criteria) this;
        }

        public Criteria andErrorsetnameNotIn(List<String> values) {
            addCriterion("errorsetname not in", values, "errorsetname");
            return (Criteria) this;
        }

        public Criteria andErrorsetnameBetween(String value1, String value2) {
            addCriterion("errorsetname between", value1, value2, "errorsetname");
            return (Criteria) this;
        }

        public Criteria andErrorsetnameNotBetween(String value1, String value2) {
            addCriterion("errorsetname not between", value1, value2, "errorsetname");
            return (Criteria) this;
        }

        public Criteria andEnableIsNull() {
            addCriterion("enable is null");
            return (Criteria) this;
        }

        public Criteria andEnableIsNotNull() {
            addCriterion("enable is not null");
            return (Criteria) this;
        }

        public Criteria andEnableEqualTo(Integer value) {
            addCriterion("enable =", value, "enable");
            return (Criteria) this;
        }

        public Criteria andEnableNotEqualTo(Integer value) {
            addCriterion("enable <>", value, "enable");
            return (Criteria) this;
        }

        public Criteria andEnableGreaterThan(Integer value) {
            addCriterion("enable >", value, "enable");
            return (Criteria) this;
        }

        public Criteria andEnableGreaterThanOrEqualTo(Integer value) {
            addCriterion("enable >=", value, "enable");
            return (Criteria) this;
        }

        public Criteria andEnableLessThan(Integer value) {
            addCriterion("enable <", value, "enable");
            return (Criteria) this;
        }

        public Criteria andEnableLessThanOrEqualTo(Integer value) {
            addCriterion("enable <=", value, "enable");
            return (Criteria) this;
        }

        public Criteria andEnableIn(List<Integer> values) {
            addCriterion("enable in", values, "enable");
            return (Criteria) this;
        }

        public Criteria andEnableNotIn(List<Integer> values) {
            addCriterion("enable not in", values, "enable");
            return (Criteria) this;
        }

        public Criteria andEnableBetween(Integer value1, Integer value2) {
            addCriterion("enable between", value1, value2, "enable");
            return (Criteria) this;
        }

        public Criteria andEnableNotBetween(Integer value1, Integer value2) {
            addCriterion("enable not between", value1, value2, "enable");
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

        public Criteria andUpdatetimeIsNull() {
            addCriterion("updatetime is null");
            return (Criteria) this;
        }

        public Criteria andUpdatetimeIsNotNull() {
            addCriterion("updatetime is not null");
            return (Criteria) this;
        }

        public Criteria andUpdatetimeEqualTo(Date value) {
            addCriterion("updatetime =", value, "updatetime");
            return (Criteria) this;
        }

        public Criteria andUpdatetimeNotEqualTo(Date value) {
            addCriterion("updatetime <>", value, "updatetime");
            return (Criteria) this;
        }

        public Criteria andUpdatetimeGreaterThan(Date value) {
            addCriterion("updatetime >", value, "updatetime");
            return (Criteria) this;
        }

        public Criteria andUpdatetimeGreaterThanOrEqualTo(Date value) {
            addCriterion("updatetime >=", value, "updatetime");
            return (Criteria) this;
        }

        public Criteria andUpdatetimeLessThan(Date value) {
            addCriterion("updatetime <", value, "updatetime");
            return (Criteria) this;
        }

        public Criteria andUpdatetimeLessThanOrEqualTo(Date value) {
            addCriterion("updatetime <=", value, "updatetime");
            return (Criteria) this;
        }

        public Criteria andUpdatetimeIn(List<Date> values) {
            addCriterion("updatetime in", values, "updatetime");
            return (Criteria) this;
        }

        public Criteria andUpdatetimeNotIn(List<Date> values) {
            addCriterion("updatetime not in", values, "updatetime");
            return (Criteria) this;
        }

        public Criteria andUpdatetimeBetween(Date value1, Date value2) {
            addCriterion("updatetime between", value1, value2, "updatetime");
            return (Criteria) this;
        }

        public Criteria andUpdatetimeNotBetween(Date value1, Date value2) {
            addCriterion("updatetime not between", value1, value2, "updatetime");
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