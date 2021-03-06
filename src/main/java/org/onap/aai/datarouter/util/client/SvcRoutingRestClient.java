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
package org.onap.aai.datarouter.util.client;

import org.onap.aai.restclient.client.RestClient;

import com.fasterxml.jackson.databind.JsonNode;


public interface SvcRoutingRestClient {

  String getHost();

  void setHost(String host);

  String getPort();

  void setPort(String port);

  String getTargetUri();

  void setTargetUri(String targetUri);

  JsonNode getTargetPayload();

  void setTargetPayload(JsonNode targetPayload);

  RestClient getRestClient();
  
  void setRestClient(RestClient client);

}
