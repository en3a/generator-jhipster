<%#
 Copyright 2013-2018 the original author or authors from the JHipster project.

 This file is part of the JHipster project, see http://www.jhipster.tech/
 for more information.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
-%>
package <%=packageName%>.repository;

import <%=packageName%>.domain.<%=entityClass%>;
import org.springframework.stereotype.Repository;
<% if (databaseType === 'cassandra') { %>
import com.datastax.driver.core.*;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;<% } %><% if (databaseType === 'sql') { %>
import org.springframework.data.jpa.repository.*;<% if (fieldsContainOwnerManyToMany==true) { %>
import org.springframework.data.repository.query.Param;<% } %>
<%_ let importList = fieldsContainOwnerManyToMany;
    for (r of relationships) {
        if (r.relationshipType === 'many-to-one' && r.otherEntityName === 'user') {
            importList = true;
        }
    }
    if (importList === true) {
_%>
import java.util.List;<% }} %><% if (databaseType === 'mongodb') { %>
import org.springframework.data.mongodb.repository.MongoRepository;<% } %><% if (databaseType === 'cassandra') { %>

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
<% if (fieldsContainInstant === true) { %>
import java.time.Instant;<% } %><% if (fieldsContainLocalDate === true) { %>
import java.time.LocalDate;<% } %><% if (fieldsContainZonedDateTime === true) { %>
import java.time.ZonedDateTime;<% } %>
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;<% } %>

