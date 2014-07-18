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
package io.openwire.jms;

import io.openwire.commands.OpenWireStreamMessage;
import io.openwire.utils.ExceptionSupport;
import io.openwire.utils.OpenWireMarshallingSupport;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import javax.jms.JMSException;
import javax.jms.MessageEOFException;
import javax.jms.MessageFormatException;
import javax.jms.MessageNotReadableException;
import javax.jms.StreamMessage;

import org.fusesource.hawtbuf.Buffer;
import org.fusesource.hawtbuf.ByteArrayInputStream;
import org.fusesource.hawtbuf.ByteArrayOutputStream;

/**
 * Wrapper class that provides StreamMessage compliant mappings to the OpenWireStreamMessage
 */
public class OpenWireJMSStreamMessage extends OpenWireJMSMessage implements StreamMessage {

    private final OpenWireStreamMessage message;

    protected transient DataOutputStream dataOut;
    protected transient ByteArrayOutputStream bytesOut;
    protected transient DataInputStream dataIn;
    protected transient int remainingBytes = -1;

    /**
     * Creates a new instance that wraps a new OpenWireMessage instance.
     */
    public OpenWireJMSStreamMessage() {
        this(new OpenWireStreamMessage());
    }

    /**
     * Creates a new instance that wraps the given OpenWireMessage
     *
     * @param message
     *        the OpenWireMessage to wrap.
     */
    public OpenWireJMSStreamMessage(OpenWireStreamMessage message) {
        this.message = message;
    }

    @Override
    public OpenWireJMSStreamMessage copy() throws JMSException {
        OpenWireJMSStreamMessage other = new OpenWireJMSStreamMessage(message.copy());
        copy(other);
        return other;
    }

    private void copy(OpenWireJMSStreamMessage copy) {
        storeContent();
        super.copy(copy);
        copy.dataOut = null;
        copy.bytesOut = null;
        copy.dataIn = null;
    }

    @Override
    public void clearBody() throws JMSException {
        super.clearBody();
        message.clearBody();
        dataOut = null;
        dataIn = null;
        bytesOut = null;
        remainingBytes = -1;
    }

    @Override
    public boolean readBoolean() throws JMSException {
        initializeReading();
        try {
            this.dataIn.mark(10);
            int type = this.dataIn.read();
            if (type == -1) {
                throw new MessageEOFException("reached end of data");
            }
            if (type == OpenWireMarshallingSupport.BOOLEAN_TYPE) {
                return this.dataIn.readBoolean();
            }
            if (type == OpenWireMarshallingSupport.STRING_TYPE) {
                return Boolean.valueOf(this.dataIn.readUTF()).booleanValue();
            }
            if (type == OpenWireMarshallingSupport.NULL) {
                this.dataIn.reset();
                throw new NullPointerException("Cannot convert NULL value to boolean.");
            } else {
                this.dataIn.reset();
                throw new MessageFormatException(" not a boolean type");
            }
        } catch (EOFException e) {
            throw ExceptionSupport.createMessageEOFException(e);
        } catch (IOException e) {
            throw ExceptionSupport.createMessageFormatException(e);
        }
    }

    @Override
    public byte readByte() throws JMSException {
        initializeReading();
        try {
            this.dataIn.mark(10);
            int type = this.dataIn.read();
            if (type == -1) {
                throw new MessageEOFException("reached end of data");
            }
            if (type == OpenWireMarshallingSupport.BYTE_TYPE) {
                return this.dataIn.readByte();
            }
            if (type == OpenWireMarshallingSupport.STRING_TYPE) {
                return Byte.valueOf(this.dataIn.readUTF()).byteValue();
            }
            if (type == OpenWireMarshallingSupport.NULL) {
                this.dataIn.reset();
                throw new NullPointerException("Cannot convert NULL value to byte.");
            } else {
                this.dataIn.reset();
                throw new MessageFormatException(" not a byte type");
            }
        } catch (NumberFormatException mfe) {
            try {
                this.dataIn.reset();
            } catch (IOException ioe) {
                throw ExceptionSupport.create(ioe);
            }
            throw mfe;
        } catch (EOFException e) {
            throw ExceptionSupport.createMessageEOFException(e);
        } catch (IOException e) {
            throw ExceptionSupport.createMessageFormatException(e);
        }
    }

