/*
 * Copyright 2002-2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.synyx.hades.dao.test;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.synyx.hades.dao.UserDao;
import org.synyx.hades.domain.Role;
import org.synyx.hades.domain.User;


/**
 * Base integration test class for {@code UserDao}. Extend this class and
 * provide and application context by overriding {@code
 * AbstractSingleSpringContextTests#getConfigLocations()}.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")
public class UserDaoTest {

    // CUT
    @Autowired
    private UserDao userDao;

    // Test fixture
    private User firstUser;
    private User secondUser;
    private Integer id;


    @Before
    public void onSetUpInTransaction() throws Exception {

        firstUser = new User("Oliver", "Gierke", "gierke@synyx.de");
        secondUser = new User("Joachim", "Arrasz", "arrasz@synyx.de");
    }


    /**
     * Tests creation of users.
     */
    @Test
    public void testCreation() {

        flushTestUsers();
    }


    /**
     * Tests reading a single user.
     * 
     * @throws Exception
     */
    @Test
    public void testRead() throws Exception {

        flushTestUsers();

        User foundPerson = userDao.readByPrimaryKey(id);
        Assert.assertEquals(firstUser.getFirstname(), foundPerson
                .getFirstname());
    }


    /**
     * Asserts, that a call to {@code UserDao#readByPrimaryKey(Integer)} returns
     * {@code null} for invalid not {@code null} ids.
     */
    @Test
    public void testReadByPrimaryKeyReturnsNullForNotFoundEntities() {

        flushTestUsers();

        Assert.assertNull(userDao.readByPrimaryKey(id * 27));
    }


    /**
     * Tests updating a user.
     */
    @Test
    public void testUpdate() {

        flushTestUsers();

        User foundPerson = userDao.readByPrimaryKey(id);
        foundPerson.setLastname("Schlicht");

        User updatedPerson = userDao.readByPrimaryKey(id);
        Assert.assertEquals(foundPerson.getFirstname(), updatedPerson
                .getFirstname());
    }


    /**
     * Tests deleting a user.
     */
    @Test
    public void testDelete() {

        flushTestUsers();

        userDao.delete(firstUser);
        Assert.assertNull(userDao.readByPrimaryKey(id));
    }


    /**
     * Tests, that searching by the lastname of the reference user returns
     * exactly that instance.
     * 
     * @throws Exception
     */
    @Test
    public void testFindByLastname() throws Exception {

        flushTestUsers();

        List<User> byName = userDao.findByLastname("Gierke");

        Assert.assertTrue(byName.size() == 1);
        Assert.assertEquals(firstUser, byName.get(0));
    }


    /**
     * Tests, that searching by the email address of the reference user returns
     * exactly that instance.
     * 
     * @throws Exception
     */
    @Test
    public void testFindByEmailAddress() throws Exception {

        flushTestUsers();

        User byName = userDao.findByEmailAddress("gierke@synyx.de");

        Assert.assertNotNull(byName);
        Assert.assertEquals(firstUser, byName);
    }


    /**
     * Tests reading all users.
     */
    @Test
    public void testReadAll() {

        flushTestUsers();

        List<User> reference = Arrays.asList(firstUser, secondUser);
        Assert.assertTrue(userDao.readAll().containsAll(reference));
    }


    /**
     * Tests cascading persistence.
     */
    @Test
    public void testCascadesPersisting() {

        // Create link prior to persisting
        firstUser.addColleague(secondUser);

        // Persist
        flushTestUsers();

        // Fetches first user from .. bdatabase
        User firstReferenceUser = userDao.readByPrimaryKey(firstUser.getId());
        Assert.assertEquals(firstUser, firstReferenceUser);

        // Fetch colleagues and assert link
        Set<User> colleagues = firstReferenceUser.getColleagues();
        Assert.assertEquals(1, colleagues.size());
        Assert.assertTrue(colleagues.contains(secondUser));
    }


    /**
     * Tests, that persisting a relationsship without cascade attributes throws
     * a {@code DataAccessException}.
     */
    @Test
    public void testPreventsCascadingRolePersisting() {

        firstUser.addRole(new Role("USER"));

        try {
            flushTestUsers();
            Assert.fail("Expected DataAccessException!");
        } catch (DataAccessException e) {

        }
    }


    /**
     * Tests cascading on {@literal merge} operation.
     */
    @Test
    public void testMergingCascadesCollegueas() {

        firstUser.addColleague(secondUser);
        flushTestUsers();

        firstUser.addColleague(new User("Florian", "Hopf", "hopf@synyx.de"));
        firstUser = userDao.save(firstUser);

        User reference = userDao.readByPrimaryKey(firstUser.getId());
        Set<User> colleagues = reference.getColleagues();

        Assert.assertNotNull(colleagues);
        Assert.assertEquals(2, colleagues.size());
    }


    /**
     * Tests that an exception is being thrown if you try to persist some
     * relation that is not configured to be cascaded.
     */
    @Test
    public void testMergingDoesNotCascadeRoles() {

        flushTestUsers();

        firstUser.addRole(new Role("USER"));

        try {
            userDao.saveAndFlush(firstUser);
            Assert.fail("Expected DataAccessException!");
        } catch (DataAccessException e) {

        }
    }


    /**
     * Tests, that the generic dao implements count correctly.
     */
    @Test
    public void testCountsCorrectly() {

        Long count = userDao.count();

        User user = new User();
        user.setEmailAddress("gierke@synyx.de");
        userDao.save(user);

        Assert.assertTrue(userDao.count().equals(count + 1));
    }


    /**
     * Tests invoking a method of a custom implementation of the DAO interface.
     */
    @Test
    public void testInvocationOfCustomImplementation() {

        userDao.someCustomMethod(new User());
    }


    /**
     * Tests that overriding a finder method is recognized by the DAO
     * implementation. If an overriding method is found it will will be invoked
     * instead of the automatically generated finder.
     */
    @Test
    public void testOverwritingFinder() {

        userDao.findByOverrridingMethod();
    }


    /**
     * Flushes test users to the database.
     */
    private void flushTestUsers() {

        firstUser = userDao.save(firstUser);
        secondUser = userDao.save(secondUser);

        userDao.flush();

        id = firstUser.getId();

        Assert.assertNotNull(id);
        Assert.assertNotNull(secondUser.getId());

        Assert.assertTrue(userDao.exists(id));
        Assert.assertTrue(userDao.exists(secondUser.getId()));
    }
}
