package org.synyx.hades.dao.orm;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.synyx.hades.dao.UserDao;
import org.synyx.hades.domain.User;


/**
 * Integration test for transactional behaviour of DAO operations.
 * 
 * @author Oliver Gierke
 */
@ContextConfiguration({ "classpath:namespace-autoconfig-context.xml",
        "classpath:tx-manager.xml" })
public class TransactionalDaoIntegrationTest extends
        AbstractJUnit4SpringContextTests {

    @Autowired
    private UserDao dao;

    @Autowired
    private DelegatingTransactionManager transactionManager;


    @Before
    public void setUp() {

        transactionManager.resetCount();
    }


    @After
    public void tearDown() {

        dao.deleteAll();
    }


    @Test
    public void simpleManipulatingOperation() throws Exception {

        dao.saveAndFlush(new User("foo", "bar", "foo@bar.de"));
        assertThat(transactionManager.getTransactionRequests(), is(1));
    }


    @Test
    public void unannotatedFinder() throws Exception {

        dao.findByEmailAddress("foo@bar.de");
        assertThat(transactionManager.getTransactionRequests(), is(0));
    }


    @Test
    public void invokeTransactionalFinder() throws Exception {

        dao.findByHadesQuery("foo@bar.de");
        assertThat(transactionManager.getTransactionRequests(), is(1));
    }


    @Test
    public void invokeRedeclaredMethod() throws Exception {

        dao.readByPrimaryKey(1);
        assertFalse(transactionManager.getDefinition().isReadOnly());
    }

    public static class DelegatingTransactionManager implements
            PlatformTransactionManager {

        private PlatformTransactionManager txManager;
        private int transactionRequests;
        private TransactionDefinition definition;


        public DelegatingTransactionManager(PlatformTransactionManager txManager) {

            this.txManager = txManager;
        }


        public void commit(TransactionStatus status)
                throws TransactionException {

            txManager.commit(status);
        }


        public TransactionStatus getTransaction(TransactionDefinition definition)
                throws TransactionException {

            this.transactionRequests++;
            this.definition = definition;

            return txManager.getTransaction(definition);
        }


        public int getTransactionRequests() {

            return transactionRequests;
        }


        public TransactionDefinition getDefinition() {

            return definition;
        }


        public void resetCount() {

            this.transactionRequests = 0;
            this.definition = null;
        }


        public void rollback(TransactionStatus status)
                throws TransactionException {

            txManager.rollback(status);
        }
    }
}
