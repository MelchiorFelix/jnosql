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

package org.apache.diana.cassandra.column;


import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.ResultSetFuture;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.querybuilder.BuiltStatement;
import com.datastax.driver.core.querybuilder.Insert;
import org.apache.diana.api.ExecuteAsyncQueryException;
import org.apache.diana.api.column.ColumnFamilyEntity;
import org.apache.diana.api.column.ColumnFamilyManager;
import org.apache.diana.api.column.ColumnQuery;
import org.apache.diana.api.column.PreparedStatement;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.stream.Collectors;

class CassandraDocumentEntityManager implements ColumnFamilyManager {


    private final Session session;

    private final Executor executor;

    private final String keyspace;

    CassandraDocumentEntityManager(Session session, Executor executor, String keyspace) {
        this.session = session;
        this.executor = executor;
        this.keyspace = keyspace;
    }

    @Override
    public ColumnFamilyEntity save(ColumnFamilyEntity entity) {
        Insert insert = QueryUtils.insert(entity, keyspace);
        session.execute(insert);
        return entity;
    }

    @Override
    public void saveAsync(ColumnFamilyEntity entity) {
        Insert insert = QueryUtils.insert(entity, keyspace);
        session.executeAsync(insert);
    }

    @Override
    public void saveAsync(ColumnFamilyEntity entity, Consumer<ColumnFamilyEntity> consumer) {
        Insert insert = QueryUtils.insert(entity, keyspace);
        ResultSetFuture resultSetFuture = session.executeAsync(insert);
        resultSetFuture.addListener(() -> consumer.accept(entity), executor);
    }

    @Override
    public ColumnFamilyEntity update(ColumnFamilyEntity entity) {
        return save(entity);
    }

    @Override
    public void updateAsync(ColumnFamilyEntity entity) {
        saveAsync(entity);
    }

    @Override
    public void updateAsync(ColumnFamilyEntity entity, Consumer<ColumnFamilyEntity> consumer) {
        saveAsync(entity, consumer);
    }

    @Override
    public void delete(ColumnQuery query) {
        BuiltStatement delete = QueryUtils.delete(query, keyspace);
        session.execute(delete);

    }

    @Override
    public void deleteAsync(ColumnQuery query) {
        BuiltStatement delete = QueryUtils.delete(query, keyspace);
        session.executeAsync(delete);
    }

    @Override
    public void deleteAsync(ColumnQuery query, Consumer<Void> consumer) {
        BuiltStatement delete = QueryUtils.delete(query, keyspace);
        ResultSetFuture resultSetFuture = session.executeAsync(delete);
        resultSetFuture.addListener(() -> consumer.accept(null), executor);
    }

    @Override
    public List<ColumnFamilyEntity> find(ColumnQuery query) {
        BuiltStatement select = QueryUtils.add(query, keyspace);
        ResultSet resultSet = session.execute(select);
        return resultSet.all().stream().map(row -> CassandraConverter.toDocumentEntity(row))
                .collect(Collectors.toList());
    }



    @Override
    public void findAsync(ColumnQuery query, Consumer<List<ColumnFamilyEntity>> consumer)
            throws ExecuteAsyncQueryException, UnsupportedOperationException {
        BuiltStatement select = QueryUtils.add(query, keyspace);
        ResultSetFuture resultSet = session.executeAsync(select);
        CassandraReturnQueryAsync executeAsync = new CassandraReturnQueryAsync(resultSet, consumer);
        resultSet.addListener(executeAsync, executor);
    }

    @Override
    public List<ColumnFamilyEntity> nativeQuery(String query) {
        ResultSet resultSet = session.execute(query);
        return resultSet.all().stream().map(row -> CassandraConverter.toDocumentEntity(row))
                .collect(Collectors.toList());
    }

    @Override
    public void nativeQueryAsync(String query, Consumer<List<ColumnFamilyEntity>> consumer)
            throws ExecuteAsyncQueryException {
        ResultSetFuture resultSet = session.executeAsync(query);
        CassandraReturnQueryAsync executeAsync = new CassandraReturnQueryAsync(resultSet, consumer);
        resultSet.addListener(executeAsync, executor);
    }

    @Override
    public PreparedStatement nativeQueryPrepare(String query) {
        com.datastax.driver.core.PreparedStatement prepare = session.prepare(query);
        return new CassandraPrepareStatment(prepare, executor, session);
    }

    @Override
    public void close()  {
        session.close();
    }

    Session getSession() {
        return session;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CassandraDocumentEntityManager{");
        sb.append("session=").append(session);
        sb.append('}');
        return sb.toString();
    }
}