    @Override
    public short readShort() throws JMSException {
        initializeReading();
        try {
            this.dataIn.mark(17);
            int type = this.dataIn.read();
            if (type == -1) {
                throw new MessageEOFException("reached end of data");
            }
            if (type == OpenWireMarshallingSupport.SHORT_TYPE) {
                return this.dataIn.readShort();
            }
            if (type == OpenWireMarshallingSupport.BYTE_TYPE) {
                return this.dataIn.readByte();
            }
            if (type == OpenWireMarshallingSupport.STRING_TYPE) {
                return Short.valueOf(this.dataIn.readUTF()).shortValue();
            }
            if (type == OpenWireMarshallingSupport.NULL) {
                this.dataIn.reset();
                throw new NullPointerException("Cannot convert NULL value to short.");
            } else {
                this.dataIn.reset();
                throw new MessageFormatException(" not a short type");
            }
        } catch (NumberFormatException mfe) {
            try {
                this.dataIn.reset();
            } catch (IOException ioe) {
                throw ExceptionSupport.create(ioe);
            }
            throw mfe;
        } catch (EOFException e) {
            throw ExceptionSupport.createMessageEOFException(e);
        } catch (IOException e) {
            throw ExceptionSupport.createMessageFormatException(e);
        }
    }

    @Override
    public char readChar() throws JMSException {
        initializeReading();
        try {
            this.dataIn.mark(17);
            int type = this.dataIn.read();
            if (type == -1) {
                throw new MessageEOFException("reached end of data");
            }
            if (type == OpenWireMarshallingSupport.CHAR_TYPE) {
                return this.dataIn.readChar();
            }
            if (type == OpenWireMarshallingSupport.NULL) {
                this.dataIn.reset();
                throw new NullPointerException("Cannot convert NULL value to char.");
            } else {
                this.dataIn.reset();
                throw new MessageFormatException(" not a char type");
            }
        } catch (NumberFormatException mfe) {
            try {
                this.dataIn.reset();
            } catch (IOException ioe) {
                throw ExceptionSupport.create(ioe);
            }
            throw mfe;
        } catch (EOFException e) {
            throw ExceptionSupport.createMessageEOFException(e);
        } catch (IOException e) {
            throw ExceptionSupport.createMessageFormatException(e);
        }
    }

    @Override
    public int readInt() throws JMSException {
        initializeReading();
        try {
            this.dataIn.mark(33);
            int type = this.dataIn.read();
            if (type == -1) {
                throw new MessageEOFException("reached end of data");
            }
            if (type == OpenWireMarshallingSupport.INTEGER_TYPE) {
                return this.dataIn.readInt();
            }
            if (type == OpenWireMarshallingSupport.SHORT_TYPE) {
                return this.dataIn.readShort();
            }
            if (type == OpenWireMarshallingSupport.BYTE_TYPE) {
                return this.dataIn.readByte();
            }
            if (type == OpenWireMarshallingSupport.STRING_TYPE) {
                return Integer.valueOf(this.dataIn.readUTF()).intValue();
            }
            if (type == OpenWireMarshallingSupport.NULL) {
                this.dataIn.reset();
                throw new NullPointerException("Cannot convert NULL value to int.");
            } else {
                this.dataIn.reset();
                throw new MessageFormatException(" not an int type");
            }
        } catch (NumberFormatException mfe) {
            try {
                this.dataIn.reset();
            } catch (IOException ioe) {
                throw ExceptionSupport.create(ioe);
            }
            throw mfe;
        } catch (EOFException e) {
            throw ExceptionSupport.createMessageEOFException(e);
        } catch (IOException e) {
            throw ExceptionSupport.createMessageFormatException(e);
        }
    }

