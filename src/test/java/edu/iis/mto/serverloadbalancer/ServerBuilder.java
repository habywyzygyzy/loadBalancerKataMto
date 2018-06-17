package edu.iis.mto.serverloadbalancer;

public class ServerBuilder implements Builder<Server> {

	public static ServerBuilder server() {
		return new ServerBuilder();
	}

	private int capacity;

	public Server build() {
		return new Server(capacity);
	}

	public ServerBuilder withCapacity(int capacity) {
		this.capacity = capacity;
		return this;
	}

}
