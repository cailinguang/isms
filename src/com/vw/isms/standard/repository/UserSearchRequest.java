package com.vw.isms.standard.repository;

public class UserSearchRequest
{
  private String namePattern;
  private int pageNumber;
  private int itemPerPage;
  
  public String getNamePattern()
  {
    return this.namePattern;
  }
  
  public void setNamePattern(String namePattern)
  {
    this.namePattern = namePattern;
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
}
