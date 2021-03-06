/**
 * ﻿============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017-2018 AT&T Intellectual Property. All rights reserved.
 * Copyright © 2017-2018 Amdocs
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */
package org.onap.aai.datarouter.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response.Status;

import org.eclipse.jetty.util.security.Password;
import org.onap.aai.cl.api.Logger;
import org.onap.aai.cl.mdc.MdcContext;
import org.onap.aai.datarouter.logging.DataRouterMsgs;
import org.onap.aai.restclient.client.Headers;
import org.onap.aai.restclient.client.OperationResult;
import org.onap.aai.restclient.client.RestClient;
import org.onap.aai.restclient.enums.RestAuthenticationMode;
import org.onap.aai.restclient.rest.HttpUtil;
import org.slf4j.MDC;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import com.sun.jersey.core.util.MultivaluedMapImpl;

public class SearchServiceAgent {

  private Logger logger;
  
  private RestClient searchClient = null;
  private Map<String, String> indexSchemaMapping = new HashMap<>();
  
  private String searchUrl = null;
  private String documentEndpoint = null;
  private String schemaHomeDir = null;
  
  
  /**
   * Creates a new instance of the search service agent.
   * 
   * @param certName         - Certificate to use for talking to the Search Service.
   * @param keystore         - Keystore to use for talking to the Search Service.
   * @param keystorePwd      - Keystore password for talking to the Search Service.
   * @param searchUrl        - URL at which the Search Service can be reached.
   * @param documentEndpoint - Endpoint for accessing document resources on the Search Service.
   * @param logger           - Logger to use for system logs.
   */
  public SearchServiceAgent(String certName, 
                            String keystore, 
                            String keystorePwd,
                            String searchUrl,
                            String documentEndpoint,
                            Logger logger) {
    
    initialize(certName, keystore, keystorePwd, searchUrl, documentEndpoint, logger);
  }
  
  
  /**
   * Performs all one-time initialization required for the search agent.
   * 
   * @param certName         - Certificate to use for talking to the Search Service.
   * @param keystore         - Keystore to use for talking to the Search Service.
   * @param keystorePwd      - Keystore password for talking to the Search Service.
   * @param searchUrl        - URL at which the Search Service can be reached.
   * @param documentEndpoint - Endpoint for accessing document resources on the Search Service.
   * @param logger           - Logger to use for system logs.
   */
  private void initialize(String certName, 
                          String keystore, 
                          String keystorePwd, 
                          String searchUrl, 
                          String documentEndpoint, 
                          Logger logger) {
    
    String deobfuscatedCertPassword = keystorePwd.startsWith("OBF:")?Password.deobfuscate(keystorePwd):keystorePwd;
    // Create REST client for search service
    searchClient = new RestClient()
                    .authenticationMode(RestAuthenticationMode.SSL_CERT)
                    .validateServerHostname(false)
                    .validateServerCertChain(false)
                    .clientCertFile(DataRouterConstants.DR_HOME_AUTH + certName)
                    .clientCertPassword(deobfuscatedCertPassword);                    
    
    this.searchUrl        = searchUrl;
    this.documentEndpoint = documentEndpoint;
    
    this.logger           = logger;
    this.schemaHomeDir = DataRouterConstants.DR_SEARCH_SCHEMA_HOME;
  }
  
  
  /**
   * Creates an index through the search db abstraction
   * 
   * @param index          - The name of the index to be created.
   * @param schemaLocation - The name of the schema file for the index.
   */
  public void createSearchIndex(String index, String schemaLocation) {
     
    // Create a mapping of the index name to schema location 
    indexSchemaMapping.put(index, schemaLocation);
    
    // Now, create the index.
    createIndex(index, schemaLocation);
  }
  
  public void createSearchIndex(String index, String schemaLocation, String endUrl) {
    
    // Create a mapping of the index name to schema location 
    indexSchemaMapping.put(index, schemaLocation);
    
    // Now, create the index.
    createIndex(index, schemaLocation, endUrl);
  }
  
