package com.dubboclub.admin.model;

/**
 * Created by bieber on 2015/6/6.
 */
public class Node extends BasicModel {

    public static final short PROVIDER_TYPE=1,CONSUMER_TYPE=2;

    private String nodeAddress;

    private short type;

    public String getNodeAddress() {
        return nodeAddress;
    }

    public void setNodeAddress(String nodeAddress) {
        this.nodeAddress = nodeAddress;
    }

    public short getType() {
        return type;
    }

    public void setType(short type) {
        this.type = type;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Node node = (Node) o;

        if (type != node.type) return false;
        if (nodeAddress != null ? !nodeAddress.equals(node.nodeAddress) : node.nodeAddress != null) return false;

        return true;
    }

    public int hashCode() {
        int result = nodeAddress != null ? nodeAddress.hashCode() : 0;
        result = 31 * result + (int) type;
        return result;
    }
}