    @Override
    public long readLong() throws JMSException {
        initializeReading();
        try {
            this.dataIn.mark(65);
            int type = this.dataIn.read();
            if (type == -1) {
                throw new MessageEOFException("reached end of data");
            }
            if (type == OpenWireMarshallingSupport.LONG_TYPE) {
                return this.dataIn.readLong();
            }
            if (type == OpenWireMarshallingSupport.INTEGER_TYPE) {
                return this.dataIn.readInt();
            }
            if (type == OpenWireMarshallingSupport.SHORT_TYPE) {
                return this.dataIn.readShort();
            }
            if (type == OpenWireMarshallingSupport.BYTE_TYPE) {
                return this.dataIn.readByte();
            }
            if (type == OpenWireMarshallingSupport.STRING_TYPE) {
                return Long.valueOf(this.dataIn.readUTF()).longValue();
            }
            if (type == OpenWireMarshallingSupport.NULL) {
                this.dataIn.reset();
                throw new NullPointerException("Cannot convert NULL value to long.");
            } else {
                this.dataIn.reset();
                throw new MessageFormatException(" not a long type");
            }
        } catch (NumberFormatException mfe) {
            try {
                this.dataIn.reset();
            } catch (IOException ioe) {
                throw ExceptionSupport.create(ioe);
            }
            throw mfe;
        } catch (EOFException e) {
            throw ExceptionSupport.createMessageEOFException(e);
        } catch (IOException e) {
            throw ExceptionSupport.createMessageFormatException(e);
        }
    }

    @Override
    public float readFloat() throws JMSException {
        initializeReading();
        try {
            this.dataIn.mark(33);
            int type = this.dataIn.read();
            if (type == -1) {
                throw new MessageEOFException("reached end of data");
            }
            if (type == OpenWireMarshallingSupport.FLOAT_TYPE) {
                return this.dataIn.readFloat();
            }
            if (type == OpenWireMarshallingSupport.STRING_TYPE) {
                return Float.valueOf(this.dataIn.readUTF()).floatValue();
            }
            if (type == OpenWireMarshallingSupport.NULL) {
                this.dataIn.reset();
                throw new NullPointerException("Cannot convert NULL value to float.");
            } else {
                this.dataIn.reset();
                throw new MessageFormatException(" not a float type");
            }
        } catch (NumberFormatException mfe) {
            try {
                this.dataIn.reset();
            } catch (IOException ioe) {
                throw ExceptionSupport.create(ioe);
            }
            throw mfe;
        } catch (EOFException e) {
            throw ExceptionSupport.createMessageEOFException(e);
        } catch (IOException e) {
            throw ExceptionSupport.createMessageFormatException(e);
        }
    }

    @Override
    public double readDouble() throws JMSException {
        initializeReading();
        try {
            this.dataIn.mark(65);
            int type = this.dataIn.read();
            if (type == -1) {
                throw new MessageEOFException("reached end of data");
            }
            if (type == OpenWireMarshallingSupport.DOUBLE_TYPE) {
                return this.dataIn.readDouble();
            }
            if (type == OpenWireMarshallingSupport.FLOAT_TYPE) {
                return this.dataIn.readFloat();
            }
            if (type == OpenWireMarshallingSupport.STRING_TYPE) {
                return Double.valueOf(this.dataIn.readUTF()).doubleValue();
            }
            if (type == OpenWireMarshallingSupport.NULL) {
                this.dataIn.reset();
                throw new NullPointerException("Cannot convert NULL value to double.");
            } else {
                this.dataIn.reset();
                throw new MessageFormatException(" not a double type");
            }
        } catch (NumberFormatException mfe) {
            try {
                this.dataIn.reset();
            } catch (IOException ioe) {
                throw ExceptionSupport.create(ioe);
            }
            throw mfe;
        } catch (EOFException e) {
            throw ExceptionSupport.createMessageEOFException(e);
        } catch (IOException e) {
            throw ExceptionSupport.createMessageFormatException(e);
        }
    }

