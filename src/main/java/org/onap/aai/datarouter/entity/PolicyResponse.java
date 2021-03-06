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
package org.onap.aai.datarouter.entity;

/**
 * Provides information about the level of success of a policy execution against a routed query.
 */
public class PolicyResponse {

  private ResponseType responseType;

  private String responseData;

  private int httpResponseCode;

  public PolicyResponse(ResponseType responseType, String responseData) {
    super();
    this.responseType = responseType;
    this.responseData = responseData;
  }

  public ResponseType getResponseType() {
    return responseType;
  }

  public String getResponseData() {
    return responseData;
  }


  public int getHttpResponseCode() {
    return httpResponseCode;
  }

  public void setHttpResponseCode(int httpResponseCode) {
    this.httpResponseCode = httpResponseCode;
  }

  @Override
  public String toString() {
    return "PolicyResponse [responseType=" + responseType + ", responseData=" + responseData
        + ", httpResponseCode=" + httpResponseCode + "]";
  }



  public enum ResponseType {
    SUCCESS, PARTIAL_SUCCESS, FAILURE;
  }
}
