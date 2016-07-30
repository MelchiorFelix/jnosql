/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.diana.api.reader;

import org.apache.diana.api.ReaderField;
import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.*;

/**
 * @author Marcelo de Souza
 */
public class CalendarReaderTest {

    private ReaderField readerField;

    @Before
    public void init() {
        readerField = new CalendarReader();
    }

    @Test
    public void shouldValidateCompatibility() {
        assertTrue(readerField.isCompatible(Calendar.class));
        assertFalse(readerField.isCompatible(String.class));
        assertFalse(readerField.isCompatible(Long.class));
    }

    @Test
    public void shouldConvert() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2010, 10, 9);

        assertEquals(calendar, readerField.read(Calendar.class, calendar));
        assertEquals(calendar, readerField.read(Calendar.class, calendar.getTimeInMillis()));
        assertEquals(calendar, readerField.read(Calendar.class, calendar.getTime()));
    }


}