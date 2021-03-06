/*
 * Copyright 2008-2010 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.synyx.hades.dao.query;

/**
 * Exception to be thrown when trying to access a {@link Parameter} with an
 * invalid index inside a {@link Parameters} instance.
 * 
 * @author Oliver Gierke
 */
class ParameterOutOfBoundsException extends RuntimeException {

    private static final long serialVersionUID = 8433209953653278886L;


    /**
     * Creates a new {@link ParameterOutOfBoundsException} with the given
     * exception as cause.
     * 
     * @param cause
     */
    public ParameterOutOfBoundsException(Throwable cause) {

        super(cause);
    }
}
