package com.vw.isms.web.request;

public class StandardSearchRequest
{
  private String namePattern;
  private String standardType;
  private int pageNumber;
  private int itemPerPage;
  private boolean archived;
  
  public String getNamePattern()
  {
    return this.namePattern;
  }
  
  public void setNamePattern(String namePattern)
  {
    this.namePattern = namePattern;
  }
  
  public String getStandardType()
  {
    return this.standardType;
  }
  
  public void setStandardType(String standardType)
  {
    this.standardType = standardType;
  }
  
  public boolean isSuccess()
  {
    return true;
  }
  
  public int getPageNumber()
  {
    return this.pageNumber;
  }
  
  public void setPageNumber(int pageNumber)
  {
    this.pageNumber = pageNumber;
  }
  
  public int getItemPerPage()
  {
    return this.itemPerPage;
  }
  
  public void setItemPerPage(int itemPerPage)
  {
    this.itemPerPage = itemPerPage;
  }
  
  public boolean isArchived()
  {
    return this.archived;
  }
  
  public void setArchived(boolean archived)
  {
    this.archived = archived;
  }
}
