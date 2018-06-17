package edu.iis.mto.serverloadbalancer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.Test;

public class ServerLoadBalancerTest {

	@Test
	public void balancingServerWithNoVms_serverStaysEmpty() {

		Server theServer = a(ServerBuilder.server().withCapacity(1));

		balancing(aServerListWith(theServer), anEmptyListOfVms());

		assertThat(theServer, CurrentLoadPertcentageMatcher.hasCurrentLoadOf(0.0d));
	}

	@Test
	public void balancingOneServerWithOneSlotCapacity__andOneSlotVm_fillsServerWithTheVm() {

		Server theServer = a(ServerBuilder.server().withCapacity(1));
		Vm theVm = a(VmBuilder.vm().ofSize(1));

		balancing(aServerListWith(theServer), aVmsListWith(theVm));

		assertThat(theServer, CurrentLoadPertcentageMatcher.hasCurrentLoadOf(100.0d));
		assertThat("server should contain the vm", theServer.contains(theVm));
	}

	@Test
	public void itCompiles() {
		assertThat(true, equalTo(true));

	}

	private Vm[] aVmsListWith(Vm... vms) {
		return vms;
	}

	private void balancing(Server[] servers, Vm[] vms) {
		new ServerLoadBalancer().balance(servers, vms);
	}

	private Vm[] anEmptyListOfVms() {
		return new Vm[0];
	}

	private Server[] aServerListWith(Server... servers) {
		return servers;
	}

	private <T> T a(Builder<T> builder) {
		return builder.build();
	}
}
