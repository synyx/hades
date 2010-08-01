package org.synyx.hades.extensions.beans;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.io.Serializable;

import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.SimpleTypeConverter;
import org.springframework.util.StringUtils;
import org.synyx.hades.dao.GenericDao;
import org.synyx.hades.dao.orm.GenericDaoSupport.IdAware;
import org.synyx.hades.dao.orm.GenericDaoSupport.PersistableEntityInformation;
import org.synyx.hades.dao.orm.GenericDaoSupport.ReflectiveEntityInformation;
import org.synyx.hades.domain.Persistable;
import org.synyx.hades.util.ClassUtils;


/**
 * Generic {@link PropertyEditor} to map entities handled by a
 * {@link GenericDao} to their id's and vice versa.
 * 
 * @author Oliver Gierke
 */
public class DomainClassPropertyEditor<T extends Serializable> extends
        PropertyEditorSupport {

    private final GenericDao<?, T> dao;
    private final PropertyEditorRegistry registry;


    /**
     * Creates a new {@link DomainClassPropertyEditor} for the given dao.
     * 
     * @param dao
     * @param registry
     */
    public DomainClassPropertyEditor(GenericDao<?, T> dao,
            PropertyEditorRegistry registry) {

        this.dao = dao;
        this.registry = registry;
    }


    /*
     * (non-Javadoc)
     * 
     * @see java.beans.PropertyEditorSupport#setAsText(java.lang.String)
     */
    @Override
    public void setAsText(String idAsString) throws IllegalArgumentException {

        if (!StringUtils.hasText(idAsString)) {
            setValue(null);
            return;
        }

        setValue(dao.readByPrimaryKey(getId(idAsString)));
    }


    /*
     * (non-Javadoc)
     * 
     * @see java.beans.PropertyEditorSupport#getAsText()
     */
    @Override
    public String getAsText() {

        Object entity = getValue();

        if (null == entity) {
            return null;
        }

        Object id = getId(entity);
        return id == null ? null : id.toString();
    }


    /**
     * Looks up the id of the given entity using one of the {@link IdAware}
     * implementations of Hades.
     * 
     * @param entity
     * @return
     */
    private Object getId(Object entity) {

        if (entity instanceof Persistable) {
            return new PersistableEntityInformation().getId(entity);
        } else {
            return new ReflectiveEntityInformation(entity.getClass())
                    .getId(entity);
        }
    }


    /**
     * Returns the actual typed id. Looks up an available customly registered
     * {@link PropertyEditor} from the {@link PropertyEditorRegistry} before
     * falling back on a {@link SimpleTypeConverter} to translate the
     * {@link String} id into the type one.
     * 
     * @param idAsString
     * @return
     */
    @SuppressWarnings("unchecked")
    private T getId(String idAsString) {

        Class<T> idClass = (Class<T>) ClassUtils.getIdClass(dao.getClass());

        PropertyEditor idEditor = registry.findCustomEditor(idClass, null);

        if (idEditor != null) {
            idEditor.setAsText(idAsString);
            return (T) idEditor.getValue();
        }

        return new SimpleTypeConverter()
                .convertIfNecessary(idAsString, idClass);
    }
}
