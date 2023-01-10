package com.wf.cmp.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.Refresh;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.indices.DeleteIndexRequest;
import co.elastic.clients.elasticsearch.indices.DeleteIndexResponse;
import co.elastic.clients.json.JsonData;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ElasticSearchService {

  @Autowired
  private ElasticsearchClient esClient;

  private ObjectWriter ow;

  public ElasticSearchService(){
    ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
  }

  // read all records at a given index as object type : T
  public <T> List<T> read(String index, Class<T> clazz) throws IOException {
    SearchResponse<T> response = esClient.search(s -> s
            .index(index), clazz
    );

    List<T> hits = new ArrayList<>();

    for(Hit<T> hit : response.hits().hits()){
      hits.add(hit.source());
    }

    return hits;
  }

  // read all records at a given index as Hit Objects
  public List<Hit<Object>> read(String index) throws IOException {
    SearchResponse<Object> response = esClient.search(s -> s
            .index(index), Object.class
    );

    List<Hit<Object>> hits = new ArrayList<>();

    for(Hit<Object> hit : response.hits().hits()){
      hits.add(hit);
    }

    return hits;
  }

  // TODO: fix this, not working yet
  // read a record with given id at an index
  public GetResponse<Object> readById(String index, String id) throws IOException {
    // Search by product name
    return esClient.get(g -> g
                    .index(index)
                    .id(id),
            Object.class
    );
  }


  // create a record at an index
  public IndexResponse create(String index, String json) throws IOException {
    JsonData doc = JsonData.fromJson(json);

    IndexRequest request = new IndexRequest.Builder()
        .index(index)
        .refresh(Refresh.True)
        .document(doc)
        .build();

    return esClient.index(request);
  }

  //create multiple records at an index
  public BulkResponse bulkCreate(String index, List<String> docs) throws IOException {

    BulkRequest.Builder br = new BulkRequest.Builder();

    for (String doc : docs) {
      JsonData json = JsonData.fromJson(doc);
      br.operations(op -> op
          .index(idx -> idx
              .index(index)
              .document(json)
          )
      );
    }

    BulkResponse response = esClient.bulk(br.build());
    return response;
  }

  // update a record with given id at an index
  public IndexResponse update(String index, String id, String json) throws IOException {
    JsonData doc = JsonData.fromJson(json);

    IndexRequest request = new IndexRequest.Builder()
        .index(index)
        .id(id)
        .document(doc)
        .build();

    return esClient.index(request);
  }

  // delete a record with given id at an index
  public DeleteResponse delete(String index, String id) throws IOException {

    DeleteRequest request = DeleteRequest.of(d -> d.index(index).id(id));

    return esClient.delete(request);
  }

  // delete index
  public DeleteIndexResponse delete(String index) throws IOException {

    DeleteIndexRequest request = DeleteIndexRequest.of(d -> d.index(index));

    return esClient.indices().delete(request);
  }

}
