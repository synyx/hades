package org.synyx.hades.extensions.converter;

import static org.synyx.hades.util.ClassUtils.*;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;
import org.synyx.hades.dao.GenericDao;


/**
 * @author Oliver Gierke
 */
public class GenericDaoConverter implements ConditionalGenericConverter,
        ApplicationContextAware {

    private Map<Class<?>, GenericDao<?, Serializable>> daoMap;
    private final ConversionService service;


    public GenericDaoConverter(ConversionService service) {

        this.service = service;
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.core.convert.converter.GenericConverter#
     * getConvertibleTypes()
     */
    public Set<ConvertiblePair> getConvertibleTypes() {

        return Collections.singleton(new ConvertiblePair(Object.class,
                Object.class));
    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * org.springframework.core.convert.converter.GenericConverter#convert(java
     * .lang.Object, org.springframework.core.convert.TypeDescriptor,
     * org.springframework.core.convert.TypeDescriptor)
     */
    public Object convert(Object source, TypeDescriptor sourceType,
            TypeDescriptor targetType) {

        GenericDao<?, Serializable> dao =
                getDaoForDomainType(targetType.getType());
        Serializable id = service.convert(source, getIdClass(dao.getClass()));
        return dao.readByPrimaryKey(id);
    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * org.springframework.core.convert.converter.ConditionalGenericConverter
     * #matches(org.springframework.core.convert.TypeDescriptor,
     * org.springframework.core.convert.TypeDescriptor)
     */
    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {

        GenericDao<?, ?> dao = getDaoForDomainType(targetType.getType());

        if (dao == null) {
            return false;
        }

        Class<? extends Serializable> idClass = getIdClass(dao.getClass());

        return service.canConvert(sourceType.getType(), idClass);
    }


    private GenericDao<?, Serializable> getDaoForDomainType(Class<?> domainType) {

        return daoMap.get(domainType);
    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * org.springframework.context.ApplicationContextAware#setApplicationContext
     * (org.springframework.context.ApplicationContext)
     */
    @SuppressWarnings("unchecked")
    public void setApplicationContext(ApplicationContext context) {

        @SuppressWarnings("rawtypes")
        Collection<GenericDao> daos =
                BeanFactoryUtils.beansOfTypeIncludingAncestors(context,
                        GenericDao.class).values();

        this.daoMap =
                new HashMap<Class<?>, GenericDao<?, Serializable>>(daos.size());

        for (GenericDao<?, ?> dao : daos) {
            Class<?> domainClass = getDomainClass(dao.getClass());
            this.daoMap.put(domainClass, (GenericDao<?, Serializable>) dao);
        }
    }
}
