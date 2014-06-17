/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.openwire.codec.v4;

import io.openwire.codec.BooleanStream;
import io.openwire.codec.OpenWireFormat;
import io.openwire.commands.DataArrayResponse;
import io.openwire.commands.DataStructure;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class DataArrayResponseMarshaller extends ResponseMarshaller {

    /**
     * Return the type of Data Structure we marshal
     *
     * @return short representation of the type data structure
     */
    @Override
    public byte getDataStructureType() {
        return DataArrayResponse.DATA_STRUCTURE_TYPE;
    }

    /**
     * @return a new object instance
     */
    @Override
    public DataStructure createObject() {
        return new DataArrayResponse();
    }

    /**
     * Un-marshal an object instance from the data input stream
     *
     * @param o
     *        the object to un-marshal
     * @param dataIn
     *        the data input stream to build the object from
     * @throws IOException
     */
    @Override
    public void tightUnmarshal(OpenWireFormat wireFormat, Object o, DataInput dataIn, BooleanStream bs) throws IOException {
        super.tightUnmarshal(wireFormat, o, dataIn, bs);

        DataArrayResponse info = (DataArrayResponse) o;

        if (bs.readBoolean()) {
            short size = dataIn.readShort();
            DataStructure value[] = new DataStructure[size];
            for (int i = 0; i < size; i++) {
                value[i] = tightUnmarsalNestedObject(wireFormat, dataIn, bs);
            }
            info.setData(value);
        } else {
            info.setData(null);
        }
    }

    /**
     * Write the booleans that this object uses to a BooleanStream
     */
    @Override
    public int tightMarshal1(OpenWireFormat wireFormat, Object o, BooleanStream bs) throws IOException {
        DataArrayResponse info = (DataArrayResponse) o;

        int rc = super.tightMarshal1(wireFormat, o, bs);
        rc += tightMarshalObjectArray1(wireFormat, info.getData(), bs);

        return rc + 0;
    }

    /**
     * Write a object instance to data output stream
     *
     * @param o
     *        the instance to be marshaled
     * @param dataOut
     *        the output stream
     * @throws IOException
     *         thrown if an error occurs
     */
    @Override
    public void tightMarshal2(OpenWireFormat wireFormat, Object o, DataOutput dataOut, BooleanStream bs) throws IOException {
        super.tightMarshal2(wireFormat, o, dataOut, bs);

        DataArrayResponse info = (DataArrayResponse) o;
        tightMarshalObjectArray2(wireFormat, info.getData(), dataOut, bs);
    }

    /**
     * Un-marshal an object instance from the data input stream
     *
     * @param o
     *        the object to un-marshal
     * @param dataIn
     *        the data input stream to build the object from
     * @throws IOException
     */
    @Override
    public void looseUnmarshal(OpenWireFormat wireFormat, Object o, DataInput dataIn) throws IOException {
        super.looseUnmarshal(wireFormat, o, dataIn);

        DataArrayResponse info = (DataArrayResponse) o;

        if (dataIn.readBoolean()) {
            short size = dataIn.readShort();
            DataStructure value[] = new DataStructure[size];
            for (int i = 0; i < size; i++) {
                value[i] = looseUnmarsalNestedObject(wireFormat, dataIn);
            }
            info.setData(value);
        } else {
            info.setData(null);
        }
    }

    /**
     * Write the booleans that this object uses to a BooleanStream
     */
    @Override
    public void looseMarshal(OpenWireFormat wireFormat, Object o, DataOutput dataOut) throws IOException {

        DataArrayResponse info = (DataArrayResponse) o;

        super.looseMarshal(wireFormat, o, dataOut);
        looseMarshalObjectArray(wireFormat, info.getData(), dataOut);
    }
}