package de.mobile.olaf.api;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public final class Message {

	private final static int version = 1;
    private final int ip;
    private final int clientId;
    private final int eventId;
    private final long time;

    Message(int ip, int clientId, int eventId, long time) {
        this.ip = ip;
        this.clientId = clientId;
        this.eventId = eventId;
        this.time = time;
    }
    
    public byte[] asBytes() {
    	try {
    		ByteArrayOutputStream bos = new ByteArrayOutputStream();
    		DataOutputStream dos = new DataOutputStream(bos);
    		dos.writeShort(version);
    		dos.writeLong(time);
    		dos.writeInt(clientId);
    		dos.writeShort(eventId);
    		dos.writeInt(ip);
    		dos.flush();
    		return bos.toByteArray();
    	} catch (IOException unlikely) {
    		throw new RuntimeException(unlikely);
    	}
    }
    
    public static Message fromBytes(byte[] bytes) {
    	ByteBuffer buffer = ByteBuffer.wrap(bytes);
    	@SuppressWarnings("unused")
    	int version = buffer.getShort();
    	long time = buffer.getLong();
    	int clientId = buffer.getInt();
    	int eventId = buffer.getShort();
    	int ip = buffer.getInt();
    	return new Message(ip, clientId, eventId, time);
    }
    
    private Message(Builder builder) {
        this(builder.ip, builder.clientId, builder.eventId, builder.time);   
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public Builder asBuilder() {
        return newBuilder()
            .setIp(this.ip)
            .setClientId(this.clientId)
            .setEventId(this.eventId)
            .setTime(this.time);
    }

    // GETTERS
    public int getIp() {
        return ip;
    }

    public int getClientId() {
        return clientId;
    }

    public int getEventId() {
        return eventId;
    }

    public long getTime() {
        return time;
    }
    
    public int getVersion() {
		return version;
	}

    public Message clone() {
        try {
            return (Message) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
        
    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ip;
        result = PRIME * result + clientId;
        result = PRIME * result + eventId;
        result = PRIME * result + (int) (time ^ (time >>> 32));
        return result;
    }
        
    @Override 
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Message other = (Message) obj;
        if (ip != other.ip)
        	return false;
        if (clientId != other.clientId)
            return false;
        if (eventId != other.eventId)
            return false;
        if (time != other.time)
            return false;
        return true;
    }
    
    @Override
	public String toString() {
		return "Message [version=" + version + ", time=" + time + ", clientId=" + clientId
				+ ", eventId=" + eventId + ", ip=" + ip + "]";
	}

	public static class Builder {
        private int ip;
        private int clientId;
        private int eventId;
        private long time;
    	
    	public Builder setIp(String ip) {
    	    this.ip = aton(ip); return this;
    	}
    	
    	public Builder setIp(int ip) {
    		this.ip = ip; return this;
    	}
    	
    	public Builder setClientId(int clientId) {
    	    this.clientId = clientId; return this;
    	}
    	
    	public Builder setEventId(int eventId) {
    	    this.eventId = eventId; return this;
    	}
    	
    	public Builder setTime(long time) {
    	    this.time = time; return this;
    	}
    	
    	public Message build() {
    	    return new Message(this);
    	}
    }

	static int aton(String address) {
		if (address == null)
			throw new NullPointerException();
		String[] parts = address.split("\\.");
		if (parts.length > 4)
			throw new IllegalArgumentException();
		int n = 0;
		for (int i = 0, j = 24; i < parts.length; i++, j -= 8) {
			n |= (Integer.valueOf(parts[i]) & 0xff) << j;
		}
		return n;
	}

}