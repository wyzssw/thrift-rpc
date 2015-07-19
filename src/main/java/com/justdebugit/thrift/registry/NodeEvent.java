package com.justdebugit.thrift.registry;

import org.apache.commons.lang3.tuple.ImmutablePair;


public class NodeEvent {
	
	public enum EventType {
		/**
		 * CHILD_ADDED,   节点增加
		 * CHILD_UPDATED, 子节点内容发生变化
		 * CHILD_REMOVED, 子节点被删除
		 * NODE_CHANAGED  节点发生变化
		 */
		CHILD_ADDED,CHILD_UPDATED,CHILD_REMOVED,NODE_CHANAGED
	}
	
	private final EventType type;
	private final ImmutablePair<String, byte[]> data;
	
    public NodeEvent(EventType type,ImmutablePair<String, byte[]> data ) {
		this.type = type;
		this.data = data;
	}

    public EventType getType() {
		return type;
	}
	public ImmutablePair<String, byte[]> getData() {
		return data;
	}
}
