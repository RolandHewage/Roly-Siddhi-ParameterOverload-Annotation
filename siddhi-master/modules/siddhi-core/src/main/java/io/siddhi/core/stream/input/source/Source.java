/*
 * Copyright (c)  2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.siddhi.core.stream.input.source;

import io.siddhi.core.config.SiddhiAppContext;
import io.siddhi.core.exception.ConnectionUnavailableException;
import io.siddhi.core.util.ExceptionUtil;
import io.siddhi.core.util.StringUtil;
import io.siddhi.core.util.config.ConfigReader;
import io.siddhi.core.util.transport.BackoffRetryCounter;
import io.siddhi.core.util.transport.OptionHolder;
import io.siddhi.query.api.definition.StreamDefinition;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Abstract class to represent Event Sources. Events Sources are the object entry point to Siddhi from external
 * transports. Each source represent a transport type. Whenever Siddhi need to support a new transport, a new Event
 * source should be implemented.
 */
public abstract class Source {
    private static final Logger LOG = Logger.getLogger(Source.class);
    private String type;
    private SourceMapper mapper;
    private StreamDefinition streamDefinition;
    private SiddhiAppContext siddhiAppContext;

    private AtomicBoolean isTryingToConnect = new AtomicBoolean(false);
    private BackoffRetryCounter backoffRetryCounter = new BackoffRetryCounter();
    private AtomicBoolean isConnected = new AtomicBoolean(false);
    private ScheduledExecutorService scheduledExecutorService;
    private ConnectionCallback connectionCallback = new ConnectionCallback();

    public final void init(String sourceType, OptionHolder transportOptionHolder, SourceMapper sourceMapper,
                           String[] transportPropertyNames, ConfigReader configReader, String mapType,
                           OptionHolder mapOptionHolder, List<AttributeMapping> attributeMappings,
                           List<AttributeMapping> transportMappings, ConfigReader mapperConfigReader,
                           SourceHandler sourceHandler, StreamDefinition streamDefinition,
                           SiddhiAppContext siddhiAppContext) {
        this.type = sourceType;

        sourceMapper.init(streamDefinition, mapType, mapOptionHolder, attributeMappings, sourceType,
                (this instanceof SourceSyncCallback) ? (SourceSyncCallback) this : null, transportMappings,
                sourceHandler, mapperConfigReader, siddhiAppContext);
        this.mapper = sourceMapper;
        this.streamDefinition = streamDefinition;
        this.siddhiAppContext = siddhiAppContext;
        init(sourceMapper, transportOptionHolder, transportPropertyNames, configReader, siddhiAppContext);
        scheduledExecutorService = siddhiAppContext.getScheduledExecutorService();
    }

    /**
     * To initialize the source. (This will be called only once, no connection to external systems should be made at
     * this point).
     *
     * @param sourceEventListener             The listener to pass the events for processing which are consumed
     *                                        by the source
     * @param optionHolder                    Contains static options of the source
     * @param requestedTransportPropertyNames Requested transport properties that should be passed to
     *                                        SourceEventListener
     * @param configReader                    System configuration reader for source
     * @param siddhiAppContext                Siddhi application context
     */
    public abstract void init(SourceEventListener sourceEventListener, OptionHolder optionHolder,
                              String[] requestedTransportPropertyNames, ConfigReader configReader,
                              SiddhiAppContext siddhiAppContext);

    /**
     * Get produced event class types
     *
     * @return Array of classes that will be produced by the source,
     * null or empty array if it can produce any type of class.
     */
    public abstract Class[] getOutputEventClasses();

    /**
     * Called to connect to the source backend for receiving events
     *
     * @param connectionCallback Callback to pass the ConnectionUnavailableException for connection failure after
     *                           initial successful connection
     * @throws ConnectionUnavailableException if it cannot connect to the source backend
     */
    public abstract void connect(ConnectionCallback connectionCallback) throws ConnectionUnavailableException;

    /**
     * Called to disconnect from the source backend, or when ConnectionUnavailableException is thrown
     */
    public abstract void disconnect();

    /**
     * Called at the end to clean all the resources consumed
     */
    public abstract void destroy();

    /**
     * Called to pause event consumption
     */
    public abstract void pause();

    /**
     * Called to resume event consumption
     */
    public abstract void resume();

    public void connectWithRetry() {
        if (!isConnected.get()) {
            isTryingToConnect.set(true);
            try {
                connect(connectionCallback);
                isConnected.set(true);
                isTryingToConnect.set(false);
                backoffRetryCounter.reset();
            } catch (ConnectionUnavailableException e) {
                disconnect();
                isConnected.set(false);
                retryWithBackoff(e);
            } catch (RuntimeException e) {
                LOG.error(StringUtil.removeCRLFCharacters(ExceptionUtil.getMessageWithContext(e, siddhiAppContext)) +
                        " Error while connecting at Source '" + StringUtil.removeCRLFCharacters(type) + "' at '" +
                        StringUtil.removeCRLFCharacters(streamDefinition.getId()) + "'.", e);
                throw e;
            }
        }
    }

    private void retryWithBackoff(ConnectionUnavailableException e) {
        LOG.error(ExceptionUtil.getMessageWithContext(e, siddhiAppContext) +
                " Error while connecting at Source '" + StringUtil.removeCRLFCharacters(type) + "' at '" +
                StringUtil.removeCRLFCharacters(streamDefinition.getId()) + "'. Will retry in '" +
                StringUtil.removeCRLFCharacters(backoffRetryCounter.getTimeInterval()) + "'.", e);
        scheduledExecutorService.schedule(new Runnable() {
            @Override
            public void run() {
                connectWithRetry();
            }
        }, backoffRetryCounter.getTimeIntervalMillis(), TimeUnit.MILLISECONDS);
        backoffRetryCounter.increment();
    }

    public final SourceMapper getMapper() {
        return mapper;
    }

    public void shutdown() {
        try {
            disconnect();
            destroy();
        } finally {
            isConnected.set(false);
            isTryingToConnect.set(false);
        }
    }

    public String getType() {
        return type;
    }

    public StreamDefinition getStreamDefinition() {
        return streamDefinition;
    }

    /**
     * Callback class used to pass connection exception during message retrieval
     */
    public class ConnectionCallback {
        public void onError(ConnectionUnavailableException e) {
            disconnect();
            isConnected.set(false);
            retryWithBackoff(e);
        }
    }
}
