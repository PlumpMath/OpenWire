/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.openwire.utils;

import io.openwire.commands.ConnectionId;
import io.openwire.commands.ConsumerId;
import io.openwire.commands.ProducerId;
import io.openwire.commands.SessionId;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Encapsulates an ActiveMQ compatible OpenWire Session ID and provides methods
 * for creating consumer and producer ID objects that are children of this session.
 */
public class OpenWireSessionId {

    private final SessionId sessionId;

    private final AtomicLong consumerIdGenerator = new AtomicLong(1);
    private final AtomicLong producerIdGenerator = new AtomicLong(1);
    private final AtomicLong deliveryIdGenerator = new AtomicLong(1);

    /**
     * Creates a new OpenWireSessionId instance with the given ID.
     *
     * @param sessionId
     *        the SessionId assigned to this instance.
     */
    public OpenWireSessionId(SessionId sessionId) {
        this.sessionId = sessionId;
    }

    /**
     * Creates a new OpenWireSessionId instance based on the given ConnectionId
     * and a session sequence number.
     *
     * @param connectionId
     *        the ConnectionId to use for this Session ID.
     * @param sequence
     *        the sequence number that identifies this Session instance.
     */
    public OpenWireSessionId(ConnectionId connectionId, long sequence) {
        this(new SessionId(connectionId, sequence));
    }

    /**
     * @return the fixed SessionId of this OpenWireSessionId instance.
     */
    public SessionId getSessionId() {
        return sessionId;
    }

    /**
     * @return the next ConsumerId instance for the managed SessionId.
     */
    public ConsumerId getNextConsumerId() {
        return new ConsumerId(sessionId, consumerIdGenerator.getAndIncrement());
    }

    /**
     * @return the next ProducerId instance for the managed SessionId.
     */
    public ProducerId getNextProducerId() {
        return new ProducerId(sessionId, producerIdGenerator.getAndIncrement());
    }

    /**
     * @return the next Id to assign incoming message deliveries from the managed session Id.
     */
    public long getNextDeliveryId() {
        return this.deliveryIdGenerator.getAndIncrement();
    }

    @Override
    public String toString() {
        return sessionId.toString();
    }
}