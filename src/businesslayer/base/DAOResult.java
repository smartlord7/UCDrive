package businesslayer.base;

import datalayer.base.IDatabaseEntity;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;

public class DAOResult implements Serializable {
    private boolean isQuery;
    private DAOResultStatusEnum status;
    private HashMap<String, String> errors;
    private IDatabaseEntity entity;
    private Class<?> daoClass;
    private Class<?> entityClass;
    private String daoMethod;
    private int code;

    public DAOResult() {
    }

    public DAOResult(boolean isQuery, DAOResultStatusEnum status, HashMap<String, String> errors,
                     IDatabaseEntity entity, Class<?> daoClass, Class<?> entityClass, String daoMethod) {
        this.isQuery = isQuery;
        this.status = status;
        this.errors = errors;
        this.entity = entity;
        this.daoClass = daoClass;
        this.entityClass = entityClass;
        this.daoMethod = daoMethod;
    }

    public DAOResult(boolean isQuery, DAOResultStatusEnum status, HashMap<String, String> errors, IDatabaseEntity entity,
                     Class<?> daoClass, Class<?> entityClass, String daoMethod, int code) {
        this.isQuery = isQuery;
        this.status = status;
        this.errors = errors;
        this.entity = entity;
        this.daoClass = daoClass;
        this.entityClass = entityClass;
        this.daoMethod = daoMethod;
        this.code = code;
    }

    public boolean isQuery() {
        return isQuery;
    }

    public void setQuery(boolean query) {
        isQuery = query;
    }

    public HashMap<String, String> getErrors() {
        return errors;
    }

    public void setErrors(HashMap<String, String> errors) {
        this.errors = errors;
    }

    public IDatabaseEntity getEntity() {
        return entity;
    }

    public void setEntity(IDatabaseEntity entity) {
        this.entity = entity;
    }

    public DAOResultStatusEnum getStatus() {
        return status;
    }

    public void setStatus(DAOResultStatusEnum status) {
        this.status = status;
    }

    public Class<?> getDaoClass() {
        return daoClass;
    }

    public void setDaoClass(Class<?> daoClass) {
        this.daoClass = daoClass;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDaoMethod() {
        return daoMethod;
    }

    public void setDaoMethod(String daoMethod) {
        this.daoMethod = daoMethod;
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(Class<?> entityClass) {
        this.entityClass = entityClass;
    }
}
