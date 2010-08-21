/*
 * Copyright 2008-2010 the original author or authors.
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
package org.synyx.hades.extensions.beans;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.beans.PropertyEditor;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.PropertyEditorRegistry;
import org.synyx.hades.dao.GenericDao;
import org.synyx.hades.domain.AbstractPersistable;


/**
 * Unit test for {@link DomainClassPropertyEditor}.
 * 
 * @author Oliver Gierke
 */
public class EntityPropertyEditorUnitTest {

    private DomainClassPropertyEditor<Integer> editor;
    private PropertyEditorRegistry registry;
    private UserDao userDao;


    @Before
    public void setUp() {

        registry = mock(PropertyEditorRegistry.class);
        userDao = mock(UserDao.class);

        editor = new DomainClassPropertyEditor<Integer>(userDao, registry);
    }


    @Test
    public void convertsPlainIdTypeCorrectly() throws Exception {

        User user = new User(1);
        when(userDao.readByPrimaryKey(1)).thenReturn(user);

        editor.setAsText("1");

        verify(userDao, times(1)).readByPrimaryKey(1);
    }


    @Test
    public void convertsEntityToIdCorrectly() throws Exception {

        editor.setValue(new User(1));
        assertThat(editor.getAsText(), is("1"));
    }


    @Test
    public void usesCustomEditorIfConfigured() throws Exception {

        PropertyEditor customEditor = mock(PropertyEditor.class);
        when(customEditor.getValue()).thenReturn(1);

        when(registry.findCustomEditor(Integer.class, null)).thenReturn(
                customEditor);

        convertsPlainIdTypeCorrectly();

        verify(customEditor, times(1)).setAsText("1");
    }

    /**
     * Sample entity.
     * 
     * @author Oliver Gierke
     */
    @SuppressWarnings("serial")
    private static class User extends AbstractPersistable<Integer> {

        public User(Integer id) {

            setId(id);
        }
    }

    /**
     * Sample generic DAO interface.
     * 
     * @author Oliver Gierke
     */
    private static interface UserDao extends GenericDao<User, Integer> {

    }
}
