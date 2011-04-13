package de.mobile.common.domain;

import java.io.Serializable;


/**
 * A compact, immutable and thread safe representation of IP(4) addresses.
 * 
 * @author alex
 * 
 */
public final class Ip4Address implements Comparable<Ip4Address>, Serializable {

    private static final long serialVersionUID = 1L;

    private final int address;

    public Ip4Address(int number) {
    	this.address = number;
    }

    public static Ip4Address fromString(String value) {
    	return new Ip4Address(aton(value));
    }
    
    public static Ip4Address fromBytes(byte[] bytes) {
    	return fromBytes(bytes, 0);
    }
    
    public static Ip4Address fromBytes(byte[] bytes, int offset) {
    	return new Ip4Address(fromBytes(bytes[offset+0], bytes[offset + 1], bytes[offset + 2], bytes[offset + 3]));
    }
    
    static int fromBytes(byte b1, byte b2, byte b3, byte b4) {
        return b1 << 24 | (b2 & 0xFF) << 16 | (b3 & 0xFF) << 8 | (b4 & 0xFF);
    }
    
    public static int aton(String address) {
        if (address == null) throw new NullPointerException();
        String[] parts = address.split("\\.");
        if (parts.length > 4) throw new IllegalArgumentException();
        int n = 0;
        for (int i = 0, j = 24; i < parts.length; i++, j -= 8) {
            n |= (Integer.valueOf(parts[i]) & 0xff) << j;
        }
        return n;
    }
    
    public byte[] asBytes() {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) ((address >>> 24) & 0xff);
        bytes[1] = (byte) ((address >>> 16) & 0xff);
        bytes[2] = (byte) ((address >>> 8) & 0xff);
        bytes[3] = (byte) ((address) & 0xff);
        return bytes;
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + (int) (address ^ (address >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final Ip4Address other = (Ip4Address) obj;
        if (address != other.address) return false;
        return true;
    }

    @Override
    public String toString() {
        byte[] src = asBytes();
        return (src[0] & 0xff) + "." + (src[1] & 0xff) + "." + (src[2] & 0xff) + "." + (src[3] & 0xff);
    }

    public int compareTo(Ip4Address o) {
        long d  = unsingnedlongValue() - o.unsingnedlongValue();
        return d == 0 ? 0 : (d < 0 ? -1 : 1);
    }

    private Object readResolve() {
        return new Ip4Address(address);
    }
    
    public int intValue() {
    	return address;
    }

    public long unsingnedlongValue() {
        return address & 0xffffffffL;
    }
    
    public static void main(String[] args) {
		Ip4Address a = Ip4Address.fromString("255.255.255.255");
		System.out.println(a.unsingnedlongValue());
	}

}
