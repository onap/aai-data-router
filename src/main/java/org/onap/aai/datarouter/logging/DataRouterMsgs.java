/**
 * ============LICENSE_START=======================================================
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
package org.onap.aai.datarouter.logging;

import com.att.eelf.i18n.EELFResourceManager;
import org.onap.aai.cl.eelf.LogMessageEnum;

public enum DataRouterMsgs implements LogMessageEnum {

    /** Data Layer Service started. */
    SERVICE_STARTED,

    /**
     * Data Layer Service failed to start.
     * Arguments: {0} = Exception message.
     */
    STARTUP_FAILURE,

    /**
     * File has been changed.
     * Arguments: {0} = File name.
     */
    FILE_CHANGED,

    /**
     * File has been reloaded.
     * Arguments: {0} = File name.
     */
    FILE_RELOADED,

    /**
     * Reports the configuration watcher interval.
     * Arguments: {0} = Interval
     */
    REPORT_CONFIG_WATCHER_INT,

    /**
     * Loading properties file.
     * Arguments: {0} = File name.
     */
    LOADING_PROPERTIES,

    /**
     * Properties file has been loaded.
     * Arguments: {0} = File name.
     */
    PROPERTIES_LOADED,

    /**
     * UEB no events received.
     * Arguments: {0} = Topic name
     */
    UEB_NO_EVENTS_RECEIVED,

    /**
     * Routing policies are being configured.
     */
    CONFIGURING_ROUTING_POLICIES,

    /**
     * A properties file could not be successfully loaded.
     * Arguments: {0} = File name.
     */
    LOAD_PROPERTIES_FAILURE,

    /**
     * Failed to register for an event topic with UEB.
     * Arguments: {0} = Topic {1} = Error reason
     */
    UEB_CONNECT_ERR,

    /**
     * An error occurred while trying to route a query.
     * Arguments: {0} = Query {1} = Error reason
     */
    QUERY_ROUTING_ERR,

    /**
     * Error in file monitor block.
     */
    FILE_MON_BLOCK_ERR,

    /**
     * Failure to create a property map.
     */
    CREATE_PROPERTY_MAP_ERR,

    /**
     * An error occurred reading from a file stream.
     */
    FILE_STREAM_ERR,

    /**
     * An error occurred while trying to configure a routing policy.
     * Arguments: {0} = policy name {1} = source of the routing policy {2} = action of the routing
     * policy
     */
    ROUTING_POLICY_CONFIGURATION_ERROR,

    /**
     * Received request {0} {1} from {2}. Sending response: {3}
     * Arguments: {0} = operation {1} = target URL {2} = source {3} = response code
     */
    PROCESS_REST_REQUEST,

    /**
     * Index {0} may not exist in the search data store.  Attempting to create it now.
     */
    CREATE_MISSING_INDEX,

    /**
     * Processed event {0}. Result: {1}
     * Arguments: {0} = event topic {1} = result
     */
    PROCESS_EVENT,

    /**
     * Arguments: {0} = Error
     */

    BAD_REST_REQUEST,

    /**
     * Arguments: {0} = Search index URL {1} = Reason
     */
    FAIL_TO_CREATE_SEARCH_INDEX,

    /**
     * Arguments: {0} = Successfully created index at endpoint
     */
    SEARCH_INDEX_CREATE_SUCCESS,

    INVALID_OXM_FILE,

    INVALID_OXM_DIR,
    LOAD_OXM_ERROR,

    /**
     * Arguments: {0} = origin payload
     */
    INVALID_ORIGIN_PAYLOAD,

    /**
     * Arguments: {0} = Origin URL {1} = Target outbound URL
     */
    ROUTING_FROM_TO,

    /**
     * Arguments: {0} = Target outbound URL, {1} = Response
     */
    ROUTING_RESPONSE,

    /**
     * Failed to create or update document in index {0}.  Cause: {1}
     *
     * Arguments:
     *    {0} = Index name
     *    {1} = Failure cause
     */
    FAIL_TO_CREATE_UPDATE_DOC,

    /**
     * Arguments: {0} = Operation, {1} = Time in ms
     */
    OP_TIME,

    /** Arguments: {0} = Schema file location */
    READING_JSON_SCHEMA_ERROR,

    /** Arguments: {0} = Error message */
    JSON_CONVERSION_ERROR,

    /**
     * Operation: {0}; URI: {1}; Exception: {2}
     */
    EXCEPTION_DURING_METHOD_CALL,

    /**
     * Failed to publish {0} event(s) to DMaaP; cause: {1}
     * Arguments: {0} = number of events; {1} = cause
     */
    FAIL_TO_SEND_TO_DMAAP,

    /**
     * Failed to authenticate: {0}
     * Arguments: {0} = reason
     */
    FAIL_TO_AUTHENTICATE,

    /**
     * Invalid value for configuration '{0}'; using default value {1}
     * Arguments: {0} config attribute;  {1} default value
     */
    WARN_USING_DEFAULT;
    /**
     * Static initializer to ensure the resource bundles for this class are loaded...
     */
    static {
        EELFResourceManager.loadMessageBundle("logging/DataRouterMsgs");
    }
}
