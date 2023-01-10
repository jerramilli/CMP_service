package com.wf.cmp.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = { "com.wf.cmp" })
public class ElasticSearchConfig {

  @Value("${elasticSearch.url}")
  private String url;

  @Value("${elasticSearch.username}")
  private String username;

  @Value("${elasticSearch.password}")
  private String password;

  // username pwd auth
  @Bean
  public ElasticsearchClient client() {

    final CredentialsProvider credentialsProvider =
            new BasicCredentialsProvider();
    credentialsProvider.setCredentials(AuthScope.ANY,
            new UsernamePasswordCredentials(username, password));

    RestClientBuilder builder = RestClient.builder(
                    HttpHost.create(url))
            .setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
              @Override
              public HttpAsyncClientBuilder customizeHttpClient(
                      HttpAsyncClientBuilder httpClientBuilder) {
                return httpClientBuilder
                        .setDefaultCredentialsProvider(credentialsProvider);
              }
            });

    // Create the low-level client
    RestClient restClient = builder.build();

// Create the transport with a Jackson mapper
    ElasticsearchTransport transport = new RestClientTransport(
            restClient, new JacksonJsonpMapper());

// And create the API client
    return new ElasticsearchClient(transport);
  }


  @Bean
  public RestClient restClient() {

    final CredentialsProvider credentialsProvider =
            new BasicCredentialsProvider();
    credentialsProvider.setCredentials(AuthScope.ANY,
            new UsernamePasswordCredentials(username, password));

    RestClientBuilder builder = RestClient.builder(
                    HttpHost.create(url))
            .setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
              @Override
              public HttpAsyncClientBuilder customizeHttpClient(
                      HttpAsyncClientBuilder httpClientBuilder) {
                return httpClientBuilder
                        .setDefaultCredentialsProvider(credentialsProvider);
              }
            });

    // Create the low-level client
    RestClient restClient = builder.build();

    return restClient;

  }

}
