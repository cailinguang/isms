package com.vw.isms.standard.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.ResultSetExtractor;

public abstract class PagingResultSetExtractor<T>
  implements ResultSetExtractor<PagingResult<T>>
{
  private final PagingResult<T> result;
  
  public PagingResultSetExtractor(int pageNumber, int itemPerPage)
  {
    this.result = new PagingResult(pageNumber, itemPerPage);
  }
  
  public abstract T mapRow(ResultSet paramResultSet)
    throws SQLException;
  
  public PagingResult<T> extractData(ResultSet rs)
    throws SQLException
  {
    int startIndex = this.result.getItemPerPage() * this.result.getPageNumber();
    int endIndex = this.result.getItemPerPage() * (this.result.getPageNumber() + 1);
    int index = 0;
    while ((index < startIndex) && (rs.next())) {
      index++;
    }
    while ((index < endIndex) && (rs.next()))
    {
      this.result.add(mapRow(rs));
      index++;
    }
    this.result.setHasNextPage(rs.next());
    return this.result;
  }
}
