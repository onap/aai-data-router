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
package org.onap.aai.datarouter.policy;

import java.io.FileNotFoundException;
import java.util.List;

import org.apache.camel.Exchange;
import org.onap.aai.datarouter.entity.SpikeEventEntity;
import org.onap.aai.datarouter.entity.SpikeEventMeta;
import org.onap.aai.datarouter.logging.EntityEventPolicyMsgs;


public class SpikeEntitySearchProcessor extends AbstractSpikeEntityEventProcessor {

  public static final String additionalInfo = "Response of SpikeEntityEventPolicy";

  private static final String PROCESS_SPIKE_EVENT = "Process Spike Event";

  /** Agent for communicating with the Search Service. */

  public SpikeEntitySearchProcessor(SpikeEventPolicyConfig config) throws FileNotFoundException {
    super(config);

  }

  @Override
  protected void startup() {
    // Create the indexes in the search service if they do not already exist.
    searchAgent.createSearchIndex(searchIndexName, searchIndexSchema);
    logger.info(EntityEventPolicyMsgs.ENTITY_EVENT_POLICY_REGISTERED);
  }

  @Override
  public void process(Exchange exchange) throws Exception {

    long startTime = System.currentTimeMillis();

    SpikeEventMeta meta = processSpikeEvent(exchange);

    if (meta == null) {
      return;
    }

    String oxmEntityType = getOxmEntityType(meta.getSpikeEventVertex().getType());
    List<String> searchableAttr = getSearchableAttibutes(meta.getOxmJaxbContext(), oxmEntityType,
        meta.getSpikeEventVertex().getType(), meta.getEventEntity().toString(), exchange);
    if (searchableAttr == null) {
      return;
    }


    SpikeEventEntity spikeEventEntity = new SpikeEventEntity();
    spikeEventEntity.setEntityType(meta.getSpikeEventVertex().getType());
    spikeEventEntity.setLink(meta.getSpikeEventVertex().getEntityLink());
    spikeEventEntity = populateSpikeEventEntity(exchange, spikeEventEntity,
        meta.getOxmJaxbContext(), meta.getSpikeEventVertex().getType(), meta.getBodyOperationType(),
        meta.getVertexProperties().toString(), oxmEntityType, searchableAttr);

    if (spikeEventEntity == null) {
      return;
    }

    handleSearchServiceOperation(spikeEventEntity, meta.getBodyOperationType(), searchIndexName);
    long stopTime = System.currentTimeMillis();
    metricsLogger.info(EntityEventPolicyMsgs.OPERATION_RESULT_NO_ERRORS, PROCESS_SPIKE_EVENT,
        String.valueOf(stopTime - startTime));
    setResponse(exchange, ResponseType.SUCCESS, additionalInfo);
    return;
  }


  /*
   * This is not for this Scope. We get back to it later. (updateCerInEntity) private void
   * updateSearchEntityWithCrossEntityReference(SpikeEventEntity spikeEventEntity) { try {
   * Map<String, List<String>> headers = new HashMap<>(); headers.put(Headers.FROM_APP_ID,
   * Arrays.asList("Data Router")); headers.put(Headers.TRANSACTION_ID,
   * Arrays.asList(MDC.get(MdcContext.MDC_REQUEST_ID)));
   * 
   * String entityId = spikeEventEntity.getId(); String jsonPayload;
   * 
   * // Run the GET to retrieve the ETAG from the search service OperationResult storedEntity =
   * searchAgent.getDocument(entitySearchIndex, entityId);
   * 
   * if (HttpUtil.isHttpResponseClassSuccess(storedEntity.getResultCode())) { /* NOTES:
   * aaiEventEntity (ie the nested entity) may contain a subset of properties of the pre-existing
   * object, so all we want to do is update the CER on the pre-existing object (if needed).
   * 
   * 
   * List<String> etag = storedEntity.getHeaders().get(Headers.ETAG);
   * 
   * if (etag != null && !etag.isEmpty()) { headers.put(Headers.IF_MATCH, etag); } else {
   * logger.error(EntityEventPolicyMsgs.NO_ETAG_AVAILABLE_FAILURE, entitySearchIndex, entityId); }
   * 
   * ArrayList<JsonNode> sourceObject = new ArrayList<>();
   * NodeUtils.extractObjectsByKey(NodeUtils.convertJsonStrToJsonNode(storedEntity.getResult()),
   * "content", sourceObject);
   * 
   * if (!sourceObject.isEmpty()) { JsonNode node = sourceObject.get(0); final String sourceCer =
   * NodeUtils.extractFieldValueFromObject(node, "crossReferenceEntityValues"); String newCer =
   * spikeEventEntity.getCrossReferenceEntityValues(); boolean hasNewCer = true; if (sourceCer !=
   * null && sourceCer.length() > 0) { // already has CER if (!sourceCer.contains(newCer)) {// don't
   * re-add newCer = sourceCer + ";" + newCer; } else { hasNewCer = false; } }
   * 
   * if (hasNewCer) { // Do the PUT with new CER ((ObjectNode)
   * node).put("crossReferenceEntityValues", newCer); jsonPayload =
   * NodeUtils.convertObjectToJson(node, false); searchAgent.putDocument(entitySearchIndex,
   * entityId, jsonPayload, headers); } } } else {
   * 
   * if (storedEntity.getResultCode() == 404) { // entity not found, so attempt to do a PUT
   * searchAgent.putDocument(entitySearchIndex, entityId, spikeEventEntity.getAsJson(), headers); }
   * else { logger.error(EntityEventPolicyMsgs.FAILED_TO_UPDATE_ENTITY_IN_DOCSTORE,
   * spikeEventEntity.getId(), "SYNC_ENTITY"); } } } catch (IOException e) {
   * logger.error(EntityEventPolicyMsgs.FAILED_TO_UPDATE_ENTITY_IN_DOCSTORE,
   * spikeEventEntity.getId(), "SYNC_ENTITY"); } }
   */

}
