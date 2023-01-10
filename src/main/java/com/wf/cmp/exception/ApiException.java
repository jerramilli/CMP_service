package com.wf.cmp.exception;

public class ApiException extends RuntimeException{
  public ApiException(String exception ) {
    super(exception);
  }
}
