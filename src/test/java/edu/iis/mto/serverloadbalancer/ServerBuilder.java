package edu.iis.mto.serverloadbalancer;

public class ServerBuilder implements Builder<Server> {

	public static ServerBuilder server() {
		return new ServerBuilder();
	}

	private int capacity;
	private double initialLoad;

	public Server build() {
		Server server = new Server(capacity);
		addInitialLoad(server);
		return server;
	}

	private void addInitialLoad(Server server) {
		if (initialLoad > 0) {
			int initialVmSize = (int) (initialLoad / capacity * Server.MAXIMUM_LOAD);
			Vm initialVm = VmBuilder.vm().ofSize(initialVmSize).build();
			server.addVm(initialVm);
		}
	}

	public ServerBuilder withCapacity(int capacity) {
		this.capacity = capacity;
		return this;
	}

	public ServerBuilder withCurrentLoadOf(double initialLoad) {
		this.initialLoad = initialLoad;
		return this;
	}

}