  /**
   * This method performs the actual work of creating a search index.
   * 
   * @param index          - The name of the index to be created.
   * @param schemaLocation - The name of the schema file for the index.
   */
  private void createIndex(String index, String schemaLocation) {
    
    logger.debug("Creating search index, index name: = " + index + ", schemaLocation = " + schemaLocation);
    
    MultivaluedMap<String, String> headers = new MultivaluedMapImpl();
    headers.put("Accept", Arrays.asList("application/json"));
    headers.put(Headers.FROM_APP_ID, Arrays.asList("DL"));
    headers.put(Headers.TRANSACTION_ID, Arrays.asList(UUID.randomUUID().toString()));
      
    String url = concatSubUri(searchUrl, index);
    try {

      OperationResult result = searchClient.put(url, loadFileData(schemaLocation), headers,
                                                MediaType.APPLICATION_JSON_TYPE, null);

      if (!HttpUtil.isHttpResponseClassSuccess(result.getResultCode())) {
        logger.error(DataRouterMsgs.FAIL_TO_CREATE_SEARCH_INDEX, index, result.getFailureCause());
      } else {
        logger.info(DataRouterMsgs.SEARCH_INDEX_CREATE_SUCCESS, index);
      }

    } catch (Exception e) {
      logger.error(DataRouterMsgs.FAIL_TO_CREATE_SEARCH_INDEX, index, e.getLocalizedMessage());
    }
  }
  
  private void createIndex(String index, String schemaLocation, String endUrl) {
    
    logger.debug("Creating search index, index name: = " + index + ", schemaLocation = " + schemaLocation);
    
    MultivaluedMap<String, String> headers = new MultivaluedMapImpl();
    headers.put("Accept", Arrays.asList("application/json"));
    headers.put(Headers.FROM_APP_ID, Arrays.asList("DL"));
    headers.put(Headers.TRANSACTION_ID, Arrays.asList(UUID.randomUUID().toString()));
      
    String url = concatSubUri(searchUrl, endUrl, index);
    try {

      OperationResult result = searchClient.put(url, loadFileData(schemaLocation), headers,
                                                MediaType.APPLICATION_JSON_TYPE, null);
      if (!HttpUtil.isHttpResponseClassSuccess(result.getResultCode())) {
        logger.error(DataRouterMsgs.FAIL_TO_CREATE_SEARCH_INDEX, index, result.getFailureCause());
      } else {
        logger.info(DataRouterMsgs.SEARCH_INDEX_CREATE_SUCCESS, index);
      }

    } catch (Exception e) {
      logger.error(DataRouterMsgs.FAIL_TO_CREATE_SEARCH_INDEX, index, e.getLocalizedMessage());
    }
  }
  
