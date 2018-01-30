package com.vw.isms.property;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.vw.isms.ModelException;
import com.vw.isms.RepositoryException;
import com.vw.isms.standard.model.Evidence;
import com.vw.isms.standard.model.StandardNode;
import com.vw.isms.standard.repository.StandardRepository;
import com.vw.isms.util.IdUtil;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@JsonTypeName("evidences")
public class EvidenceSetProperty
  extends Property
{
  public static final String TYPE = "evidences";
  private final Map<Long, Evidence> evidences = new HashMap();
  
  public Collection<Evidence> getValue()
  {
    return Collections.unmodifiableCollection(this.evidences.values());
  }
  
  @JsonCreator
  public EvidenceSetProperty(@JsonProperty("id") long id, @JsonProperty("name") String name, @JsonProperty("readonly") boolean readonly, @JsonProperty("value") Collection<Evidence> evidences)
  {
    super(id, name, readonly);
    for (Evidence e : evidences) {
      this.evidences.put(Long.valueOf(e.getId()), e);
    }
  }
  
  public void update(StandardNode node, StandardRepository repo)
    throws RepositoryException
  {
    repo.updateEvidenceSetProperty(node, this);
  }
  
  public void create(StandardNode node, StandardRepository repo)
    throws RepositoryException
  {
    repo.createEvidenceSetProperty(node, this);
  }
  
  public static EvidenceSetProperty getProperty(Properties props, String name)
    throws ModelException
  {
    Property prop = props.getProperty(name);
    if ((prop instanceof EvidenceSetProperty)) {
      return (EvidenceSetProperty)prop;
    }
    throw new ModelException(prop + " is not a EvidenceIdProperty.");
  }
  
  public String getType()
  {
    return "evidences";
  }
  
  public Property makeCopy()
  {
    return new EvidenceSetProperty(IdUtil.next(), this.name, this.readonly, getValue());
  }
}
