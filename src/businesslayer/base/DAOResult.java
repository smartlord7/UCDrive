/**------------ucDrive: REPOSITÓRIO DE FICHEIROS NA UC------------
 University of Coimbra
 Degree in Computer Science and Engineering
 Sistemas Distribuidos
 3rd year, 2nd semester
 Authors:
 Sancho Amaral Simões, 2019217590, uc2019217590@student.uc.pt
 Tiago Filipe Santa Ventura, 2019243695, uc2019243695@student.uc.pt
 Coimbra, 2nd April 2022
 ---------------------------------------------------------------------------*/

package businesslayer.base;

import datalayer.base.IDatabaseEntity;
import java.io.Serializable;
import java.util.HashMap;

/**
 * Class that has the DAOResult methods.
 */

public class DAOResult implements Serializable {

    // region Private properties

    private boolean isQuery;
    private DAOResultStatusEnum status;
    private HashMap<String, String> errors;
    private IDatabaseEntity entity;
    private Class<?> daoClass;
    private Class<?> entityClass;
    private String daoMethod;
    private int code;

    // endregion Private properties

    // region Public methods

    /**
     * Constructor method.
     */
    public DAOResult() {
    }

    /**
     * Constructor method.
     * @param isQuery is the query verification.
     * @param status is the DAOResult status.
     * @param errors are the errors occurred.
     * @param entity is the database entity.
     * @param daoClass is the DAO class.
     * @param entityClass is the entity class.
     * @param daoMethod is the DAO method.
     */
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

    /**
     * Constructor method.
     * @param isQuery is the query verification.
     * @param status is the DAOResult status.
     * @param errors are the errors occurred.
     * @param entity is the database entity.
     * @param daoClass is the DAO class.
     * @param entityClass is the entity class.
     * @param daoMethod is the DAO method.
     * @param code is the identification code.
     */
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

    // endregion Public methods

    // region Getters and Setters

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

    // endregion Getters and Setters

}