    @Override
    public String readString() throws JMSException {
        initializeReading();
        try {
            this.dataIn.mark(65);
            int type = this.dataIn.read();
            if (type == -1) {
                throw new MessageEOFException("reached end of data");
            }
            if (type == OpenWireMarshallingSupport.NULL) {
                return null;
            }
            if (type == OpenWireMarshallingSupport.BIG_STRING_TYPE) {
                return OpenWireMarshallingSupport.readUTF8(dataIn);
            }
            if (type == OpenWireMarshallingSupport.STRING_TYPE) {
                return this.dataIn.readUTF();
            }
            if (type == OpenWireMarshallingSupport.LONG_TYPE) {
                return new Long(this.dataIn.readLong()).toString();
            }
            if (type == OpenWireMarshallingSupport.INTEGER_TYPE) {
                return new Integer(this.dataIn.readInt()).toString();
            }
            if (type == OpenWireMarshallingSupport.SHORT_TYPE) {
                return new Short(this.dataIn.readShort()).toString();
            }
            if (type == OpenWireMarshallingSupport.BYTE_TYPE) {
                return new Byte(this.dataIn.readByte()).toString();
            }
            if (type == OpenWireMarshallingSupport.FLOAT_TYPE) {
                return new Float(this.dataIn.readFloat()).toString();
            }
            if (type == OpenWireMarshallingSupport.DOUBLE_TYPE) {
                return new Double(this.dataIn.readDouble()).toString();
            }
            if (type == OpenWireMarshallingSupport.BOOLEAN_TYPE) {
                return (this.dataIn.readBoolean() ? Boolean.TRUE : Boolean.FALSE).toString();
            }
            if (type == OpenWireMarshallingSupport.CHAR_TYPE) {
                return new Character(this.dataIn.readChar()).toString();
            } else {
                this.dataIn.reset();
                throw new MessageFormatException(" not a String type");
            }
        } catch (NumberFormatException mfe) {
            try {
                this.dataIn.reset();
            } catch (IOException ioe) {
                throw ExceptionSupport.create(ioe);
            }
            throw mfe;
        } catch (EOFException e) {
            throw ExceptionSupport.createMessageEOFException(e);
        } catch (IOException e) {
            throw ExceptionSupport.createMessageFormatException(e);
        }
    }

    @Override
    public int readBytes(byte[] value) throws JMSException {
        initializeReading();
        try {
            if (value == null) {
                throw new NullPointerException();
            }

            if (remainingBytes == -1) {
                this.dataIn.mark(value.length + 1);
                int type = this.dataIn.read();
                if (type == -1) {
                    throw new MessageEOFException("reached end of data");
                }
                if (type != OpenWireMarshallingSupport.BYTE_ARRAY_TYPE) {
                    throw new MessageFormatException("Not a byte array");
                }
                remainingBytes = this.dataIn.readInt();
            } else if (remainingBytes == 0) {
                remainingBytes = -1;
                return -1;
            }

            if (value.length <= remainingBytes) {
                // small buffer
                remainingBytes -= value.length;
                this.dataIn.readFully(value);
                return value.length;
            } else {
                // big buffer
                int rc = this.dataIn.read(value, 0, remainingBytes);
                remainingBytes = 0;
                return rc;
            }
        } catch (EOFException e) {
            JMSException jmsEx = new MessageEOFException(e.getMessage());
            jmsEx.setLinkedException(e);
            throw jmsEx;
        } catch (IOException e) {
            JMSException jmsEx = new MessageFormatException(e.getMessage());
            jmsEx.setLinkedException(e);
            throw jmsEx;
        }
    }