  /**
   * Retrieves a document from the search service.
   * 
   * @param index - The index to retrieve the document from.
   * @param id    - The unique identifier for the document.
   * 
   * @return - The REST response returned from the Search Service.
   */
  public OperationResult getDocument(String index, String id) {
    
    Map<String, List<String>> headers = new HashMap<>();
    headers.put(Headers.FROM_APP_ID, Arrays.asList("Data Router"));
    headers.put(Headers.TRANSACTION_ID, Arrays.asList(MDC.get(MdcContext.MDC_REQUEST_ID)));
    
    String url = concatSubUri(searchUrl, index, documentEndpoint, id);
    return searchClient.get(url, headers, MediaType.APPLICATION_JSON_TYPE);    
  }
  
  
  /**
   * Creates or updates a document in the Search Service.
   * 
   * @param index   - The index to create or update the document in.
   * @param id      - The identifier for the document.
   * @param payload - The document contents.
   * @param headers - HTTP headers.
   */
  public void putDocument(String index, String id, String payload, Map<String, List<String>> headers) {
        
    // Try to post the document to the search service.
    OperationResult result = doDocumentPut(index, id, payload, headers);
    
    // A 404 response from the Search Service may indicate that the index we are writing
    // to does not actually exist.  We will try creating it now.
    if(result.getResultCode() == Status.NOT_FOUND.getStatusCode()) {
            
      // Lookup the location of the schema that we want to create.
      String indexSchemaLocation = indexSchemaMapping.get(index);
      if(indexSchemaLocation != null) {
        
        // Try creating the index now...
        logger.info(DataRouterMsgs.CREATE_MISSING_INDEX, index);
        createIndex(index, indexSchemaLocation);
        
        // ...and retry the document post.
        result = doDocumentPut(index, id, payload, headers);
      }
    }
    
    if(!resultSuccessful(result)) {
      logger.error(DataRouterMsgs.FAIL_TO_CREATE_UPDATE_DOC, index, result.getFailureCause());
    }
  }
  
  
  /**
   * This method does the actual work of submitting a document PUT request to the Search Service.
   * 
   * @param index   - The index to create or update the document in.
   * @param id      - The identifier for the document.
   * @param payload - The document contents.
   * @param headers - HTTP headers.
   * 
   * @return - The HTTP response returned by the Search Service.
   */
  private OperationResult doDocumentPut(String index, String id, String payload, Map<String, List<String>> headers) {
    
    String url = concatSubUri(searchUrl, index, documentEndpoint, id);
    return searchClient.put(url, payload, headers, MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_JSON_TYPE);
  }
  
  
  /**
   * Creates a document in the Search Service.
   * 
   * @param index   - The index to create the document in.
   * @param payload - The document contents.
   * @param headers - HTTP headers.
   */
  public void postDocument(String index, String payload, Map<String, List<String>> headers) {
    
    // Try to post the document to the search service.
    OperationResult result = doDocumentPost(index, payload, headers);
    
    // A 404 response from the Search Service may indicate that the index we are writing
    // to does not actually exist.  We will try creating it now.
    if(result.getResultCode() == Status.NOT_FOUND.getStatusCode()) {
      
      // Lookup the location of the schema that we want to create.
      String indexSchemaLocation = indexSchemaMapping.get(index);
      if(indexSchemaLocation != null) {
        
        // Try creating the index now...
        logger.info(DataRouterMsgs.CREATE_MISSING_INDEX, index);
        createIndex(index, indexSchemaLocation);
        
        // ...and retry the document post.
        result = doDocumentPost(index, payload, headers);
      }
    }
    
    if(!resultSuccessful(result)) {
      logger.error(DataRouterMsgs.FAIL_TO_CREATE_UPDATE_DOC, index, result.getFailureCause());
    }
  }
  
  
  /**
   * This method does the actual work of submitting a document PUT request to the Search Service.
   * 
   * @param index   - The index to create or update the document in.
   * @param payload - The document contents.
   * @param headers - HTTP headers.
   * 
   * @return - The HTTP response returned by the Search Service.
   */
  private OperationResult doDocumentPost(String index, String payload, Map<String, List<String>> headers) {
    
    String url = concatSubUri(searchUrl, index, documentEndpoint);
    return searchClient.post(url, payload, headers, MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_JSON_TYPE);
  }
  
  
  /**
   * Removes a document from the Search Service.
   * 
   * @param index   - The index to create the document in.
   * @param documentId      - The identifier for the document.
   * @param headers - HTTP headers.
   */
  public void deleteDocument(String index, String documentId, Map<String, List<String>> headers) {
    
    String url = concatSubUri(searchUrl, index, documentEndpoint, documentId);
    searchClient.delete(url, headers, null);
  }
  
  
  /**
   * Convenience method to load up all the data from a file into a string
   * 
   * @param filename the filename to read from disk
   * @return the data contained within the file
   * @throws Exception
   */
  protected String loadFileData(String filename) throws Exception {
    Resource fileResource = getSchemaResource(filename);
    if (fileResource == null) {
      throw new Exception("Could not find file = " + filename + ".");
    }

    try (
        InputStreamReader inputStreamReader = new InputStreamReader(fileResource.getInputStream(), StandardCharsets.UTF_8);
        BufferedReader in = new BufferedReader(inputStreamReader);
        ){
      String line;
      StringBuilder data = new StringBuilder();
      while((line = in.readLine()) != null) {
        data.append(line);
      }
      return data.toString();
    } catch (Exception e) {
      throw new Exception("Failed to read from file = " + filename + ".", e);
    }
  }

  private Resource getSchemaResource(String filename) {
    Resource fileResource = null;

    if(filename == null) {
      return null;
    }
    
    if ((schemaHomeDir != null) && (fileResource = new FileSystemResource(schemaHomeDir + "/" + filename)).isReadable()) {
      return fileResource;
    }

    if ((fileResource = new ClassPathResource(filename)).isReadable()) {
      return fileResource;
    }

    return null;
  }
  
  
  /**
   * Helper utility to concatenate substrings of a URI together to form a proper URI.
   * 
   * @param suburis the list of substrings to concatenate together
   * @return the concatenated list of substrings
   */
  public static String concatSubUri(String... suburis) {
    String finalUri = "";

    for (String suburi : suburis) {

      if (suburi != null) {
        // Remove any leading / since we only want to append /
        suburi = suburi.replaceFirst("^/*", "");

        // Add a trailing / if one isn't already there
        finalUri += suburi.endsWith("/") ? suburi : suburi + "/";
      }
    }

    return finalUri;
  }
  
  
  /**
   * Helper utility to check the response code of an HTTP response.
   * 
   * @param aResult - The response that we want to check.
   * 
   * @return - true if the response contains a success code,
   *           false otherwise.
   */
  private boolean resultSuccessful(OperationResult aResult) {
    
    return (aResult.getResultCode() >= 200) && (aResult.getResultCode() < 300);
  }
}
