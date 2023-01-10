package com.wf.cmp.service;

import static com.wf.cmp.constants.CMPconstants.CREATED;
import static com.wf.cmp.constants.CMPconstants.NOT_FOUND;
import static com.wf.cmp.constants.CMPconstants.UPDATED;

import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.DeleteResponse;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.indices.DeleteIndexResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.gson.Gson;
import com.wf.cmp.exception.ApiException;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.wf.cmp.model.CMPRecords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CMPService {

  private Logger logger = LoggerFactory.getLogger(CMPService.class);

  private Gson gson = new Gson();

  private ObjectWriter ow;

  @Autowired
  private ElasticSearchService esService;

  public CMPService(){
    ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
  }


  // read records at given index
  public CMPRecords read(String index) throws IOException {
    logger.info("Started reading for all indices");

    try{
      List<Hit<Object>> hits = esService.read(index);
      logger.info(String.format("Successfully read index: %s and found no of hits: %s",
              index, hits.size()));


      return new CMPRecords(hits);

    }
    catch(Exception e){
      logger.error(String.format("Failed to read index: %s from elastic search, ex: %s", index, e.getMessage()));
      throw new ApiException(String.format("Failed to read from Elastic Search index: %s , ex: %s", index, e.getMessage()));
    }

  }

  // TODO: fix this, not working yet
  // read records for a given index and id
  public CMPRecords readById(String index, String id) throws IOException {
    logger.info("Started reading for all indices");

    GetResponse response;
    try {
      response = esService.readById(index, id);
    }
    catch(Exception e){
      logger.error(String.format("Failed to read from index: %s for given id: %s from elastic search, ex: %s", index, id, e.getCause()));
      throw new ApiException(String.format("Failed to read from Elastic Search index: %s for given id: %s , ex: %s", index, id, e.getMessage()));
    }

    if (response!=null && response.found()) {
      Object source = response.source();
      logger.info(String.format("Successfully found record for given id: %s and index: %s ",
              id, index));
      return new CMPRecords(source);
    }
    else{
      throw new ApiException(String.format("Record not found for given id: %s and index: %s", id, index));
    }

  }


  // save records at given index
  public void save(String index, List<Object> data) {

    List<String> docs = data.stream().map(o -> gson.toJson(o)).collect(Collectors.toList());

    logger.info("Trying to post documents to Elastic search index :"+ index);
    BulkResponse response;
    try{

      response = esService.bulkCreate(index, docs);
    }
    catch(Exception e){
      logger.error(String.format("Failed to post to Elastic Search index: %s , ex: %s", index, e.getMessage()));
      throw new ApiException(String.format("Failed to post to Elastic Search index: %s , ex: %s", index, e.getMessage()));
    }


    if(response!=null && response.errors()){
        logger.error(String.format("Failed to save records at index: %s , errors: %s",
            index, response.items()));
      throw new ApiException(String.format("Failed to save records at index: %s , errors: %s",
              index, response.items()));
    }
    else{
      logger.info("Successfully finished posting documents to Elastic search index : "+ index);
    }


  }


  // save a record at given index
  public void save(String index, Object data) throws IOException {
    String json = gson.toJson(data);
    IndexResponse response = esService.create(index, json);
    if(response.result().name().equalsIgnoreCase(CREATED)){
      logger.info(String.format("document with data : %s at index: %s  has been created successfully.", json, index));
    }
    else {
      throw new ApiException(String.format("Unable to create the document with json : %s , in index: %s",
              json, index));
    }
  }

  // update a record for given id at given index
  public void update(String index, String id, Object data) throws IOException {
    String json = gson.toJson(data);
    IndexResponse response = esService.update(index, id, json);
    if(response.result().name().equalsIgnoreCase(UPDATED)){
      logger.info(String.format("document with id: %s, given json : %s , at index: %s has been updated successfully.",
          id, json, index));
    }
    else{
      throw new ApiException(String.format("Unable to update document with Id : %s , with given json: %s, in index: %s",
              id, json, index));
    }
  }

  //delete record for given id at given index
  public void delete(String index, String id) throws IOException {
    DeleteResponse response = esService.delete(index, id);
    if (Objects.nonNull(response.result()) && !response.result().name().equalsIgnoreCase(NOT_FOUND)) {
      logger.info("Document with id : " + response.id()+ " has been deleted successfully !.");
    }
    else {
      throw new ApiException("document with id : " + response.id()+" does not exist.");
    }

  }

  //delete index
  public void delete(String index) throws IOException {
    DeleteIndexResponse response = esService.delete(index);
    if (Objects.nonNull(response.acknowledged()) && response.acknowledged()) {
      logger.info("Index : " + index+ " has been deleted successfully !.");
    }
    else {
      throw new ApiException("index : " + index+ " does not exist.");
    }

  }

}
