/*
 *
 *  Copyright (c) 2017 Otávio Santana and others
 *   All rights reserved. This program and the accompanying materials
 *   are made available under the terms of the Eclipse Public License v1.0
 *   and Apache License v2.0 which accompanies this distribution.
 *   The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 *   and the Apache License v2.0 is available at http://www.opensource.org/licenses/apache2.0.php.
 *
 *   You may elect to redistribute this code under either of these licenses.
 *
 *   Contributors:
 *
 *   Otavio Santana
 *
 */
package org.eclipse.jnosql.diana.document.query;

import jakarta.nosql.Condition;
import jakarta.nosql.document.Document;
import jakarta.nosql.document.DocumentCondition;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

class ReadOnlyDocumentCondition implements DocumentCondition {

    private final DocumentCondition condition;

    ReadOnlyDocumentCondition(DocumentCondition condition) {
        this.condition = requireNonNull(condition, "condition is required");
    }

    @Override
    public Document getDocument() {
        return condition.getDocument();
    }

    @Override
    public Condition getCondition() {
        return condition.getCondition();
    }

    @Override
    public DocumentCondition and(DocumentCondition condition) {
        throw new IllegalStateException("You cannot change the status after building the query");
    }

    @Override
    public DocumentCondition negate() {
        throw new IllegalStateException("You cannot change the status after building the query");
    }

    @Override
    public DocumentCondition or(DocumentCondition condition) {
        throw new IllegalStateException("You cannot change the status after building the query");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !DocumentCondition.class.isAssignableFrom(o.getClass())) {
            return false;
        }
        DocumentCondition that = (DocumentCondition) o;
        return Objects.equals(condition, that);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(condition);
    }

    @Override
    public String toString() {
        return  "ReadOnlyDocumentCondition{" + "condition=" + condition +
                '}';
    }
}