<%_ if (databaseType === 'sql') { _%>
/**
 * Spring Data JPA repository for the <%= entityClass %> entity.
 */
<%_ } if (databaseType === 'mongodb') { _%>
/**
 * Spring Data MongoDB repository for the <%= entityClass %> entity.
 */
<%_ } if (databaseType === 'couchbase') { _%>
/**
 * Spring Data Couchbase repository for the <%= entityClass %> entity.
 */
<%_ } if (databaseType === 'cassandra') { _%>
/**
 * Cassandra repository for the <%= entityClass %> entity.
 */
<%_ } if (databaseType === 'sql' || databaseType === 'mongodb' || databaseType === 'couchbase') { _%>
@SuppressWarnings("unused")
@Repository
public interface <%=entityClass%>Repository extends <% if (databaseType === 'sql') { %>JpaRepository<% } %><% if (databaseType === 'mongodb') { %>MongoRepository<% } %><% if (databaseType === 'couchbase') { %>N1qlCouchbaseRepository<% } %><<%=entityClass%>, <%= pkType %>><% if (jpaMetamodelFiltering) { %>, JpaSpecificationExecutor<<%=entityClass%>><% } %> {
    <%_ for (idx in relationships) {
        if (relationships[idx].relationshipType === 'many-to-one' && relationships[idx].otherEntityName === 'user') { _%>

    @Query("select <%= entityTableName %> from <%= entityClass %> <%= entityTableName %> where <%= entityTableName %>.<%= relationships[idx].relationshipFieldName %>.login = ?#{principal.username}")
    List<<%= entityClass %>> findBy<%= relationships[idx].relationshipNameCapitalized %>IsCurrentUser();
    <%_ } } _%>
    <%_ if (fieldsContainOwnerManyToMany === true) { _%>
    @Query("select distinct <%= entityTableName %> from <%= entityClass %> <%= entityTableName %><% for (idx in relationships) {
    if (relationships[idx].relationshipType === 'many-to-many' && relationships[idx].ownerSide === true) { %> left join fetch <%=entityTableName%>.<%=relationships[idx].relationshipFieldNamePlural%><%}??}%>")
    List<<%=entityClass%>> findAllWithEagerRelationships();

    @Query("select <%= entityTableName %> from <%= entityClass %> <%= entityTableName %><% for (idx in relationships) {
    if (relationships[idx].relationshipType === 'many-to-many' && relationships[idx].ownerSide === true) { %> left join fetch <%=entityTableName%>.<%=relationships[idx].relationshipFieldNamePlural%><%}??}%> where <%=entityTableName%>.id =:id")
    <%=entityClass%> findOneWithEagerRelationships(@Param("id") Long id);
    <%_ } _%>

}
<%_ } if (databaseType === 'cassandra') { _%>
@Repository
public class <%= entityClass %>Repository {

    private final Session session;

    private final Validator validator;

    private Mapper<<%= entityClass %>> mapper;

    private PreparedStatement findAllStmt;

    private PreparedStatement truncateStmt;

    public <%= entityClass %>Repository(Session session, Validator validator) {
        this.session = session;
        this.validator = validator;
        this.mapper = new MappingManager(session).mapper(<%= entityClass %>.class);
        this.findAllStmt = session.prepare("SELECT * FROM <%= entityInstance %>");
        this.truncateStmt = session.prepare("TRUNCATE <%= entityInstance %>");
    }

    public List<<%= entityClass %>> findAll() {
        List<<%= entityClass %>> <%= entityInstancePlural %>List = new ArrayList<>();
        BoundStatement stmt = findAllStmt.bind();
        session.execute(stmt).all().stream().map(
            row -> {
                <%= entityClass %> <%= entityInstance %> = new <%= entityClass %>();
                <%= entityInstance %>.setId(row.getUUID("id"));<%
                for (idx in fields) {
                    const fieldInJavaBeanMethod = fields[idx].fieldInJavaBeanMethod;
                    const fieldName = fields[idx].fieldName;
                    const fieldNameUnderscored = fields[idx].fieldNameUnderscored;
                    if (fields[idx].fieldType === 'Integer') { %>
                <%= entityInstance %>.set<%= fieldInJavaBeanMethod %>(row.getInt("<%= fieldName %>"));<% } else if (fields[idx].fieldType === 'BigDecimal') { %>
                <%= entityInstance %>.set<%= fieldInJavaBeanMethod %>(row.getDecimal("<%= fieldName %>"));<% } else if (fields[idx].fieldType === 'LocalDate') { %>
                <%= entityInstance %>.set<%= fieldInJavaBeanMethod %>(row.get("<%= fieldName %>", LocalDate.class));<% } else if (['Instant', 'ZonedDateTime'].includes(fields[idx].fieldType)) { %>
                <%= entityInstance %>.set<%= fieldInJavaBeanMethod %>(row.get("<%= fieldName %>", <%= fields[idx].fieldType %>.class));<% } else if (fields[idx].fieldType === 'Boolean') { %>
                <%= entityInstance %>.set<%= fieldInJavaBeanMethod %>(row.getBool("<%= fieldName %>"));<% } else if (fields[idx].fieldType === 'Text') { %>
                <%= entityInstance %>.set<%= fieldInJavaBeanMethod %>(row.getString("<%= fieldName %>"));<% } else if (fields[idx].fieldType === 'ByteBuffer') { %>
                <%= entityInstance %>.set<%= fieldInJavaBeanMethod %>(row.getBytes("<%= fieldName %>"));
                <%_ if (fields[idx].fieldTypeBlobContent !== 'text') { _%>
                <%= entityInstance %>.set<%= fieldInJavaBeanMethod %>ContentType(row.getString("<%= fieldNameUnderscored %>_content_type"));
                <%_ } _%><% } else { %>
                <%= entityInstance %>.set<%= fieldInJavaBeanMethod %>(row.get<%= fields[idx].fieldType %>("<%= fieldName %>"));<% } } %>
                return <%= entityInstance %>;
            }
        ).forEach(<%= entityInstancePlural %>List::add);
        return <%= entityInstancePlural %>List;
    }

    public <%= entityClass %> findOne(UUID id) {
        return mapper.get(id);
    }

    public <%= entityClass %> save(<%= entityClass %> <%= entityInstance %>) {
        if (<%= entityInstance %>.getId() == null) {
            <%= entityInstance %>.setId(UUID.randomUUID());
        }
        Set<ConstraintViolation<<%= entityClass %>>> violations = validator.validate(<%= entityInstance %>);
        if (violations != null && !violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
        mapper.save(<%= entityInstance %>);
        return <%= entityInstance %>;
    }

    public void delete(UUID id) {
        mapper.delete(id);
    }

    public void deleteAll() {
        BoundStatement stmt = truncateStmt.bind();
        session.execute(stmt);
    }
}
<%_ } _%>
