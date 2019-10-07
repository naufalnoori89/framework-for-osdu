package com.osdu.service;

import com.osdu.client.delfi.DelfiSearchClient;
import com.osdu.mapper.SearchObjectMapper;
import com.osdu.mapper.SearchResultMapper;
import com.osdu.model.SearchObject;
import com.osdu.model.SearchResult;
import com.osdu.model.delfi.DelfiSearchObject;
import com.osdu.model.delfi.DelfiSearchResult;
import com.osdu.model.osdu.OsduSearchObject;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.NotImplementedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Service;

/**
 * Delfi API query service.
 */
@Service
@Slf4j
public class DelfiSearchService implements SearchService {

  static final String KIND_HEADER_KEY = "kind";
  static final String PARTITION_HEADER_KEY = "partition";
  static final String AUTHORIZATION_HEADER = "authorization";

  final DelfiSearchClient delfiSearchClient;

  @Inject
  @Named
  SearchObjectMapper searchObjectMapper;

  @Inject
  @Named
  SearchResultMapper searchResultMapper;

  @Value("${search.mapper.delfi.appkey}")
  String applicationKey;

  public DelfiSearchService(DelfiSearchClient delfiSearchClient) {
    this.delfiSearchClient = delfiSearchClient;
  }

  /**
   * NOT IMPLEMENTED YET Searches Delfi partition using index.
   *
   * @param searchObject parameters to use during search
   * @param headers      headers of the orriginal search request to get authorization header from
   *                     them
   * @return {@link SearchResult} the result of the search from Delfi portal
   */
  @Override
  public SearchResult searchIndexWithCursor(SearchObject searchObject, MessageHeaders headers) {
    throw new NotImplementedException();
  }

  /**
   * Searches Delfi partition.
   *
   * @param searchObject parameters to use during search
   * @param headers      headers of the orriginal search request to get authorization header from
   *                     them
   * @return {@link SearchResult} the result of the search from Delfi portal
   */
  @Override
  public SearchResult searchIndex(SearchObject searchObject, MessageHeaders headers) {
    log.info("Received request to query Delfi Portal for data with following arguments: {},{}",
        searchObject, headers);

    String kind = extractHeaders(headers, KIND_HEADER_KEY);
    String partition = extractHeaders(headers, PARTITION_HEADER_KEY);

    DelfiSearchObject delfiSearchObject = searchObjectMapper
        .osduSearchObjectToDelfiSearchObject((OsduSearchObject) searchObject, kind, partition);
    DelfiSearchResult searchResult = delfiSearchClient.searchIndex(
        String.valueOf(headers.get(AUTHORIZATION_HEADER)),
        applicationKey,
        partition,
        delfiSearchObject);
    SearchResult osduSearchResult = searchResultMapper
        .delfiSearchResultToOsduSearchResult(searchResult, (OsduSearchObject) searchObject);
    log.info("Received search result: {}", osduSearchResult);
    return osduSearchResult;
  }

  private String extractHeaders(MessageHeaders headers, String headerKey) {
    if (headers.containsKey(headerKey)) {
      String result = (String) headers.get(headerKey);
      log.debug("Found {} override in the request, using following parameter: {}", headerKey,
          result);
      return result;
    }
    return null;
  }
}