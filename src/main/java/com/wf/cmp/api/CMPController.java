package com.wf.cmp.api;

import co.elastic.clients.elasticsearch.core.search.Hit;
import com.wf.cmp.model.CMPRequestBody;
import com.wf.cmp.model.CMPRecords;
import com.wf.cmp.service.CMPService;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class CMPController {

  @Autowired
  CMPService cmpService;

  // read records at given index
  @GetMapping(value = "/records",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<CMPRecords> getRecords(
      @RequestParam(value = "index", required = true) String index,
      @RequestParam(value = "id", required = false) String id) throws IOException {

    try{
      // TODO: fix this, not working yet : cmpService.read(index, id)

      CMPRecords cmpRecords = id==null ? cmpService.read(index) : cmpService.readById(index, id);

      return ResponseEntity.ok(cmpRecords);
    }
    catch(Exception e){
      return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

  }


  // save records at given index
  @PostMapping(value = "/records",
      produces = {"application/json"},
      consumes = {"application/json; charset=utf-8"})
  public ResponseEntity saveRecords(
      @RequestParam(value = "index", required = true) String index,
      @RequestBody CMPRequestBody body){

    try{
      cmpService.save(index, body.getPayload());
      return new ResponseEntity("Successfully Saved docs to ES", HttpStatus.CREATED);
    }
    catch(Exception e){
      return ResponseEntity.internalServerError().body(e.getMessage());
    }
  }

  // update record for given id at given index
  @PutMapping(value = "/records",
      produces = {"application/json"},
      consumes = {"application/json; charset=utf-8"})
  public ResponseEntity updateRecord(
      @RequestParam(value = "index", required = true) String index,
      @RequestParam(value = "id", required = true) String id,
      @RequestBody Object body) throws IOException {

    try{
      cmpService.update(index, id, body);
      return new ResponseEntity("Successfully Updated doc in ES", HttpStatus.OK);
    }
    catch(Exception e){
      return ResponseEntity.internalServerError().body(e.getMessage());
    }
  }


  // delete record for given id and at given index
  @DeleteMapping(value = "/records",
      produces = {"application/json"},
      consumes = {"application/json; charset=utf-8"})
  public ResponseEntity deleteRecords(
      @RequestParam(value = "index", required = true) String index,
      @RequestParam(value = "id", required = true) String id) throws IOException {

    try{
      cmpService.delete(index, id);
      return  new ResponseEntity("Successfully Deleted doc in ES", HttpStatus.OK);
    }
    catch(Exception e){
      return ResponseEntity.internalServerError().body(e.getMessage());
    }
  }

  // delete index , for DEV utility
  @DeleteMapping(value = "/index/{index}",
          produces = {"application/json"},
          consumes = {"application/json; charset=utf-8"})
  public ResponseEntity deleteIndex(
          @PathVariable(value = "index", required = true) String index) throws IOException {

    try{
      cmpService.delete(index);
      return  new ResponseEntity("Successfully Deleted index in ES", HttpStatus.OK);
    }
    catch(Exception e){
      return ResponseEntity.internalServerError().body(e.getMessage());
    }
  }
}
