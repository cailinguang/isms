package com.vw.isms.standard.repository;

import com.vw.isms.ModelException;
import com.vw.isms.RepositoryException;
import com.vw.isms.property.BooleanProperty;
import com.vw.isms.property.EnumProperty;
import com.vw.isms.property.EvidenceSetProperty;
import com.vw.isms.property.FloatProperty;
import com.vw.isms.property.Property;
import com.vw.isms.property.StringProperty;
import com.vw.isms.standard.model.*;
import com.vw.isms.util.IdUtil;
import java.io.PrintStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Transactional
public class JdbcStandardRepository
        implements StandardRepository
{
    private final NamedParameterJdbcTemplate namedTemplate;
    private final JdbcTemplate jdbcTemplate;
    private String uploadDir;
    private static final String SCHEMA = "APP";

    public JdbcStandardRepository(JdbcTemplate template)
    {
        this.namedTemplate = new NamedParameterJdbcTemplate(template);
        this.jdbcTemplate = template;
    }

    public void createStandard(Standard standard)
            throws RepositoryException
    {
        try
        {
            SimpleJdbcInsertion insertion = new SimpleJdbcInsertion();
            insertion.withSchema("APP").withTable("ISMS_STANDARD")
                    .withColumnValue("STANDARD_ID", Long.valueOf(standard.getStandardId()))
                    .withColumnValue("STANDARD_TYPE", standard.getStandardType().getType())
                    .withColumnValue("IS_EVALUATION", Integer.valueOf(standard.isEvaluation() ? 1 : 0))
                    .withColumnValue("NAME", StringProperty.getProperty(standard, "name").getValue())
                    .withColumnValue("DESCRIPTION",
                            StringProperty.getProperty(standard, "description").getValue())
                    .withColumnValue("ARCHIVED", Integer.valueOf(0));
            insertion.insert(this.jdbcTemplate);
            List<StandardNode> nodes = new ArrayList();
            collectStandardNodes(standard.getChildren(), nodes);
            batchCreateStandardNodes(nodes);

            Map<Long, StandardNode> propertyIdToNode = new HashMap();

            List<BooleanProperty> booleans = new ArrayList();
            collectProperties(standard.getChildren(), booleans, BooleanProperty.class, propertyIdToNode);
            batchCreateBooleanProperties(booleans, propertyIdToNode);

            List<EnumProperty> enums = new ArrayList();
            collectProperties(standard.getChildren(), enums, EnumProperty.class, propertyIdToNode);
            batchCreateEnumProperties(enums, propertyIdToNode);

            List<FloatProperty> floats = new ArrayList();
            collectProperties(standard.getChildren(), floats, FloatProperty.class, propertyIdToNode);
            batchCreateFloatProperties(floats, propertyIdToNode);

            List<StringProperty> strings = new ArrayList();
            collectProperties(standard.getChildren(), strings, StringProperty.class, propertyIdToNode);
            batchCreateStringProperties(strings, propertyIdToNode);

            List<EvidenceSetProperty> evidences = new ArrayList();
            collectProperties(standard.getChildren(), evidences, EvidenceSetProperty.class, propertyIdToNode);

            batchCreateEvidenceProperties(evidences, propertyIdToNode);
        }
        catch (Throwable t)
        {
            throw new RepositoryException(t.getMessage());
        }
    }

    private void batchCreateEvidenceProperties(final List<EvidenceSetProperty> props, final Map<Long, StandardNode> propertyIdToNode)
    {
        String sql = "INSERT INTO APP.ISMS_EVIDENCE_PROPERTY (PROPERTY_ID, NODE_ID, NAME, READONLY, EVIDENCE_ID, EVIDENCE_NAME, EVIDENCE_DESCRIPTION, EVIDENCE_PATH, EVIDENCE_CONTENT_TYPE, STANDARD_ID) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";


        int numEvidences = 0;
        for (EvidenceSetProperty prop : props) {
            numEvidences += Math.max(1, prop.getValue().size());
        }
        final int batchSize = numEvidences;
        this.jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter()
        {
            public void setValues(PreparedStatement ps, int i)
                    throws SQLException
            {
                EvidenceSetProperty prop = (EvidenceSetProperty)props.get(i);
                StandardNode node = (StandardNode)propertyIdToNode.get(Long.valueOf(prop.getId()));
                if (prop.getValue().isEmpty())
                {
                    ps.setLong(1, IdUtil.next());
                    ps.setLong(2, node.getNodeId());
                    ps.setString(3, prop.getName());
                    ps.setInt(4, prop.isReadonly() ? 1 : 0);
                    ps.setLong(5, 0L);
                    ps.setString(6, "NA");
                    ps.setString(7, "NA");
                    ps.setString(8, "NA");
                    ps.setString(9, "NA");
                    ps.setLong(10, node.getStandard().getStandardId());
                }
                else
                {
                    for (Evidence ev : prop.getValue())
                    {
                        ps.setLong(1, IdUtil.next());
                        ps.setLong(2, node.getNodeId());
                        ps.setString(3, prop.getName());
                        ps.setInt(4, prop.isReadonly() ? 1 : 0);
                        ps.setLong(5, ev.getId());
                        ps.setString(6, ev.getName());
                        ps.setString(7, ev.getDescription());
                        ps.setString(8, ev.getPath());
                        ps.setString(9, ev.getContentType());
                        ps.setLong(10, node.getStandard().getStandardId());
                    }
                }
            }

            public int getBatchSize()
            {
                return batchSize;
            }
        });
    }

    private void batchCreateStringProperties(final List<StringProperty> props, final Map<Long, StandardNode> propertyIdToNode)
    {
        String sql = "INSERT INTO APP.ISMS_STRING_PROPERTY (PROPERTY_ID, NODE_ID, NAME, READONLY, VALUE, STANDARD_ID) VALUES (?, ?, ?, ?, ?, ?)";

        this.jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter()
        {
            public void setValues(PreparedStatement ps, int i)
                    throws SQLException
            {
                StringProperty prop = (StringProperty)props.get(i);
                StandardNode node = (StandardNode)propertyIdToNode.get(Long.valueOf(prop.getId()));
                ps.setLong(1, prop.getId());
                ps.setLong(2, node.getNodeId());
                ps.setString(3, prop.getName());
                ps.setInt(4, prop.isReadonly() ? 1 : 0);
                ps.setString(5, prop.getValue());
                ps.setLong(6, node.getStandard().getStandardId());
            }

            public int getBatchSize()
            {
                return props.size();
            }
        });
    }

    private void batchCreateFloatProperties(final List<FloatProperty> props, final Map<Long, StandardNode> propertyIdToNode)
    {
        String sql = "INSERT INTO APP.ISMS_FLOAT_PROPERTY (PROPERTY_ID, NODE_ID, NAME, READONLY, VALUE, STANDARD_ID) VALUES (?, ?, ?, ?, ?, ?)";

        this.jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter()
        {
            public void setValues(PreparedStatement ps, int i)
                    throws SQLException
            {
                FloatProperty prop = (FloatProperty)props.get(i);
                StandardNode node = (StandardNode)propertyIdToNode.get(Long.valueOf(prop.getId()));
                ps.setLong(1, prop.getId());
                ps.setLong(2, node.getNodeId());
                ps.setString(3, prop.getName());
                ps.setInt(4, prop.isReadonly() ? 1 : 0);
                ps.setDouble(5, prop.getValue());
                ps.setLong(6, node.getStandard().getStandardId());
            }

            public int getBatchSize()
            {
                return props.size();
            }
        });
    }

    private void batchCreateEnumProperties(final List<EnumProperty> props, final Map<Long, StandardNode> propertyIdToNode)
    {
        String sql = "INSERT INTO APP.ISMS_ENUM_PROPERTY (PROPERTY_ID, NODE_ID, NAME, READONLY, VALUE, ENUM_TYPE, STANDARD_ID) VALUES (?, ?, ?, ?, ?, ?, ?)";

        this.jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter()
        {
            public void setValues(PreparedStatement ps, int i)
                    throws SQLException
            {
                EnumProperty prop = (EnumProperty)props.get(i);
                StandardNode node = (StandardNode)propertyIdToNode.get(Long.valueOf(prop.getId()));
                ps.setLong(1, prop.getId());
                ps.setLong(2, node.getNodeId());
                ps.setString(3, prop.getName());
                ps.setInt(4, prop.isReadonly() ? 1 : 0);
                ps.setString(5, prop.getValue());
                ps.setString(6, prop.getType());
                ps.setLong(7, node.getStandard().getStandardId());
            }

            public int getBatchSize()
            {
                return props.size();
            }
        });
    }

    private void batchCreateBooleanProperties(final List<BooleanProperty> props, final Map<Long, StandardNode> propertyIdToNode)
    {
        String sql = "INSERT INTO APP.ISMS_BOOLEAN_PROPERTY (PROPERTY_ID, NODE_ID, NAME, READONLY, VALUE, STANDARD_ID) VALUES (?, ?, ?, ?, ?, ?)";

        this.jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter()
        {
            public void setValues(PreparedStatement ps, int i)
                    throws SQLException
            {
                BooleanProperty prop = (BooleanProperty)props.get(i);
                StandardNode node = (StandardNode)propertyIdToNode.get(Long.valueOf(prop.getId()));
                ps.setLong(1, prop.getId());
                ps.setLong(2, node.getNodeId());
                ps.setString(3, prop.getName());
                ps.setInt(4, prop.isReadonly() ? 1 : 0);
                ps.setInt(5, prop.getValue() ? 1 : 0);
                ps.setLong(6, node.getStandard().getStandardId());
            }

            public int getBatchSize()
            {
                return props.size();
            }
        });
    }

    private void batchCreateStandardNodes(final List<StandardNode> nodes)
    {
        String sql = "INSERT INTO APP.ISMS_STANDARD_NODE (NODE_ID, STANDARD_ID, NODE_TYPE, NAME, NODE_POSITION, PARENT_NODE_ID) VALUES (?, ?, ?, ?, ?, ?)";

        this.jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter()
        {
            public void setValues(PreparedStatement ps, int i)
                    throws SQLException
            {
                StandardNode node = (StandardNode)nodes.get(i);
                ps.setLong(1, node.getNodeId());
                ps.setLong(2, node.getStandard().getStandardId());
                ps.setString(3, node.getNodeType().getNodeType());
                try
                {
                    ps.setString(4, node.getName());
                }
                catch (ModelException e)
                {
                    throw new SQLException(e);
                }
                ps.setInt(5, node.getNodePosition());
                ps.setLong(6, node.getParentId());
            }

            public int getBatchSize()
            {
                return nodes.size();
            }
        });
    }

    private <T extends Property> void collectProperties(List<StandardNode> children, List<T> props, Class<T> clazz, Map<Long, StandardNode> propertyIdToNode)
    {
        for (StandardNode node : children)
        {
            for (Property prop : node.getProperties().values()) {
                if (clazz.isAssignableFrom(prop.getClass()))
                {
                    props.add((T)prop);
                    propertyIdToNode.put(Long.valueOf(prop.getId()), node);
                }
            }
            collectProperties(node.getChildren(), props, clazz, propertyIdToNode);
        }
    }

    private void collectStandardNodes(List<StandardNode> children, List<StandardNode> nodes)
    {
        for (StandardNode node : children)
        {
            nodes.add(node);
            collectStandardNodes(node.getChildren(), nodes);
        }
    }

    public void updateStandardWithoutChildren(Standard standard)
            throws RepositoryException
    {
        try
        {
            SimpleJdbcUpdate update = new SimpleJdbcUpdate();
            update.withSchema("APP").withTable("ISMS_STANDARD")
                    .withKey("STANDARD_ID", Long.valueOf(standard.getStandardId()))
                    .withColumnValue("NAME", StringProperty.getProperty(standard, "name").getValue())
                    .withColumnValue("DESCRIPTION",
                            StringProperty.getProperty(standard, "description").getValue())
                    .withColumnValue("ARCHIVED", Integer.valueOf(standard.isArchived() ? 1 : 0));
            update.update(this.namedTemplate);
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            throw new RepositoryException(t.getMessage());
        }
    }

    public Standard getStandardWithoutNodes(long standardId)
            throws RepositoryException
    {
        SimpleJdbcQuery<Standard> query = new SimpleJdbcQuery()
        {
            public Standard mapRow(ResultSet rs, int rowNum)
                    throws SQLException
            {
                String name = rs.getString("NAME");
                String description = rs.getString("DESCRIPTION");
                String standardType = rs.getString("STANDARD_TYPE");
                long standardId = rs.getLong("STANDARD_ID");
                boolean isEvaluation = rs.getInt("IS_EVALUATION") != 0;
                boolean archived = rs.getInt("ARCHIVED") != 0;
                try
                {
                    Standard standard = new Standard(standardId, StandardType.getInstance(standardType), isEvaluation);
                    standard.setArchived(archived);
                    StringProperty.getProperty(standard, "name").setValue(name);
                    StringProperty.getProperty(standard, "description").setValue(description);
                    return standard;
                }
                catch (ModelException e)
                {
                    throw new SQLException(e);
                }
            }
        };
        try
        {
            query.withSchema("APP").withTable("ISMS_STANDARD").withKey("STANDARD_ID", Long.valueOf(standardId)).withColumn("*");
            return (Standard)query.queryForObject(this.namedTemplate);
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            throw new RepositoryException(t.getMessage());
        }
    }

    private Standard getStandardHavingProperties(long standardId, boolean loadProperties, Map<Long, StandardNode> indexed)
            throws RepositoryException
    {
        try
        {
            Standard standard = getStandardWithoutNodes(standardId);
            SimpleJdbcQuery<StandardNode> query = createQueryForStandardNodes(standard);
            query.withSchema("APP").withTable("ISMS_STANDARD_NODE").withKey("STANDARD_ID", Long.valueOf(standardId))
                    .withColumn("*");
            long startTime = System.currentTimeMillis();
            List<StandardNode> nodes = query.query(this.namedTemplate);
            long endTime = System.currentTimeMillis();
            System.out.println("Load " + nodes.size() + " Nodes: " + (endTime - startTime));
            startTime = System.currentTimeMillis();
            for (StandardNode node : nodes) {
                indexed.put(Long.valueOf(node.getNodeId()), node);
            }
            loadPropertiesForStandard(standardId, indexed);
            endTime = System.currentTimeMillis();
            System.out.println("Load Properties: " + (endTime - startTime));
            for (StandardNode node : nodes) {
                if (node.getParentId() == 0L) {
                    standard.addChild(node);
                } else if (indexed.containsKey(Long.valueOf(node.getParentId()))) {
                    ((StandardNode)indexed.get(Long.valueOf(node.getParentId()))).addChild(node);
                }
            }
            return standard;
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            throw new RepositoryException(t.getMessage());
        }
    }

    public Standard getStandard(long standardId)
            throws RepositoryException
    {
        Map<Long, StandardNode> indexed = new HashMap();
        return getStandardHavingProperties(standardId, true, indexed);
    }

    private void loadPropertiesForNode(StandardNode node)
            throws RepositoryException
    {
        loadBooleanProperties(node);
        loadEnumProperties(node);
        loadEvidenceSetProperties(node);
        loadFloatProperties(node);
        loadStringProperties(node);
    }

    private void loadPropertiesForStandard(long standardId, Map<Long, StandardNode> indexed)
            throws RepositoryException
    {
        loadBooleanPropertiesForStandard(standardId, indexed);
        loadEnumPropertiesForStandard(standardId, indexed);
        loadEvidenceSetPropertiesForStandard(standardId, indexed);
        loadFloatPropertiesForStandard(standardId, indexed);
        loadStringPropertiesForStandard(standardId, indexed);
    }

    private void loadStringProperties(StandardNode node)
            throws RepositoryException
    {
        SimpleJdbcQuery<StringProperty> query = new SimpleJdbcQuery()
        {
            public StringProperty mapRow(ResultSet rs, int rowNum)
                    throws SQLException
            {
                String value = rs.getString("VALUE");
                boolean readonly = rs.getInt("READONLY") != 0;
                String name = rs.getString("NAME");
                long propertyId = rs.getLong("PROPERTY_ID");
                StringProperty prop = new StringProperty(propertyId, name, readonly);
                prop.setValue(value);
                return prop;
            }
        };query.withSchema("APP").withTable("ISMS_STRING_PROPERTY").withKey("NODE_ID", Long.valueOf(node.getNodeId()))
            .withColumn("*");
        List<StringProperty> props = query.query(this.namedTemplate);
        for (Property prop : props) {
            node.addProperty(prop);
        }
    }

    private void loadFloatProperties(StandardNode node)
            throws RepositoryException
    {
        SimpleJdbcQuery<FloatProperty> query = new SimpleJdbcQuery()
        {
            public FloatProperty mapRow(ResultSet rs, int rowNum)
                    throws SQLException
            {
                double value = rs.getDouble("VALUE");
                boolean readonly = rs.getInt("READONLY") != 0;
                String name = rs.getString("NAME");
                long propertyId = rs.getLong("PROPERTY_ID");
                FloatProperty prop = new FloatProperty(propertyId, name, readonly);
                prop.setValue(value);
                return prop;
            }
        };query.withSchema("APP").withTable("ISMS_FLOAT_PROPERTY").withKey("NODE_ID", Long.valueOf(node.getNodeId()))
            .withColumn("*");
        List<FloatProperty> props = query.query(this.namedTemplate);
        for (Property prop : props) {
            node.addProperty(prop);
        }
    }

    private void loadEvidenceSetProperties(StandardNode node)
            throws RepositoryException
    {
        SimpleJdbcQuery<EvidenceSetProperty> query = new SimpleJdbcQuery()
        {
            public EvidenceSetProperty mapRow(ResultSet rs, int rowNum)
                    throws SQLException
            {
                long evidenceId = rs.getLong("EVIDENCE_ID");
                String evidenceName = rs.getString("EVIDENCE_NAME");
                String description = rs.getString("EVIDENCE_DESCRIPTION");
                String path = rs.getString("EVIDENCE_PATH");
                String contentType = rs.getString("EVIDENCE_CONTENT_TYPE");
                Evidence evidence = new Evidence();
                evidence.setId(evidenceId);
                evidence.setName(evidenceName);
                evidence.setDescription(description);
                evidence.setPath(path);
                evidence.setContentType(contentType);
                boolean readonly = rs.getInt("READONLY") != 0;
                long propertyId = rs.getLong("PROPERTY_ID");
                String name = rs.getString("NAME");

                EvidenceSetProperty prop = new EvidenceSetProperty(propertyId, name, readonly, Collections.singletonList(evidence));
                return prop;
            }
        };query.withSchema("APP").withTable("ISMS_EVIDENCE_PROPERTY")
            .withKey("NODE_ID", Long.valueOf(node.getNodeId())).withColumn("*");
        List<EvidenceSetProperty> props = query.query(this.namedTemplate);
        if (!props.isEmpty())
        {
            EvidenceSetProperty template = (EvidenceSetProperty)props.get(0);
            List<Evidence> evidences = new ArrayList();
            for (EvidenceSetProperty prop : props)
            {
                boolean skip = false;
                for (Evidence ev : prop.getValue()) {
                    if (ev.getId() == 0L) {
                        skip = true;
                    }
                }
                if (!skip) {
                    evidences.addAll(prop.getValue());
                }
            }
            EvidenceSetProperty merged = new EvidenceSetProperty(template.getId(), template.getName(), template.isReadonly(), evidences);
            node.addProperty(merged);
        }
    }

    private void loadEnumProperties(StandardNode node)
            throws RepositoryException
    {
        SimpleJdbcQuery<EnumProperty> query = new SimpleJdbcQuery()
        {
            public EnumProperty mapRow(ResultSet rs, int rowNum)
                    throws SQLException
            {
                String value = rs.getString("VALUE");
                String enumType = rs.getString("ENUM_TYPE");
                boolean readonly = rs.getInt("READONLY") != 0;
                String name = rs.getString("NAME");
                long propertyId = rs.getLong("PROPERTY_ID");
                try
                {
                    EnumProperty prop = EnumProperty.newProperty(enumType, propertyId, name, readonly);
                    prop.setValue(value);
                    return prop;
                }
                catch (ModelException e)
                {
                    throw new SQLException(e);
                }
            }
        };query.withSchema("APP").withTable("ISMS_ENUM_PROPERTY").withKey("NODE_ID", Long.valueOf(node.getNodeId()))
            .withColumn("*");
        List<EnumProperty> props = query.query(this.namedTemplate);
        for (EnumProperty prop : props) {
            node.addProperty(prop);
        }
    }

    private void loadBooleanProperties(StandardNode node)
            throws RepositoryException
    {
        SimpleJdbcQuery<BooleanProperty> query = new SimpleJdbcQuery()
        {
            public BooleanProperty mapRow(ResultSet rs, int rowNum)
                    throws SQLException
            {
                boolean value = rs.getInt("VALUE") != 0;
                boolean readonly = rs.getInt("READONLY") != 0;
                String name = rs.getString("NAME");
                long propertyId = rs.getLong("PROPERTY_ID");
                BooleanProperty prop = new BooleanProperty(propertyId, name, readonly);
                prop.setValue(value);
                return prop;
            }
        };query.withSchema("APP").withTable("ISMS_BOOLEAN_PROPERTY").withKey("NODE_ID", Long.valueOf(node.getNodeId()))
            .withColumn("*");
        List<BooleanProperty> props = query.query(this.namedTemplate);
        for (Property prop : props) {
            node.addProperty(prop);
        }
    }

    private void loadStringPropertiesForStandard(long standardId, final Map<Long, StandardNode> indexed)
            throws RepositoryException
    {
        SimpleJdbcQuery<StringProperty> query = new SimpleJdbcQuery()
        {
            public StringProperty mapRow(ResultSet rs, int rowNum)
                    throws SQLException
            {
                StandardNode node = (StandardNode)indexed.get(Long.valueOf(rs.getLong("NODE_ID")));
                if (node == null) {
                    return null;
                }
                String value = rs.getString("VALUE");
                boolean readonly = rs.getInt("READONLY") != 0;
                String name = rs.getString("NAME");
                long propertyId = rs.getLong("PROPERTY_ID");
                StringProperty prop = new StringProperty(propertyId, name, readonly);
                prop.setValue(value);
                node.addProperty(prop);
                return prop;
            }
        };query.withSchema("APP").withTable("ISMS_STRING_PROPERTY").withKey("STANDARD_ID", Long.valueOf(standardId))
            .withColumn("*");
        query.query(this.namedTemplate);
    }

    private void loadFloatPropertiesForStandard(long standardId, final Map<Long, StandardNode> indexed)
            throws RepositoryException
    {
        SimpleJdbcQuery<FloatProperty> query = new SimpleJdbcQuery()
        {
            public FloatProperty mapRow(ResultSet rs, int rowNum)
                    throws SQLException
            {
                StandardNode node = (StandardNode)indexed.get(Long.valueOf(rs.getLong("NODE_ID")));
                if (node == null) {
                    return null;
                }
                double value = rs.getDouble("VALUE");
                boolean readonly = rs.getInt("READONLY") != 0;
                String name = rs.getString("NAME");
                long propertyId = rs.getLong("PROPERTY_ID");
                FloatProperty prop = new FloatProperty(propertyId, name, readonly);
                prop.setValue(value);
                node.addProperty(prop);
                return prop;
            }
        };query.withSchema("APP").withTable("ISMS_FLOAT_PROPERTY").withKey("STANDARD_ID", Long.valueOf(standardId))
            .withColumn("*");
        query.query(this.namedTemplate);
    }

    private void loadEvidenceSetPropertiesForStandard(long standardId, final Map<Long, StandardNode> indexed)
            throws RepositoryException
    {
        final Map<Long, List<EvidenceSetProperty>> results = new HashMap();
        SimpleJdbcQuery<EvidenceSetProperty> query = new SimpleJdbcQuery()
        {
            public EvidenceSetProperty mapRow(ResultSet rs, int rowNum)
                    throws SQLException
            {
                long nodeId = rs.getLong("NODE_ID");
                if (!indexed.containsKey(Long.valueOf(nodeId))) {
                    return null;
                }
                List<EvidenceSetProperty> props = (List)results.get(Long.valueOf(nodeId));
                if (props == null)
                {
                    props = new ArrayList();
                    results.put(Long.valueOf(nodeId), props);
                }
                long evidenceId = rs.getLong("EVIDENCE_ID");
                String evidenceName = rs.getString("EVIDENCE_NAME");
                String description = rs.getString("EVIDENCE_DESCRIPTION");
                String path = rs.getString("EVIDENCE_PATH");
                String contentType = rs.getString("EVIDENCE_CONTENT_TYPE");
                Evidence evidence = new Evidence();
                evidence.setId(evidenceId);
                evidence.setName(evidenceName);
                evidence.setDescription(description);
                evidence.setPath(path);
                evidence.setContentType(contentType);
                boolean readonly = rs.getInt("READONLY") != 0;
                long propertyId = rs.getLong("PROPERTY_ID");
                String name = rs.getString("NAME");

                EvidenceSetProperty prop = new EvidenceSetProperty(propertyId, name, readonly, Collections.singletonList(evidence));
                props.add(prop);
                return prop;
            }
        };query.withSchema("APP").withTable("ISMS_EVIDENCE_PROPERTY").withKey("STANDARD_ID", Long.valueOf(standardId))
            .withColumn("*");
        query.query(this.namedTemplate);
        for (Map.Entry<Long, List<EvidenceSetProperty>> entry : results.entrySet())
        {
            StandardNode node = (StandardNode)indexed.get(entry.getKey());
            if (node != null)
            {
                List<EvidenceSetProperty> props = (List)entry.getValue();
                if (!props.isEmpty())
                {
                    EvidenceSetProperty template = (EvidenceSetProperty)props.get(0);
                    List<Evidence> evidences = new ArrayList();
                    for (EvidenceSetProperty prop : props)
                    {
                        boolean skip = false;
                        for (Evidence ev : prop.getValue()) {
                            if (ev.getId() == 0L) {
                                skip = true;
                            }
                        }
                        if (!skip) {
                            evidences.addAll(prop.getValue());
                        }
                    }
                    EvidenceSetProperty merged = new EvidenceSetProperty(template.getId(), template.getName(), template.isReadonly(), evidences);
                    node.addProperty(merged);
                }
            }
        }
    }

    private void loadEnumPropertiesForStandard(long standardId, final Map<Long, StandardNode> indexed)
            throws RepositoryException
    {
        SimpleJdbcQuery<EnumProperty> query = new SimpleJdbcQuery()
        {
            public EnumProperty mapRow(ResultSet rs, int rowNum)
                    throws SQLException
            {
                StandardNode node = (StandardNode)indexed.get(Long.valueOf(rs.getLong("NODE_ID")));
                if (node == null) {
                    return null;
                }
                String value = rs.getString("VALUE");
                String enumType = rs.getString("ENUM_TYPE");
                boolean readonly = rs.getInt("READONLY") != 0;
                String name = rs.getString("NAME");
                long propertyId = rs.getLong("PROPERTY_ID");
                try
                {
                    EnumProperty prop = EnumProperty.newProperty(enumType, propertyId, name, readonly);
                    prop.setValue(value);
                    node.addProperty(prop);
                    return prop;
                }
                catch (ModelException e)
                {
                    throw new SQLException(e);
                }
            }
        };query.withSchema("APP").withTable("ISMS_ENUM_PROPERTY").withKey("STANDARD_ID", Long.valueOf(standardId))
            .withColumn("*");
        query.query(this.namedTemplate);
    }

    private void loadBooleanPropertiesForStandard(long standardId, final Map<Long, StandardNode> indexed)
            throws RepositoryException
    {
        SimpleJdbcQuery<BooleanProperty> query = new SimpleJdbcQuery()
        {
            public BooleanProperty mapRow(ResultSet rs, int rowNum)
                    throws SQLException
            {
                StandardNode node = (StandardNode)indexed.get(Long.valueOf(rs.getLong("NODE_ID")));
                if (node == null) {
                    return null;
                }
                boolean value = rs.getInt("VALUE") != 0;
                boolean readonly = rs.getInt("READONLY") != 0;
                String name = rs.getString("NAME");
                long propertyId = rs.getLong("PROPERTY_ID");
                BooleanProperty prop = new BooleanProperty(propertyId, name, readonly);
                prop.setValue(value);
                node.addProperty(prop);
                return prop;
            }
        };query.withSchema("APP").withTable("ISMS_BOOLEAN_PROPERTY").withKey("STANDARD_ID", Long.valueOf(standardId))
            .withColumn("*");
        query.query(this.namedTemplate);
    }

    private SimpleJdbcQuery<StandardNode> createQueryForStandardNodes(final Standard standard)
            throws RepositoryException
    {
        SimpleJdbcQuery<StandardNode> query = new SimpleJdbcQuery()
        {
            public StandardNode mapRow(ResultSet rs, int rowNum)
                    throws SQLException
            {
                long parentId = rs.getLong("PARENT_NODE_ID");
                long nodeId = rs.getLong("NODE_ID");
                String nodeType = rs.getString("NODE_TYPE");
                int nodePos = rs.getInt("NODE_POSITION");
                try
                {
                    StandardNode node = new StandardNode(standard, nodeId, standard.getStandardType().getNodeType(nodeType));
                    node.setParentId(parentId);
                    node.setNodePosition(nodePos);
                    return node;
                }
                catch (ModelException e)
                {
                    throw new SQLException(e);
                }
            }
        };
        return query;
    }

    public void deleteStandardAndChildren(long standardId)
            throws RepositoryException
    {
        try
        {
            SimpleJdbcDelete deleteStandard = new SimpleJdbcDelete();
            deleteStandard.withSchema("APP").withTable("ISMS_STANDARD").withKey("STANDARD_ID",
                    Long.valueOf(standardId));
            deleteStandard.delete(this.namedTemplate);
            SimpleJdbcDelete deleteNodes = new SimpleJdbcDelete();
            deleteNodes.withSchema("APP").withTable("ISMS_STANDARD_NODE").withKey("STANDARD_ID",
                    Long.valueOf(standardId));
            deleteNodes.delete(this.namedTemplate);
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            throw new RepositoryException(t.getMessage());
        }
    }

    public void createStandardNode(StandardNode node)
            throws RepositoryException
    {
        try
        {
            createStandardNodeWithoutChildren(node);
            for (StandardNode child : node.getChildren()) {
                createStandardNode(child);
            }
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            throw new RepositoryException(t.getMessage());
        }
    }

    private void createStandardNodeWithoutChildren(StandardNode node)
            throws RepositoryException
    {
        SimpleJdbcInsertion insertion = new SimpleJdbcInsertion();
        insertion.withSchema("APP").withTable("ISMS_STANDARD_NODE")
                .withColumnValue("STANDARD_ID", Long.valueOf(node.getStandard().getStandardId()))
                .withColumnValue("NODE_ID", Long.valueOf(node.getNodeId()))
                .withColumnValue("PARENT_NODE_ID", Long.valueOf(node.getParentId()))
                .withColumnValue("NODE_POSITION", Integer.valueOf(node.getNodePosition()))
                .withColumnValue("NODE_TYPE", node.getNodeType().getNodeType());
        insertion.insert(this.jdbcTemplate);
        node.createProperties(node, this);
    }

    public void updateStandardNodeWithoutChildren(StandardNode node)
            throws RepositoryException
    {
        try
        {
            SimpleJdbcUpdate update = new SimpleJdbcUpdate();
            update.withSchema("APP").withTable("ISMS_STANDARD_NODE").withKey("NODE_ID", Long.valueOf(node.getNodeId()))
                    .withColumnValue("PARENT_NODE_ID", Long.valueOf(node.getParentId()))
                    .withColumnValue("NODE_POSITION", Integer.valueOf(node.getNodePosition()));
            update.update(this.namedTemplate);
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            throw new RepositoryException(t.getMessage());
        }
    }

    public StandardNode getStandardNodeWithoutChildren(long standardId, long nodeId)
            throws RepositoryException
    {
        try
        {
            Standard standard = getStandardWithoutNodes(standardId);
            SimpleJdbcQuery<StandardNode> query = createQueryForStandardNodes(standard);
            query.withSchema("APP").withTable("ISMS_STANDARD_NODE").withKey("STANDARD_ID", Long.valueOf(standardId))
                    .withKey("NODE_ID", Long.valueOf(nodeId)).withColumn("*");
            StandardNode node = (StandardNode)query.queryForObject(this.namedTemplate);
            loadPropertiesForNode(node);
            return node;
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            throw new RepositoryException(t.getMessage());
        }
    }

    public StandardNode getStandardNode(long standardId, long nodeId)
            throws RepositoryException
    {
        try
        {
            Map<Long, StandardNode> indexed = new HashMap();
            getStandardHavingProperties(standardId, false, indexed);
            StandardNode node = (StandardNode)indexed.get(Long.valueOf(nodeId));
            loadPropertiesForStandard(standardId, indexed);
            return node;
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            throw new RepositoryException(t.getMessage());
        }
    }

    public void deleteNodeAndChildren(StandardNode node)
            throws RepositoryException
    {
        try
        {
            SimpleJdbcDelete delete = new SimpleJdbcDelete();
            delete.withSchema("APP").withTable("ISMS_STANDARD_NODE").withKey("NODE_ID",
                    Long.valueOf(node.getNodeId()));
            delete.delete(this.namedTemplate);
            for (StandardNode child : node.getChildren()) {
                deleteNodeAndChildren(child);
            }
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            throw new RepositoryException(t.getMessage());
        }
    }

    public void updateBooleanProperty(StandardNode node, BooleanProperty prop)
            throws RepositoryException
    {
        try
        {
            SimpleJdbcUpdate update = new SimpleJdbcUpdate();
            update.withSchema("APP").withTable("ISMS_BOOLEAN_PROPERTY")
                    .withKey("PROPERTY_ID", Long.valueOf(prop.getId())).withColumnValue("VALUE", Integer.valueOf(prop.getValue() ? 1 : 0));
            update.update(this.namedTemplate);
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            throw new RepositoryException(t.getMessage());
        }
    }

    public void updateEnumProperty(StandardNode node, EnumProperty prop)
            throws RepositoryException
    {
        try
        {
            SimpleJdbcUpdate update = new SimpleJdbcUpdate();
            update.withSchema("APP").withTable("ISMS_ENUM_PROPERTY").withKey("PROPERTY_ID", Long.valueOf(prop.getId()))
                    .withColumnValue("VALUE", prop.getValue());
            update.update(this.namedTemplate);
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            throw new RepositoryException(t.getMessage());
        }
    }

    public void updateEvidenceSetProperty(StandardNode node, EvidenceSetProperty prop)
            throws RepositoryException
    {
        try
        {
            SimpleJdbcDelete delete = new SimpleJdbcDelete();
            delete.withSchema("APP").withTable("ISMS_EVIDENCE_PROPERTY")
                    .withKey("NODE_ID", Long.valueOf(node.getNodeId())).withKey("NAME", prop.getName());
            delete.delete(this.namedTemplate);
            Collection<Evidence> all = new ArrayList();
            all.addAll(prop.getValue());
            if (all.isEmpty())
            {
                Evidence e = new Evidence();
                e.setId(0L);
                e.setName("NA");
                e.setDescription("NA");
                all.add(e);
            }
            for (Evidence ev : all)
            {
                SimpleJdbcInsertion insertion = new SimpleJdbcInsertion();
                insertion.withSchema("APP").withTable("ISMS_EVIDENCE_PROPERTY")
                        .withColumnValue("EVIDENCE_NAME", ev.getName())
                        .withColumnValue("EVIDENCE_DESCRIPTION", ev.getDescription())
                        .withColumnValue("EVIDENCE_ID", Long.valueOf(ev.getId()))
                        .withColumnValue("EVIDENCE_PATH", ev.getPath())
                        .withColumnValue("EVIDENCE_CONTENT_TYPE", ev.getContentType())
                        .withColumnValue("READONLY", Boolean.valueOf(prop.isReadonly()))
                        .withColumnValue("PROPERTY_ID", Long.valueOf(IdUtil.next())).withColumnValue("NAME", prop.getName())
                        .withColumnValue("NODE_ID", Long.valueOf(node.getNodeId()))
                        .withColumnValue("STANDARD_ID", Long.valueOf(node.getStandard().getStandardId()));
                insertion.insert(this.jdbcTemplate);
            }
        }
        catch (Throwable t)
        {
            Evidence e;
            t.printStackTrace();
            throw new RepositoryException(t.getMessage());
        }
    }

    public void updateFloatProperty(StandardNode node, FloatProperty prop)
            throws RepositoryException
    {
        try
        {
            SimpleJdbcUpdate update = new SimpleJdbcUpdate();
            update.withSchema("APP").withTable("ISMS_FLOAT_PROPERTY")
                    .withKey("PROPERTY_ID", Long.valueOf(prop.getId())).withColumnValue("VALUE", Double.valueOf(prop.getValue()));
            update.update(this.namedTemplate);
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            throw new RepositoryException(t.getMessage());
        }
    }

    public void updateStringProperty(StandardNode node, StringProperty prop)
            throws RepositoryException
    {
        try
        {
            SimpleJdbcUpdate update = new SimpleJdbcUpdate();
            update.withSchema("APP").withTable("ISMS_STRING_PROPERTY")
                    .withKey("PROPERTY_ID", Long.valueOf(prop.getId())).withColumnValue("VALUE", prop.getValue());
            update.update(this.namedTemplate);
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            throw new RepositoryException(t.getMessage());
        }
    }

    public void createBooleanProperty(StandardNode node, BooleanProperty prop)
            throws RepositoryException
    {
        try
        {
            SimpleJdbcInsertion insertion = new SimpleJdbcInsertion();
            insertion.withSchema("APP").withTable("ISMS_BOOLEAN_PROPERTY")
                    .withColumnValue("READONLY", Boolean.valueOf(prop.isReadonly()))
                    .withColumnValue("PROPERTY_ID", Long.valueOf(prop.getId()))
                    .withColumnValue("VALUE", Integer.valueOf(prop.getValue() ? 1 : 0)).withColumnValue("NAME", prop.getName())
                    .withColumnValue("NODE_ID", Long.valueOf(node.getNodeId()))
                    .withColumnValue("STANDARD_ID", Long.valueOf(node.getStandard().getStandardId()));
            insertion.insert(this.jdbcTemplate);
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            throw new RepositoryException(t.getMessage());
        }
    }

    public void createEnumProperty(StandardNode node, EnumProperty prop)
            throws RepositoryException
    {
        try
        {
            SimpleJdbcInsertion insertion = new SimpleJdbcInsertion();
            insertion.withSchema("APP").withTable("ISMS_ENUM_PROPERTY")
                    .withColumnValue("READONLY", Boolean.valueOf(prop.isReadonly()))
                    .withColumnValue("PROPERTY_ID", Long.valueOf(prop.getId())).withColumnValue("VALUE", prop.getValue())
                    .withColumnValue("NAME", prop.getName()).withColumnValue("ENUM_TYPE", prop.getType())
                    .withColumnValue("NODE_ID", Long.valueOf(node.getNodeId()))
                    .withColumnValue("STANDARD_ID", Long.valueOf(node.getStandard().getStandardId()));
            insertion.insert(this.jdbcTemplate);
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            throw new RepositoryException(t.getMessage());
        }
    }

    public void createEvidenceSetProperty(StandardNode node, EvidenceSetProperty prop)
            throws RepositoryException
    {
        updateEvidenceSetProperty(node, prop);
    }

    public void createFloatProperty(StandardNode node, FloatProperty prop)
            throws RepositoryException
    {
        try
        {
            SimpleJdbcInsertion insertion = new SimpleJdbcInsertion();
            insertion.withSchema("APP").withTable("ISMS_FLOAT_PROPERTY")
                    .withColumnValue("READONLY", Boolean.valueOf(prop.isReadonly()))
                    .withColumnValue("PROPERTY_ID", Long.valueOf(prop.getId())).withColumnValue("VALUE", Double.valueOf(prop.getValue()))
                    .withColumnValue("NAME", prop.getName()).withColumnValue("NODE_ID", Long.valueOf(node.getNodeId()))
                    .withColumnValue("STANDARD_ID", Long.valueOf(node.getStandard().getStandardId()));
            insertion.insert(this.jdbcTemplate);
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            throw new RepositoryException(t.getMessage());
        }
    }

    public void createStringProperty(StandardNode node, StringProperty prop)
            throws RepositoryException
    {
        try
        {
            SimpleJdbcInsertion insertion = new SimpleJdbcInsertion();
            insertion.withSchema("APP").withTable("ISMS_STRING_PROPERTY")
                    .withColumnValue("READONLY", Boolean.valueOf(prop.isReadonly()))
                    .withColumnValue("PROPERTY_ID", Long.valueOf(prop.getId())).withColumnValue("VALUE", prop.getValue())
                    .withColumnValue("NAME", prop.getName()).withColumnValue("NODE_ID", Long.valueOf(node.getNodeId()))
                    .withColumnValue("STANDARD_ID", Long.valueOf(node.getStandard().getStandardId()));
            insertion.insert(this.jdbcTemplate);
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            throw new RepositoryException(t.getMessage());
        }
    }

    public PagingResult<Standard> queryStandard(StandardSearchRequest search, boolean isEvaluation)
            throws RepositoryException
    {
        try
        {
            Map<String, Object> values = new HashMap();
            values.put("isEvaluation", Integer.valueOf(isEvaluation ? 1 : 0));
            values.put("archived", Integer.valueOf(search.isArchived() ? 1 : 0));
            StringBuilder builder = new StringBuilder();
            builder.append("SELECT * FROM APP.ISMS_STANDARD WHERE IS_EVALUATION=:isEvaluation AND ARCHIVED=:archived");
            if (!StringUtils.isEmpty(search.getNamePattern()))
            {
                values.put("namePattern", "%" + search.getNamePattern() + "%");
                builder.append(" AND NAME like :namePattern");
            }
            if (!StringUtils.isEmpty(search.getStandardType()))
            {
                builder.append(" AND STANDARD_TYPE=:standardType");
                values.put("standardType", search.getStandardType());
            }
            PagingResultSetExtractor<Standard> handler = new PagingResultSetExtractor(search.getPageNumber(), search.getItemPerPage())
            {
                public Standard mapRow(ResultSet rs)
                        throws SQLException
                {
                    String name = rs.getString("NAME");
                    String description = rs.getString("DESCRIPTION");
                    String standardType = rs.getString("STANDARD_TYPE");
                    long standardId = rs.getLong("STANDARD_ID");
                    boolean isEvaluation = rs.getInt("IS_EVALUATION") != 0;
                    boolean archived = rs.getInt("ARCHIVED") != 0;
                    try
                    {
                        Standard standard = new Standard(standardId, StandardType.getInstance(standardType), isEvaluation);

                        standard.setArchived(archived);
                        StringProperty.getProperty(standard, "name").setValue(name);
                        StringProperty.getProperty(standard, "description").setValue(description);
                        return standard;
                    }
                    catch (ModelException e)
                    {
                        throw new SQLException(e);
                    }
                }
            };
            return (PagingResult)this.namedTemplate.query(builder.toString(), values, handler);
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            throw new RepositoryException(t.getMessage());
        }
    }

    public PagingResult<Evidence> queryEvidence(EvidenceSearchRequest search)
            throws RepositoryException
    {
        try
        {
            Map<String, Object> values = new HashMap();
            StringBuilder builder = new StringBuilder();
            builder.append("SELECT * FROM APP.ISMS_EVIDENCE");
            if (!StringUtils.isEmpty(search.getNamePattern()))
            {
                values.put("namePattern", "%" + search.getNamePattern().toLowerCase() + "%");
                builder.append(" WHERE (LOWER(NAME) like :namePattern OR LOWER(DESCRIPTION) like :namePattern)");
            }

            return this.namedTemplate.query(builder.toString(), values, new PagingResultSetExtractor<Evidence>(search.getPageNumber(), search.getItemPerPage()) {
                @Override
                public Evidence mapRow(ResultSet rs) throws SQLException {
                    Evidence ev = new Evidence();
                    ev.setId(rs.getLong("EVIDENCE_ID"));
                    ev.setName(rs.getString("NAME"));
                    ev.setDescription(rs.getString("DESCRIPTION"));
                    ev.setPath(rs.getString("PATH"));
                    ev.setContentType(rs.getString("CONTENT_TYPE"));
                    return ev;
                }
            });
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            throw new RepositoryException(t.getMessage());
        }
    }

    @Override
    public PagingResult<Evidence> queryEvidenceTree(EvidenceSearchRequest search)
            throws RepositoryException
    {
        try
        {
            Map<String, Object> values = new HashMap();
            StringBuilder builder = new StringBuilder();
            builder.append("SELECT * FROM APP.ISMS_EVIDENCE where 1=1 ");
            if (!StringUtils.isEmpty(search.getNamePattern()))
            {
                values.put("namePattern", "%" + search.getNamePattern().toLowerCase() + "%");
                builder.append(" and (LOWER(NAME) like :namePattern OR LOWER(DESCRIPTION) like :namePattern)");
            }
            if(search.getClassId()!=null){
                values.put("classId", search.getClassId());
                builder.append(" and exists(select 1 from APP.ISMS_DATA_CLASS_FILE where file_id = evidence_id and class_id=:classId)");
            }

            return this.namedTemplate.query(builder.toString(), values, new PagingResultSetExtractor<Evidence>(search.getPageNumber(), search.getItemPerPage()) {
                @Override
                public Evidence mapRow(ResultSet rs) throws SQLException {
                    Evidence ev = new Evidence();
                    ev.setId(rs.getLong("EVIDENCE_ID"));
                    ev.setName(rs.getString("NAME"));
                    ev.setDescription(rs.getString("DESCRIPTION"));
                    ev.setPath(rs.getString("PATH"));
                    ev.setContentType(rs.getString("CONTENT_TYPE"));
                    return ev;
                }
            });
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            throw new RepositoryException(t.getMessage());
        }
    }

    public PagingResult<Evidence> queryEvidenceByName(String name)
            throws RepositoryException
    {
        try
        {
            Map<String, Object> values = new HashMap();
            StringBuilder builder = new StringBuilder();
            builder.append("SELECT * FROM APP.ISMS_EVIDENCE");
            if (!name.isEmpty())
            {
                values.put("name", name);
                builder.append(" WHERE NAME = :name");
            }
            return this.namedTemplate.query(builder.toString(), values, new PagingResultSetExtractor<Evidence>(0, 10)
            {
                public Evidence mapRow(ResultSet rs)
                        throws SQLException
                {
                    Evidence ev = new Evidence();
                    ev.setId(rs.getLong("EVIDENCE_ID"));
                    ev.setName(rs.getString("NAME"));
                    ev.setDescription(rs.getString("DESCRIPTION"));
                    ev.setPath(rs.getString("PATH"));
                    ev.setContentType(rs.getString("CONTENT_TYPE"));
                    return ev;
                }
            });
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            throw new RepositoryException(t.getMessage());
        }
    }

    public void createEvidence(Evidence evidence)
            throws RepositoryException
    {
        try
        {
            SimpleJdbcInsertion insertion = new SimpleJdbcInsertion();
            insertion.withSchema("APP").withTable("ISMS_EVIDENCE")
                    .withColumnValue("EVIDENCE_ID", Long.valueOf(evidence.getId()))
                    .withColumnValue("NAME", evidence.getName())
                    .withColumnValue("DESCRIPTION", evidence.getDescription())
                    .withColumnValue("PATH", evidence.getPath())
                    .withColumnValue("CONTENT_TYPE", evidence.getContentType());
            insertion.insert(this.jdbcTemplate);
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            throw new RepositoryException(t.getMessage());
        }
    }

    public Evidence getEvidence(long evidenceId)
            throws RepositoryException
    {
        SimpleJdbcQuery<Evidence> query = new SimpleJdbcQuery()
        {
            public Evidence mapRow(ResultSet rs, int rowNum)
                    throws SQLException
            {
                Evidence ev = new Evidence();
                ev.setId(rs.getLong("EVIDENCE_ID"));
                ev.setContentType(rs.getString("CONTENT_TYPE"));
                ev.setDescription(rs.getString("DESCRIPTION"));
                ev.setName(rs.getString("NAME"));
                ev.setPath(rs.getString("PATH"));
                return ev;
            }
        };
        try
        {
            query.withSchema("APP").withTable("ISMS_EVIDENCE").withKey("EVIDENCE_ID", Long.valueOf(evidenceId)).withColumn("*");
            return (Evidence)query.queryForObject(this.namedTemplate);
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            throw new RepositoryException(t.getMessage());
        }
    }

    public void deleteEvidence(long evidenceId)
            throws RepositoryException
    {
        try
        {
            SimpleJdbcDelete deleteStandard = new SimpleJdbcDelete();
            deleteStandard.withSchema("APP").withTable("ISMS_EVIDENCE").withKey("EVIDENCE_ID",
                    Long.valueOf(evidenceId));
            deleteStandard.delete(this.namedTemplate);
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            throw new RepositoryException(t.getMessage());
        }
    }

    public String getEvidencePath(String relativePath)
    {
        return this.uploadDir + "/" + relativePath;
    }

    public void setUploadDir(String uploadDir)
    {
        this.uploadDir = uploadDir;
    }

    public void updateEvidence(Evidence evidence)
            throws RepositoryException
    {
        try
        {
            SimpleJdbcUpdate update = new SimpleJdbcUpdate();
            update.withSchema("APP").withTable("ISMS_EVIDENCE").withKey("EVIDENCE_ID", Long.valueOf(evidence.getId()))
                    .withColumnValue("DESCRIPTION", evidence.getDescription());
            update.update(this.namedTemplate);
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            throw new RepositoryException(t.getMessage());
        }
    }

    @Override
    public void createDataType(DataClass dataClass) {
        SimpleJdbcInsertion insertion = new SimpleJdbcInsertion();
        insertion.withSchema("APP").withTable("ISMS_DATA_CLASS")
                .withColumnValue("CLASS_ID",dataClass.getClassId())
                .withColumnValue("CLASS_TYPE",dataClass.getClassType())
                .withColumnValue("PARENT_ID",dataClass.getParentId())
                .withColumnValue("CLASS_NAME",dataClass.getClassName())
                .withColumnValue("POSITION",dataClass.getPosition());
        insertion.insert(this.jdbcTemplate);
    }

    @Override
    public void updateDataType(DataClass dataClass) {
        SimpleJdbcUpdate insertion = new SimpleJdbcUpdate();
        insertion.withSchema("APP").withTable("ISMS_DATA_CLASS").withKey("CLASS_ID",dataClass.getClassId())
                .withColumnValue("CLASS_TYPE",dataClass.getClassType())
                .withColumnValue("PARENT_ID",dataClass.getParentId())
                .withColumnValue("CLASS_NAME",dataClass.getClassName())
                .withColumnValue("POSITION",dataClass.getPosition());
        insertion.update(this.namedTemplate);
    }

    @Override
    public DataClass queryDataClass(long classId){
        SimpleJdbcQuery<DataClass> simpleJdbcQuery = new SimpleJdbcQuery<DataClass>() {
            @Override
            public DataClass mapRow(ResultSet resultSet, int i) throws SQLException {
                DataClass dataClass = new DataClass();
                dataClass.setClassId(resultSet.getLong("CLASS_ID"));
                dataClass.setClassName(resultSet.getString("CLASS_NAME"));
                dataClass.setClassType(resultSet.getString("CLASS_TYPE"));
                dataClass.setParentId(resultSet.getLong("PARENT_ID"));
                dataClass.setPosition(resultSet.getInt("POSITION"));
                return dataClass;
            }
        };
        simpleJdbcQuery.withSchema("APP").withTable("ISMS_DATA_CLASS").withColumn("*");
        simpleJdbcQuery.withKey("CLASS_ID",classId);
        return simpleJdbcQuery.queryForObject(this.namedTemplate);
    }

    @Override
    public void deleteDataType(String classType,Long classId) {
        DataClass dataClass = this.queryDataClass(classId);
        if(dataClass==null){
            throw new IllegalArgumentException("classId not exits!");
        }
        if(dataClass.getParentId()==0){
            throw new RuntimeException("cannot delete root node!");
        }
        DataClass query = new DataClass();
        query.setParentId(dataClass.getClassId());
        if(this.queryDataClass(query).size()>0){
            throw new RuntimeException("cannot delete node with childrens!");
        }

        int countClassFile = queryDataMappingSizeByClassId(classId);
        if(countClassFile>0){
            throw new RuntimeException("cannot delete node containing files!");
        }

        SimpleJdbcDelete delete = new SimpleJdbcDelete();
        delete.withSchema("APP").withTable("ISMS_DATA_CLASS").withKey("CLASS_ID",classId);
        delete.delete(this.namedTemplate);
    }

    @Override
    public List<DataClass> queryDataClass(DataClass query) {
        SimpleJdbcQuery<DataClass> simpleJdbcQuery = new SimpleJdbcQuery<DataClass>() {
            @Override
            public DataClass mapRow(ResultSet resultSet, int i) throws SQLException {
                DataClass dataClass = new DataClass();
                dataClass.setClassId(resultSet.getLong("CLASS_ID"));
                dataClass.setClassName(resultSet.getString("CLASS_NAME"));
                dataClass.setClassType(resultSet.getString("CLASS_TYPE"));
                dataClass.setParentId(resultSet.getLong("PARENT_ID"));
                dataClass.setPosition(resultSet.getInt("POSITION"));
                return dataClass;
            }
        };
        simpleJdbcQuery.withSchema("APP").withTable("ISMS_DATA_CLASS").withColumn("*");
        if(!StringUtils.isEmpty(query.getClassType())){
            simpleJdbcQuery.withKey("CLASS_TYPE",query.getClassType());
        }
        if(query.getParentId()!=null){
            simpleJdbcQuery.withKey("PARENT_ID",query.getParentId());
        }

        simpleJdbcQuery.withOrderBy("PARENT_ID,POSITION ");
        return simpleJdbcQuery.query(this.namedTemplate);
    }

    @Override
    public void createDataMappingRelation(Long classId, Long dataId) {

        SimpleJdbcDelete delete = new SimpleJdbcDelete();
        delete.withSchema("APP").withTable("ISMS_DATA_CLASS_FILE").withKey("FILE_ID",dataId);
        delete.delete(this.namedTemplate);

        SimpleJdbcInsertion insertion = new SimpleJdbcInsertion();
        insertion.withSchema("APP").withTable("ISMS_DATA_CLASS_FILE")
                .withColumnValue("CLASS_ID",classId)
                .withColumnValue("FILE_ID",dataId);
        insertion.insert(this.jdbcTemplate);
    }

    @Override
    public int queryDataMappingSizeByClassId(Long classId){
        return this.jdbcTemplate.queryForObject("select count(*) from APP.ISMS_DATA_CLASS_FILE where CLASS_ID=?",new Object[]{classId},Integer.class);
    }

    @Override
    public void deleteDataMappingRelation(Long classId, Long dataId){
        this.jdbcTemplate.update("delete from APP.ISMS_DATA_CLASS_FILE where CLASS_ID=? and FILE_ID=?",new Object[]{classId,dataId});
    }

    @Override
    public PagingResult<Evidence> queryDatasTree(EvidenceSearchRequest search)
            throws RepositoryException
    {
        try
        {
            Map<String, Object> values = new HashMap();
            StringBuilder builder = new StringBuilder();
            builder.append("SELECT * FROM APP.ISMS_DATA where 1=1 ");
            if (!StringUtils.isEmpty(search.getNamePattern()))
            {
                values.put("namePattern", "%" + search.getNamePattern().toLowerCase() + "%");
                builder.append(" and (LOWER(NAME) like :namePattern OR LOWER(DESCRIPTION) like :namePattern)");
            }
            if(search.getClassId()!=null){
                values.put("classId", search.getClassId());
                builder.append(" and exists(select 1 from APP.ISMS_DATA_CLASS_FILE where file_id = evidence_id and class_id=:classId)");
            }

            return this.namedTemplate.query(builder.toString(), values, new PagingResultSetExtractor<Evidence>(search.getPageNumber(), search.getItemPerPage()) {
                @Override
                public Evidence mapRow(ResultSet rs) throws SQLException {
                    Evidence ev = new Evidence();
                    ev.setId(rs.getLong("EVIDENCE_ID"));
                    ev.setName(rs.getString("NAME"));
                    ev.setDescription(rs.getString("DESCRIPTION"));
                    ev.setPath(rs.getString("PATH"));
                    ev.setContentType(rs.getString("CONTENT_TYPE"));
                    return ev;
                }
            });
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            throw new RepositoryException(t.getMessage());
        }
    }

    @Override
    public void createData(Evidence evidence)
            throws RepositoryException
    {
        try
        {
            SimpleJdbcInsertion insertion = new SimpleJdbcInsertion();
            insertion.withSchema("APP").withTable("ISMS_DATA")
                    .withColumnValue("EVIDENCE_ID", Long.valueOf(evidence.getId()))
                    .withColumnValue("NAME", evidence.getName())
                    .withColumnValue("DESCRIPTION", evidence.getDescription())
                    .withColumnValue("PATH", evidence.getPath())
                    .withColumnValue("CONTENT_TYPE", evidence.getContentType());
            insertion.insert(this.jdbcTemplate);
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            throw new RepositoryException(t.getMessage());
        }
    }

    @Override
    public Evidence getData(long evidenceId)
            throws RepositoryException
    {
        SimpleJdbcQuery<Evidence> query = new SimpleJdbcQuery()
        {
            public Evidence mapRow(ResultSet rs, int rowNum)
                    throws SQLException
            {
                Evidence ev = new Evidence();
                ev.setId(rs.getLong("EVIDENCE_ID"));
                ev.setContentType(rs.getString("CONTENT_TYPE"));
                ev.setDescription(rs.getString("DESCRIPTION"));
                ev.setName(rs.getString("NAME"));
                ev.setPath(rs.getString("PATH"));
                return ev;
            }
        };
        try
        {
            query.withSchema("APP").withTable("ISMS_DATA").withKey("EVIDENCE_ID", Long.valueOf(evidenceId)).withColumn("*");
            return (Evidence)query.queryForObject(this.namedTemplate);
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            throw new RepositoryException(t.getMessage());
        }
    }

    @Override
    public void updateData(Evidence evidence)
            throws RepositoryException
    {
        try
        {
            SimpleJdbcUpdate update = new SimpleJdbcUpdate();
            update.withSchema("APP").withTable("ISMS_DATA").withKey("EVIDENCE_ID", Long.valueOf(evidence.getId()))
                    .withColumnValue("DESCRIPTION", evidence.getDescription());
            update.update(this.namedTemplate);
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            throw new RepositoryException(t.getMessage());
        }
    }

    @Override
    public void deleteData(long evidenceId)
            throws RepositoryException
    {
        try
        {
            SimpleJdbcDelete deleteStandard = new SimpleJdbcDelete();
            deleteStandard.withSchema("APP").withTable("ISMS_DATA").withKey("EVIDENCE_ID",
                    Long.valueOf(evidenceId));
            deleteStandard.delete(this.namedTemplate);
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            throw new RepositoryException(t.getMessage());
        }
    }

    @Override
    public int queryDataCountByName(String name)
            throws RepositoryException
    {
        return this.jdbcTemplate.queryForObject("SELECT COUNT(*) from APP.ISMS_DATA where name=?",new Object[]{name},Integer.class);
    }



    @Override
    public PagingResult<Evidence> querySecuritiesTree(EvidenceSearchRequest search)
            throws RepositoryException
    {
        try
        {
            Map<String, Object> values = new HashMap();
            StringBuilder builder = new StringBuilder();
            builder.append("SELECT * FROM APP.ISMS_SECURITY where 1=1 ");
            if (!StringUtils.isEmpty(search.getNamePattern()))
            {
                values.put("namePattern", "%" + search.getNamePattern().toLowerCase() + "%");
                builder.append(" and (LOWER(NAME) like :namePattern OR LOWER(DESCRIPTION) like :namePattern)");
            }
            if(search.getClassId()!=null){
                values.put("classId", search.getClassId());
                builder.append(" and exists(select 1 from APP.ISMS_DATA_CLASS_FILE where file_id = evidence_id and class_id=:classId)");
            }

            return this.namedTemplate.query(builder.toString(), values, new PagingResultSetExtractor<Evidence>(search.getPageNumber(), search.getItemPerPage()) {
                @Override
                public Evidence mapRow(ResultSet rs) throws SQLException {
                    Evidence ev = new Evidence();
                    ev.setId(rs.getLong("EVIDENCE_ID"));
                    ev.setName(rs.getString("NAME"));
                    ev.setDescription(rs.getString("DESCRIPTION"));
                    ev.setPath(rs.getString("PATH"));
                    ev.setContentType(rs.getString("CONTENT_TYPE"));
                    return ev;
                }
            });
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            throw new RepositoryException(t.getMessage());
        }
    }

    @Override
    public void createSecurity(Evidence evidence)
            throws RepositoryException
    {
        try
        {
            SimpleJdbcInsertion insertion = new SimpleJdbcInsertion();
            insertion.withSchema("APP").withTable("ISMS_SECURITY")
                    .withColumnValue("EVIDENCE_ID", Long.valueOf(evidence.getId()))
                    .withColumnValue("NAME", evidence.getName())
                    .withColumnValue("DESCRIPTION", evidence.getDescription())
                    .withColumnValue("PATH", evidence.getPath())
                    .withColumnValue("CONTENT_TYPE", evidence.getContentType());
            insertion.insert(this.jdbcTemplate);
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            throw new RepositoryException(t.getMessage());
        }
    }

    @Override
    public Evidence getSecurity(long evidenceId)
            throws RepositoryException
    {
        SimpleJdbcQuery<Evidence> query = new SimpleJdbcQuery()
        {
            public Evidence mapRow(ResultSet rs, int rowNum)
                    throws SQLException
            {
                Evidence ev = new Evidence();
                ev.setId(rs.getLong("EVIDENCE_ID"));
                ev.setContentType(rs.getString("CONTENT_TYPE"));
                ev.setDescription(rs.getString("DESCRIPTION"));
                ev.setName(rs.getString("NAME"));
                ev.setPath(rs.getString("PATH"));
                return ev;
            }
        };
        try
        {
            query.withSchema("APP").withTable("ISMS_SECURITY").withKey("EVIDENCE_ID", Long.valueOf(evidenceId)).withColumn("*");
            return (Evidence)query.queryForObject(this.namedTemplate);
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            throw new RepositoryException(t.getMessage());
        }
    }

    @Override
    public void updateSecurity(Evidence evidence)
            throws RepositoryException
    {
        try
        {
            SimpleJdbcUpdate update = new SimpleJdbcUpdate();
            update.withSchema("APP").withTable("ISMS_SECURITY").withKey("EVIDENCE_ID", Long.valueOf(evidence.getId()))
                    .withColumnValue("DESCRIPTION", evidence.getDescription());
            update.update(this.namedTemplate);
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            throw new RepositoryException(t.getMessage());
        }
    }

    @Override
    public void deleteSecurity(long evidenceId)
            throws RepositoryException
    {
        try
        {
            SimpleJdbcDelete deleteStandard = new SimpleJdbcDelete();
            deleteStandard.withSchema("APP").withTable("ISMS_SECURITY").withKey("EVIDENCE_ID",
                    Long.valueOf(evidenceId));
            deleteStandard.delete(this.namedTemplate);
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            throw new RepositoryException(t.getMessage());
        }
    }

    @Override
    public int querySecurityCountByName(String name)
            throws RepositoryException
    {
        return this.jdbcTemplate.queryForObject("SELECT COUNT(*) from APP.ISMS_SECURITY where name=?",new Object[]{name},Integer.class);
    }

    @Override
    public List<String> queryNetworkSecurityTargets(){
        return this.jdbcTemplate.queryForList("select DISTINCT t.EVALUATION_TARGET FROM APP.ISMS_NETWORK_EVALUATION t",String.class);
    }

    @Override
    public List<NetworkEvaluation> queryNetworkSecurityByTarget(String target){
        SimpleJdbcQuery<NetworkEvaluation> query = new SimpleJdbcQuery<NetworkEvaluation>() {
            @Override
            public NetworkEvaluation mapRow(ResultSet resultSet, int i) throws SQLException {
                NetworkEvaluation networkEvaluation = new NetworkEvaluation();
                networkEvaluation.setEvaluationId(resultSet.getLong("EVALUATION_ID"));
                networkEvaluation.setEvaluationTarget(resultSet.getString("EVALUATION_TARGET"));
                networkEvaluation.setEvaluationIndex(resultSet.getString("EVALUATION_INDEX"));
                networkEvaluation.setControlItem(resultSet.getString("CONTROL_ITEM"));
                networkEvaluation.setResult(resultSet.getString("RESULT"));
                networkEvaluation.setConformity(resultSet.getString("CONFORMITY"));
                networkEvaluation.setRemark(resultSet.getString("REMARK"));
                networkEvaluation.setOrder(resultSet.getInt("ORDER"));
                return networkEvaluation;
            }
        };
        query.withSchema("APP").withTable("ISMS_NETWORK_EVALUATION").withKey("EVALUATION_TARGET",target);

        return query.query(this.namedTemplate);
    }
}
