package org.bk.books.web.pagination;

import graphql.relay.Connection;
import graphql.relay.Edge;
import graphql.relay.PageInfo;
import java.util.List;

public class PageConnection<T> implements Connection<T> {
  private final List<Edge<T>> edges;
  private final PageInfo pageInfo;
  private final int totalCount;

  public PageConnection(List<Edge<T>> edges, PageInfo pageInfo, int totalCount) {
    this.edges = edges;
    this.pageInfo = pageInfo;
    this.totalCount = totalCount;
  }

  @Override
  public List<Edge<T>> getEdges() {
    return edges;
  }

  @Override
  public PageInfo getPageInfo() {
    return pageInfo;
  }

  public int getTotalCount() {
    return totalCount;
  }
}
