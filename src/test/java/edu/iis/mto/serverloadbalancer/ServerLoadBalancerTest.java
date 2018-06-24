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
	public void balancingOneServerWithTenSlotsCapacity_andOneSlotVm_fillsTheServerWithTenPercent() {

		Server theServer = a(ServerBuilder.server().withCapacity(10));
		Vm theVm = a(VmBuilder.vm().ofSize(1));

		balancing(aServerListWith(theServer), aVmsListWith(theVm));

		assertThat(theServer, CurrentLoadPertcentageMatcher.hasCurrentLoadOf(10.0d));
		assertThat("server should contain the vm", theServer.contains(theVm));
	}

	@Test
	public void balancingOneServerWithEnoughRoom_fillsTheServerWithAllVms() {

		Server theServer = a(ServerBuilder.server().withCapacity(100));
		Vm theFirstVm = a(VmBuilder.vm().ofSize(1));
		Vm theSecondVm = a(VmBuilder.vm().ofSize(1));

		balancing(aServerListWith(theServer), aVmsListWith(theFirstVm, theSecondVm));

		assertThat(theServer, ServerVmsCountMatcher.hasAVmsCountOf(2));
		assertThat("server should contain the first vm", theServer.contains(theFirstVm));
		assertThat("server should contain the second vm", theServer.contains(theSecondVm));
	}

	@Test
	public void vmsShouldBeBalancedOnLessLoadedServerFirst() {

		Server moreLoadedServer = a(ServerBuilder.server().withCapacity(100).withCurrentLoadOf(50.0d));
		Server lessLoadedServer = a(ServerBuilder.server().withCapacity(100).withCurrentLoadOf(45.0d));
		Vm theVm = a(VmBuilder.vm().ofSize(10));

		balancing(aServerListWith(moreLoadedServer, lessLoadedServer), aVmsListWith(theVm));

		assertThat("less loaded server should contain the vm", lessLoadedServer.contains(theVm));
		assertThat("more laoded server should not contain the vm", !moreLoadedServer.contains(theVm));
	}

	@Test
	public void balancingServerWithNotEnoughRoom_shouldNotBeFilledWithTheVm() {

		Server theServer = a(ServerBuilder.server().withCapacity(10).withCurrentLoadOf(90.0d));
		Vm theVm = a(VmBuilder.vm().ofSize(10));

		balancing(aServerListWith(theServer), aVmsListWith(theVm));

		assertThat("server should not contain the vm", !theServer.contains(theVm));
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
