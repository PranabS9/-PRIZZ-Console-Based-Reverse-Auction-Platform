package dao;

import exception.DatabaseOperationException;
import java.util.List;

/**
 * Generic Data Access Object interface that defines standard CRUD operations.
 *
 * <p>Demonstrates: Generics (type parameters &lt;T, ID&gt;), Interface abstraction.</p>
 *
 * <b>Viva tip:</b> T is the entity type (e.g. User), ID is the primary-key type
 * (e.g. Integer).  Any DAO that implements this automatically gets a consistent
 * contract for create / read / update / delete.
 *
 * @param <T>  Domain entity type (e.g. User, ServiceRequest)
 * @param <ID> Primary key type (e.g. Integer)
 */
public interface GenericDAO<T, ID> {

    /** Inserts a new entity record into the database. */
    void create(T entity) throws DatabaseOperationException;

    /** Finds and returns one entity by its primary key, or null if not found. */
    T findById(ID id) throws DatabaseOperationException;

    /** Returns all entity records from the database. */
    List<T> findAll() throws DatabaseOperationException;

    /** Updates an existing entity record in the database. */
    void update(T entity) throws DatabaseOperationException;

    /** Deletes the entity record with the given primary key. */
    void delete(ID id) throws DatabaseOperationException;
}
