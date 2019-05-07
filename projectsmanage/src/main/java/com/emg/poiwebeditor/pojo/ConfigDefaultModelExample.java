package com.emg.poiwebeditor.pojo;

import java.util.ArrayList;
import java.util.List;

public class ConfigDefaultModelExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public ConfigDefaultModelExample() {
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

        public Criteria andIdEqualTo(Integer value) {
            addCriterion("id =", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotEqualTo(Integer value) {
            addCriterion("id <>", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThan(Integer value) {
            addCriterion("id >", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("id >=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThan(Integer value) {
            addCriterion("id <", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThanOrEqualTo(Integer value) {
            addCriterion("id <=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdIn(List<Integer> values) {
            addCriterion("id in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotIn(List<Integer> values) {
            addCriterion("id not in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdBetween(Integer value1, Integer value2) {
            addCriterion("id between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotBetween(Integer value1, Integer value2) {
            addCriterion("id not between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andConfigidIsNull() {
            addCriterion("configid is null");
            return (Criteria) this;
        }

        public Criteria andConfigidIsNotNull() {
            addCriterion("configid is not null");
            return (Criteria) this;
        }

        public Criteria andConfigidEqualTo(Integer value) {
            addCriterion("configid =", value, "configid");
            return (Criteria) this;
        }

        public Criteria andConfigidNotEqualTo(Integer value) {
            addCriterion("configid <>", value, "configid");
            return (Criteria) this;
        }

        public Criteria andConfigidGreaterThan(Integer value) {
            addCriterion("configid >", value, "configid");
            return (Criteria) this;
        }

        public Criteria andConfigidGreaterThanOrEqualTo(Integer value) {
            addCriterion("configid >=", value, "configid");
            return (Criteria) this;
        }

        public Criteria andConfigidLessThan(Integer value) {
            addCriterion("configid <", value, "configid");
            return (Criteria) this;
        }

        public Criteria andConfigidLessThanOrEqualTo(Integer value) {
            addCriterion("configid <=", value, "configid");
            return (Criteria) this;
        }

        public Criteria andConfigidIn(List<Integer> values) {
            addCriterion("configid in", values, "configid");
            return (Criteria) this;
        }

        public Criteria andConfigidNotIn(List<Integer> values) {
            addCriterion("configid not in", values, "configid");
            return (Criteria) this;
        }

        public Criteria andConfigidBetween(Integer value1, Integer value2) {
            addCriterion("configid between", value1, value2, "configid");
            return (Criteria) this;
        }

        public Criteria andConfigidNotBetween(Integer value1, Integer value2) {
            addCriterion("configid not between", value1, value2, "configid");
            return (Criteria) this;
        }

        public Criteria andProcesstypeIsNull() {
            addCriterion("processtype is null");
            return (Criteria) this;
        }

        public Criteria andProcesstypeIsNotNull() {
            addCriterion("processtype is not null");
            return (Criteria) this;
        }

        public Criteria andProcesstypeEqualTo(Integer value) {
            addCriterion("processtype =", value, "processtype");
            return (Criteria) this;
        }

        public Criteria andProcesstypeNotEqualTo(Integer value) {
            addCriterion("processtype <>", value, "processtype");
            return (Criteria) this;
        }

        public Criteria andProcesstypeGreaterThan(Integer value) {
            addCriterion("processtype >", value, "processtype");
            return (Criteria) this;
        }

        public Criteria andProcesstypeGreaterThanOrEqualTo(Integer value) {
            addCriterion("processtype >=", value, "processtype");
            return (Criteria) this;
        }

        public Criteria andProcesstypeLessThan(Integer value) {
            addCriterion("processtype <", value, "processtype");
            return (Criteria) this;
        }

        public Criteria andProcesstypeLessThanOrEqualTo(Integer value) {
            addCriterion("processtype <=", value, "processtype");
            return (Criteria) this;
        }

        public Criteria andProcesstypeIn(List<Integer> values) {
            addCriterion("processtype in", values, "processtype");
            return (Criteria) this;
        }

        public Criteria andProcesstypeNotIn(List<Integer> values) {
            addCriterion("processtype not in", values, "processtype");
            return (Criteria) this;
        }

        public Criteria andProcesstypeBetween(Integer value1, Integer value2) {
            addCriterion("processtype between", value1, value2, "processtype");
            return (Criteria) this;
        }

        public Criteria andProcesstypeNotBetween(Integer value1, Integer value2) {
            addCriterion("processtype not between", value1, value2, "processtype");
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

        public Criteria andVisibleIsNull() {
            addCriterion("visible is null");
            return (Criteria) this;
        }

        public Criteria andVisibleIsNotNull() {
            addCriterion("visible is not null");
            return (Criteria) this;
        }

        public Criteria andVisibleEqualTo(Byte value) {
            addCriterion("visible =", value, "visible");
            return (Criteria) this;
        }

        public Criteria andVisibleNotEqualTo(Byte value) {
            addCriterion("visible <>", value, "visible");
            return (Criteria) this;
        }

        public Criteria andVisibleGreaterThan(Byte value) {
            addCriterion("visible >", value, "visible");
            return (Criteria) this;
        }

        public Criteria andVisibleGreaterThanOrEqualTo(Byte value) {
            addCriterion("visible >=", value, "visible");
            return (Criteria) this;
        }

        public Criteria andVisibleLessThan(Byte value) {
            addCriterion("visible <", value, "visible");
            return (Criteria) this;
        }

        public Criteria andVisibleLessThanOrEqualTo(Byte value) {
            addCriterion("visible <=", value, "visible");
            return (Criteria) this;
        }

        public Criteria andVisibleIn(List<Byte> values) {
            addCriterion("visible in", values, "visible");
            return (Criteria) this;
        }

        public Criteria andVisibleNotIn(List<Byte> values) {
            addCriterion("visible not in", values, "visible");
            return (Criteria) this;
        }

        public Criteria andVisibleBetween(Byte value1, Byte value2) {
            addCriterion("visible between", value1, value2, "visible");
            return (Criteria) this;
        }

        public Criteria andVisibleNotBetween(Byte value1, Byte value2) {
            addCriterion("visible not between", value1, value2, "visible");
            return (Criteria) this;
        }

        public Criteria andEditableIsNull() {
            addCriterion("editable is null");
            return (Criteria) this;
        }

        public Criteria andEditableIsNotNull() {
            addCriterion("editable is not null");
            return (Criteria) this;
        }

        public Criteria andEditableEqualTo(Byte value) {
            addCriterion("editable =", value, "editable");
            return (Criteria) this;
        }

        public Criteria andEditableNotEqualTo(Byte value) {
            addCriterion("editable <>", value, "editable");
            return (Criteria) this;
        }

        public Criteria andEditableGreaterThan(Byte value) {
            addCriterion("editable >", value, "editable");
            return (Criteria) this;
        }

        public Criteria andEditableGreaterThanOrEqualTo(Byte value) {
            addCriterion("editable >=", value, "editable");
            return (Criteria) this;
        }

        public Criteria andEditableLessThan(Byte value) {
            addCriterion("editable <", value, "editable");
            return (Criteria) this;
        }

        public Criteria andEditableLessThanOrEqualTo(Byte value) {
            addCriterion("editable <=", value, "editable");
            return (Criteria) this;
        }

        public Criteria andEditableIn(List<Byte> values) {
            addCriterion("editable in", values, "editable");
            return (Criteria) this;
        }

        public Criteria andEditableNotIn(List<Byte> values) {
            addCriterion("editable not in", values, "editable");
            return (Criteria) this;
        }

        public Criteria andEditableBetween(Byte value1, Byte value2) {
            addCriterion("editable between", value1, value2, "editable");
            return (Criteria) this;
        }

        public Criteria andEditableNotBetween(Byte value1, Byte value2) {
            addCriterion("editable not between", value1, value2, "editable");
            return (Criteria) this;
        }

        public Criteria andDefaultvalueIsNull() {
            addCriterion("defaultvalue is null");
            return (Criteria) this;
        }

        public Criteria andDefaultvalueIsNotNull() {
            addCriterion("defaultvalue is not null");
            return (Criteria) this;
        }

        public Criteria andDefaultvalueEqualTo(String value) {
            addCriterion("defaultvalue =", value, "defaultvalue");
            return (Criteria) this;
        }

        public Criteria andDefaultvalueNotEqualTo(String value) {
            addCriterion("defaultvalue <>", value, "defaultvalue");
            return (Criteria) this;
        }

        public Criteria andDefaultvalueGreaterThan(String value) {
            addCriterion("defaultvalue >", value, "defaultvalue");
            return (Criteria) this;
        }

        public Criteria andDefaultvalueGreaterThanOrEqualTo(String value) {
            addCriterion("defaultvalue >=", value, "defaultvalue");
            return (Criteria) this;
        }

        public Criteria andDefaultvalueLessThan(String value) {
            addCriterion("defaultvalue <", value, "defaultvalue");
            return (Criteria) this;
        }

        public Criteria andDefaultvalueLessThanOrEqualTo(String value) {
            addCriterion("defaultvalue <=", value, "defaultvalue");
            return (Criteria) this;
        }

        public Criteria andDefaultvalueLike(String value) {
            addCriterion("defaultvalue like", value, "defaultvalue");
            return (Criteria) this;
        }

        public Criteria andDefaultvalueNotLike(String value) {
            addCriterion("defaultvalue not like", value, "defaultvalue");
            return (Criteria) this;
        }

        public Criteria andDefaultvalueIn(List<String> values) {
            addCriterion("defaultvalue in", values, "defaultvalue");
            return (Criteria) this;
        }

        public Criteria andDefaultvalueNotIn(List<String> values) {
            addCriterion("defaultvalue not in", values, "defaultvalue");
            return (Criteria) this;
        }

        public Criteria andDefaultvalueBetween(String value1, String value2) {
            addCriterion("defaultvalue between", value1, value2, "defaultvalue");
            return (Criteria) this;
        }

        public Criteria andDefaultvalueNotBetween(String value1, String value2) {
            addCriterion("defaultvalue not between", value1, value2, "defaultvalue");
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