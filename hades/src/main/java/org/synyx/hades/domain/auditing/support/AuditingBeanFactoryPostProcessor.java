package org.synyx.hades.domain.auditing.support;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.util.StringUtils;


/**
 * {@link BeanFactoryPostProcessor} to add a {@code depends-on} from a
 * {@link org.springframework.orm.jpa.LocalEntityManagerFactoryBean} or
 * {@link org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean}
 * towards the aspect bean configured via
 * {@code &lt;context:spring-configured&gt;}. This has to be done to ensure the
 * aspect is up and running <em>before</em> the
 * {@link javax.persistence.EntityManagerFactory} gets created as this already
 * instantiates entity listeners and we need to get injection into
 * {@link org.springframework.beans.factory.annotation.Configurable} to work in
 * them.
 * 
 * @author Oliver Gierke
 */
public class AuditingBeanFactoryPostProcessor implements
        BeanFactoryPostProcessor {

    static final String BEAN_CONFIGURER_ASPECT_BEAN_NAME =
            "org.springframework.context.config.internalBeanConfigurerAspect";

    private static final String JPA_PACKAGE = "org.springframework.orm.jpa.";
    private static final List<String> CLASSES_TO_DEPEND = Arrays.asList(
            JPA_PACKAGE + "LocalContainerEntityManagerFactoryBean", JPA_PACKAGE
                    + "LocalEntityManagerFactoryBean");


    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.beans.factory.config.BeanFactoryPostProcessor#
     * postProcessBeanFactory
     * (org.springframework.beans.factory.config.ConfigurableListableBeanFactory
     * )
     */
    public void postProcessBeanFactory(
            ConfigurableListableBeanFactory beanFactory) throws BeansException {

        if (!isSpringConfigured(beanFactory)) {
            return;
        }

        for (String beanName : beanFactory.getBeanDefinitionNames()) {

            BeanDefinition definition = beanFactory.getBeanDefinition(beanName);

            if (CLASSES_TO_DEPEND.contains(definition.getBeanClassName())) {
                definition.setDependsOn(StringUtils.addStringToArray(
                        definition.getDependsOn(),
                        BEAN_CONFIGURER_ASPECT_BEAN_NAME));
            }
        }
    }


    /**
     * Returns whether we have a bean factory for which
     * {@code &lt;context:spring-configured&gt;} was activated.
     * 
     * @param factory
     * @return
     */
    private boolean isSpringConfigured(BeanFactory factory) {

        try {
            factory.getBean(BEAN_CONFIGURER_ASPECT_BEAN_NAME);
            return true;
        } catch (NoSuchBeanDefinitionException e) {
            return false;
        }
    }
}
