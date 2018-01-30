package com.vw.isms.property;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.vw.isms.ModelException;
import com.vw.isms.RepositoryException;
import com.vw.isms.standard.model.StandardNode;
import com.vw.isms.standard.repository.StandardRepository;
import com.vw.isms.util.IdUtil;

@JsonTypeName("boolean")
public class BooleanProperty
        extends Property
{
    public static final String TYPE = "boolean";
    private boolean value;

    @JsonCreator
    public BooleanProperty(@JsonProperty("id") long id, @JsonProperty("name") String name, @JsonProperty("readonly") boolean readonly)
    {
        super(id, name, readonly);
    }

    public boolean getValue()
    {
        return this.value;
    }

    public void setValue(boolean value)
    {
        this.value = value;
    }

    public static BooleanProperty getProperty(Properties props, String name)
            throws ModelException
    {
        Property prop = props.getProperty(name);
        if ((prop instanceof BooleanProperty)) {
            return (BooleanProperty)prop;
        }
        throw new ModelException(prop + " is not a BooleanProperty.");
    }

    public void update(StandardNode node, StandardRepository repo)
            throws RepositoryException
    {
        repo.updateBooleanProperty(node, this);
    }

    public void create(StandardNode node, StandardRepository repo)
            throws RepositoryException
    {
        repo.createBooleanProperty(node, this);
    }

    public String getType()
    {
        return "boolean";
    }

    public Property makeCopy()
    {
        BooleanProperty prop = new BooleanProperty(IdUtil.next(), this.name, this.readonly);
        prop.value = this.value;
        return prop;
    }
}
