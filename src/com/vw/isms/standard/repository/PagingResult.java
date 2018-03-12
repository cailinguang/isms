package com.vw.isms.standard.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PagingResult<T>
{
  private final int pageNumber;
  private final int itemPerPage;
  private int count;
  
  public PagingResult(int pageNumber, int itemPerPage)
  {
    this.pageNumber = pageNumber;
    this.itemPerPage = itemPerPage;
  }
  
  private boolean hasNextPage = false;
  private final List<T> results = new ArrayList();
  
  public List<T> getResults()
  {
    return Collections.unmodifiableList(this.results);
  }
  
  public void add(T result)
  {
    this.results.add(result);
  }
  
  public boolean isHasNextPage()
  {
    return this.hasNextPage;
  }
  
  public void setHasNextPage(boolean hasNextPage)
  {
    this.hasNextPage = hasNextPage;
  }
  
  public int getItemPerPage()
  {
    return this.itemPerPage;
  }
  
  public int getPageNumber()
  {
    return this.pageNumber;
  }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
