package com.dubboclub.dk.admin.model;

/**
 * Created by bieber on 2015/6/6.
 */
public class Node extends BasicModel {

    private String nodeAddress;


    public String getNodeAddress() {
        return nodeAddress;
    }

    public void setNodeAddress(String nodeAddress) {
        this.nodeAddress = nodeAddress;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Node node = (Node) o;

        if (nodeAddress != null ? !nodeAddress.equals(node.nodeAddress) : node.nodeAddress != null) return false;

        return true;
    }

    public int hashCode() {
        return nodeAddress != null ? nodeAddress.hashCode() : 0;
    }
}
