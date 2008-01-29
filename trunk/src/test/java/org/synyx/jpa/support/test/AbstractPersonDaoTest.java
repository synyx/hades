package org.synyx.jpa.support.test;

import java.util.Arrays;
import java.util.List;

import org.springframework.test.jpa.AbstractJpaTests;
import org.synyx.jpa.support.test.dao.PersonDao;
import org.synyx.jpa.support.test.domain.Person;



/**
 * Base integration test class for <code>PersonDao</code>. Extend this class
 * and provide and application context by overriding
 * <code>getConfigLocations()</code>.
 * 
 * @author Eberhard Wolff
 * @author Oliver Gierke
 */
public abstract class AbstractPersonDaoTest extends AbstractJpaTests {

    // CUT
    protected PersonDao personDao;

    // Test fixture
    private Person firstPerson;
    private Person secondPerson;
    private Integer id;


    /**
     * Setter to inject <code>PersonDao</code> instance.
     * 
     * @param personDao
     */
    public void setPersonDao(PersonDao personDao) {

        this.personDao = personDao;
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.test.AbstractTransactionalSpringContextTests#onSetUpInTransaction()
     */
    @Override
    protected void onSetUpInTransaction() throws Exception {

        firstPerson = new Person("Wolff", 42);
        personDao.save(firstPerson);

        id = firstPerson.getId();
        assertNotNull(id);

        secondPerson = new Person("Johnson", 40);
        personDao.save(secondPerson);
    }


    /**
     * Simple test for context creation.
     */
    public void testContextCreation() {

        assertNotNull(getApplicationContext());
    }


    /**
     * @throws Exception
     */
    public void testRead() throws Exception {

        Person foundPerson = personDao.readByPrimaryKey(id);
        assertEquals(firstPerson.getWeight(), foundPerson.getWeight());
    }


    /**
     * Tests updating a person.
     */
    public void testUpdate() {

        Person foundPerson = personDao.readByPrimaryKey(id);
        Integer updateWeight = 90;
        foundPerson.setWeight(updateWeight);
        Person updatedPerson = personDao.readByPrimaryKey(id);
        assertEquals(updateWeight, updatedPerson.getWeight());
    }


    /**
     * Test
     */
    public void testDelete() {

        personDao.delete(firstPerson);
        assertNull(personDao.readByPrimaryKey(id));
    }


    /**
     * Tests, that searching by name of the reference person returns exactly
     * that instance.
     * 
     * @throws Exception
     */
    public void testFindByName() throws Exception {

        List<Person> byName = personDao.findByName("Wolff");
        assertTrue(byName.size() == 1);
        assertEquals(firstPerson, byName.get(0));
    }


    public void testReadAll() {

        List<Person> reference = Arrays.asList(firstPerson, secondPerson);
        assertTrue(personDao.readAll().containsAll(reference));
    }
}