    @Override
    public Object readObject() throws JMSException {
        initializeReading();
        try {
            this.dataIn.mark(65);
            int type = this.dataIn.read();
            if (type == -1) {
                throw new MessageEOFException("reached end of data");
            }
            if (type == OpenWireMarshallingSupport.NULL) {
                return null;
            }
            if (type == OpenWireMarshallingSupport.BIG_STRING_TYPE) {
                return OpenWireMarshallingSupport.readUTF8(dataIn);
            }
            if (type == OpenWireMarshallingSupport.STRING_TYPE) {
                return this.dataIn.readUTF();
            }
            if (type == OpenWireMarshallingSupport.LONG_TYPE) {
                return Long.valueOf(this.dataIn.readLong());
            }
            if (type == OpenWireMarshallingSupport.INTEGER_TYPE) {
                return Integer.valueOf(this.dataIn.readInt());
            }
            if (type == OpenWireMarshallingSupport.SHORT_TYPE) {
                return Short.valueOf(this.dataIn.readShort());
            }
            if (type == OpenWireMarshallingSupport.BYTE_TYPE) {
                return Byte.valueOf(this.dataIn.readByte());
            }
            if (type == OpenWireMarshallingSupport.FLOAT_TYPE) {
                return new Float(this.dataIn.readFloat());
            }
            if (type == OpenWireMarshallingSupport.DOUBLE_TYPE) {
                return new Double(this.dataIn.readDouble());
            }
            if (type == OpenWireMarshallingSupport.BOOLEAN_TYPE) {
                return this.dataIn.readBoolean() ? Boolean.TRUE : Boolean.FALSE;
            }
            if (type == OpenWireMarshallingSupport.CHAR_TYPE) {
                return Character.valueOf(this.dataIn.readChar());
            }
            if (type == OpenWireMarshallingSupport.BYTE_ARRAY_TYPE) {
                int len = this.dataIn.readInt();
                byte[] value = new byte[len];
                this.dataIn.readFully(value);
                return value;
            } else {
                this.dataIn.reset();
                throw new MessageFormatException("unknown type");
            }
        } catch (NumberFormatException mfe) {
            try {
                this.dataIn.reset();
            } catch (IOException ioe) {
                throw ExceptionSupport.create(ioe);
            }
            throw mfe;
        } catch (EOFException e) {
            JMSException jmsEx = new MessageEOFException(e.getMessage());
            jmsEx.setLinkedException(e);
            throw jmsEx;
        } catch (IOException e) {
            JMSException jmsEx = new MessageFormatException(e.getMessage());
            jmsEx.setLinkedException(e);
            throw jmsEx;
        }
    }

    @Override
    public void writeBoolean(boolean value) throws JMSException {
        initializeWriting();
        try {
            OpenWireMarshallingSupport.marshalBoolean(dataOut, value);
        } catch (IOException ioe) {
            throw ExceptionSupport.create(ioe);
        }
    }

    @Override
    public void writeByte(byte value) throws JMSException {
        initializeWriting();
        try {
            OpenWireMarshallingSupport.marshalByte(dataOut, value);
        } catch (IOException ioe) {
            throw ExceptionSupport.create(ioe);
        }
    }

    @Override
    public void writeShort(short value) throws JMSException {
        initializeWriting();
        try {
            OpenWireMarshallingSupport.marshalShort(dataOut, value);
        } catch (IOException ioe) {
            throw ExceptionSupport.create(ioe);
        }
    }

    @Override
    public void writeChar(char value) throws JMSException {
        initializeWriting();
        try {
            OpenWireMarshallingSupport.marshalChar(dataOut, value);
        } catch (IOException ioe) {
            throw ExceptionSupport.create(ioe);
        }
    }

    @Override
    public void writeInt(int value) throws JMSException {
        initializeWriting();
        try {
            OpenWireMarshallingSupport.marshalInt(dataOut, value);
        } catch (IOException ioe) {
            throw ExceptionSupport.create(ioe);
        }
    }

    @Override
    public void writeLong(long value) throws JMSException {
        initializeWriting();
        try {
            OpenWireMarshallingSupport.marshalLong(dataOut, value);
        } catch (IOException ioe) {
            throw ExceptionSupport.create(ioe);
        }
    }

    @Override
    public void writeFloat(float value) throws JMSException {
        initializeWriting();
        try {
            OpenWireMarshallingSupport.marshalFloat(dataOut, value);
        } catch (IOException ioe) {
            throw ExceptionSupport.create(ioe);
        }
    }

    @Override
    public void writeDouble(double value) throws JMSException {
        initializeWriting();
        try {
            OpenWireMarshallingSupport.marshalDouble(dataOut, value);
        } catch (IOException ioe) {
            throw ExceptionSupport.create(ioe);
        }
    }

    @Override
    public void writeString(String value) throws JMSException {
        initializeWriting();
        try {
            if (value == null) {
                OpenWireMarshallingSupport.marshalNull(dataOut);
            } else {
                OpenWireMarshallingSupport.marshalString(dataOut, value);
            }
        } catch (IOException ioe) {
            throw ExceptionSupport.create(ioe);
        }
    }

