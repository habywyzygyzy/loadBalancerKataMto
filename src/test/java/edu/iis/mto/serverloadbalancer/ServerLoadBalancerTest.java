package edu.iis.mto.serverloadbalancer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.hamcrest.Matcher;
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
	public void balancingOneServerWithTenSlotsCapacity_andOneSlotVm_fillsTheServerWithTenPercent() {

		Server theServer = a(ServerBuilder.server().withCapacity(100));
		Vm theFirstVm = a(VmBuilder.vm().ofSize(1));
		Vm theSecondVm = a(VmBuilder.vm().ofSize(1));

		balancing(aServerListWith(theServer), aVmsListWith(theFirstVm, theSecondVm));

		assertThat(theServer, hasAVmsCountOf(2));
		assertThat("server should contain the first vm", theServer.contains(theFirstVm));
		assertThat("server should contain the second vm", theServer.contains(theSecondVm));
	}

	private Matcher<? super Server> hasAVmsCountOf(int expectedVmsCount) {
		return new ServerVmsCountMatcher(expectedVmsCount);
	}

	@Test
	public void balancingOneServerWithEnoughRoom_fillsTheServerWithAllVms() {

		Server theServer = a(ServerBuilder.server().withCapacity(10));
		Vm theVm = a(VmBuilder.vm().ofSize(1));

		balancing(aServerListWith(theServer), aVmsListWith(theVm));

		assertThat(theServer, CurrentLoadPertcentageMatcher.hasCurrentLoadOf(10.0d));
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
