package com.vw.isms.web.request;

public class EvidenceSearchRequest
{
  public String namePattern;
  private int pageNumber;
  private int itemPerPage;
  private Long classId;
  
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
  
  public String getNamePattern()
  {
    return this.namePattern;
  }
  
  public void setNamePattern(String namePattern)
  {
    this.namePattern = namePattern;
  }

    public Long getClassId() {
        return classId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }
}
