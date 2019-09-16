package com.osdu.function;

import com.osdu.exception.OsduException;
import com.osdu.model.SearchResult;
import com.osdu.model.osdu.OsduSearchObject;
import com.osdu.service.SearchService;
import java.util.function.Function;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;

/**
 * Function to Map OSDU compliant search query to Delfi query. Input format is described in
 * "SDU-82935841-250319-1033.pdf", output format is taken from API description from Delfi Developer
 * Portal -> Search Service -> /query
 */
@Component
public class MapSearchFieldsFunction implements
    Function<Message<OsduSearchObject>, Message<SearchResult>> {

  final static Logger log = LoggerFactory.getLogger(MapSearchFieldsFunction.class);

  @Inject
  SearchService searchService;

  @Override
  public Message<SearchResult> apply(Message<OsduSearchObject> messageSource) {
    try {
      log.info("Received request to search with following arguments: {}", messageSource);
      SearchResult searchResult = searchService
          .searchIndex(messageSource.getPayload(), messageSource.getHeaders());
      log.info(
          "Result of the request to search with following arguments: {}, resulted in following object : {}",
          messageSource, searchResult);
      return new GenericMessage<>(searchResult);
    } catch (OsduException e) {
      log.error("Failed to serve request to search", e);
      throw new RuntimeException(e.getMessage());
    }
  }
}