    @Override
    public void writeBytes(byte[] value) throws JMSException {
        writeBytes(value, 0, value.length);
    }

    @Override
    public void writeBytes(byte[] value, int offset, int length) throws JMSException {
        initializeWriting();
        try {
            OpenWireMarshallingSupport.marshalByteArray(dataOut, value, offset, length);
        } catch (IOException ioe) {
            throw ExceptionSupport.create(ioe);
        }
    }

    @Override
    public void writeObject(Object value) throws JMSException {
        initializeWriting();
        if (value == null) {
            try {
                OpenWireMarshallingSupport.marshalNull(dataOut);
            } catch (IOException ioe) {
                throw ExceptionSupport.create(ioe);
            }
        } else if (value instanceof String) {
            writeString(value.toString());
        } else if (value instanceof Character) {
            writeChar(((Character)value).charValue());
        } else if (value instanceof Boolean) {
            writeBoolean(((Boolean)value).booleanValue());
        } else if (value instanceof Byte) {
            writeByte(((Byte)value).byteValue());
        } else if (value instanceof Short) {
            writeShort(((Short)value).shortValue());
        } else if (value instanceof Integer) {
            writeInt(((Integer)value).intValue());
        } else if (value instanceof Float) {
            writeFloat(((Float)value).floatValue());
        } else if (value instanceof Double) {
            writeDouble(((Double)value).doubleValue());
        } else if (value instanceof byte[]) {
            writeBytes((byte[])value);
        } else if (value instanceof Long) {
            writeLong(((Long)value).longValue());
        } else {
            throw new MessageFormatException("Unsupported Object type: " + value.getClass());
        }
    }

    @Override
    public void reset() throws JMSException {
        storeContent();
        setReadOnlyBody(true);
        this.bytesOut = null;
        this.dataIn = null;
        this.dataOut = null;
        this.remainingBytes = -1;
    }

    // TODO - Compress on store.
    private void initializeWriting() throws JMSException {
        checkReadOnlyBody();
        if (this.dataOut == null) {
            this.bytesOut = new ByteArrayOutputStream();
            OutputStream os = bytesOut;
            if (message.isUseCompression()) {
                message.setCompressed(true);
                os = new DeflaterOutputStream(os);
            }
            this.dataOut = new DataOutputStream(os);
        }

        // For a message that already had a body and was sent we need to restore the content
        // if the message is used again without having its clearBody method called.
        Buffer oldContent = message.getContent();
        if (oldContent != null && !oldContent.isEmpty()) {
            try {
                if (message.isCompressed()) {
                    ByteArrayInputStream input = new ByteArrayInputStream(oldContent.getData(), oldContent.getOffset(), oldContent.getLength());
                    InflaterInputStream inflater = new InflaterInputStream(input);
                    try {
                        byte[] buffer = new byte[8*1024];
                        int read = 0;
                        while ((read = inflater.read(buffer)) != -1) {
                            this.dataOut.write(buffer, 0, read);
                        }
                    } finally {
                        inflater.close();
                    }
                } else {
                    this.dataOut.write(oldContent.getData(), oldContent.getOffset(), oldContent.getLength());
                }

                // Free up the buffer from the old content, will be re-written when
                // tbe message is sent again and storeContent() is called.
                message.setContent(null);
            } catch (IOException ioe) {
                throw ExceptionSupport.create(ioe);
            }
        }
    }

    private void initializeReading() throws MessageNotReadableException {
        checkWriteOnlyBody();
        if (this.dataIn == null) {
            Buffer data = message.getContent();
            if (data == null) {
                data = new Buffer(new byte[] {}, 0, 0);
            }
            InputStream is = new ByteArrayInputStream(data);
            if (message.isCompressed()) {
                is = new InflaterInputStream(is);
                is = new BufferedInputStream(is);
            }
            this.dataIn = new DataInputStream(is);
        }
    }

    private void storeContent() {
        if (dataOut != null) {
            try {
                dataOut.close();
                message.setContent(bytesOut.toBuffer());
                bytesOut = null;
                dataOut = null;
            } catch (IOException ioe) {
                throw new RuntimeException(ioe);
            }
        }
    }
}
