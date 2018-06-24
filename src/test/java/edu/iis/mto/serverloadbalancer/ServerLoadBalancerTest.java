package edu.iis.mto.serverloadbalancer;

import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;
import static edu.iis.mto.serverloadbalancer.ServerBuilder.*;
import static edu.iis.mto.serverloadbalancer.VmBuilder.*;
import static edu.iis.mto.serverloadbalancer.CurrentLoadPertcentageMatcher.*;

public class ServerLoadBalancerTest {

	@Test
	public void balancingServerWithNoVms_serverStaysEmpty() {

		Server theServer = a(server().withCapacity(1));

		balancing(aServerListWith(theServer), anEmptyListOfVms());

		assertThat(theServer, CurrentLoadPertcentageMatcher.hasCurrentLoadOf(0.0d));
	}

	@Test
	public void balancingOneServerWithOneSlotCapacity__andOneSlotVm_fillsServerWithTheVm() {

		Server theServer = a(server().withCapacity(1));
		Vm theVm = a(vm().ofSize(1));

		balancing(aServerListWith(theServer), aVmsListWith(theVm));

		assertThat(theServer, CurrentLoadPertcentageMatcher.hasCurrentLoadOf(100.0d));
		assertThat("server should contain the vm", theServer.contains(theVm));
	}

	@Test
	public void balancingOneServerWithTenSlotsCapacity_andOneSlotVm_fillsTheServerWithTenPercent() {

		Server theServer = a(server().withCapacity(10));
		Vm theVm = a(vm().ofSize(1));

		balancing(aServerListWith(theServer), aVmsListWith(theVm));

		assertThat(theServer, CurrentLoadPertcentageMatcher.hasCurrentLoadOf(10.0d));
		assertThat("server should contain the vm", theServer.contains(theVm));
	}

	@Test
	public void balancingOneServerWithEnoughRoom_fillsTheServerWithAllVms() {

		Server theServer = a(server().withCapacity(100));
		Vm theFirstVm = a(vm().ofSize(1));
		Vm theSecondVm = a(vm().ofSize(1));

		balancing(aServerListWith(theServer), aVmsListWith(theFirstVm, theSecondVm));

		assertThat(theServer, ServerVmsCountMatcher.hasAVmsCountOf(2));
		assertThat("server should contain the first vm", theServer.contains(theFirstVm));
		assertThat("server should contain the second vm", theServer.contains(theSecondVm));
	}

	@Test
	public void vmsShouldBeBalancedOnLessLoadedServerFirst() {

		Server moreLoadedServer = a(server().withCapacity(100).withCurrentLoadOf(50.0d));
		Server lessLoadedServer = a(server().withCapacity(100).withCurrentLoadOf(45.0d));
		Vm theVm = a(VmBuilder.vm().ofSize(10));

		balancing(aServerListWith(moreLoadedServer, lessLoadedServer), aVmsListWith(theVm));

		assertThat("less loaded server should contain the vm", lessLoadedServer.contains(theVm));
		assertThat("more laoded server should not contain the vm", !moreLoadedServer.contains(theVm));
	}

	@Test
	public void balancingServerWithNotEnoughRoom_shouldNotBeFilledWithTheVm() {

		Server theServer = a(server().withCapacity(10).withCurrentLoadOf(90.0d));
		Vm theVm = a(VmBuilder.vm().ofSize(10));

		balancing(aServerListWith(theServer), aVmsListWith(theVm));

		assertThat("server should not contain the vm", !theServer.contains(theVm));
	}

	@Test
	public void balancingServersAndVms() {
		Server server1 = a(server().withCapacity(4));
		Server server2 = a(server().withCapacity(6));

		Vm vm1 = a(vm().ofSize(1));
		Vm vm2 = a(vm().ofSize(4));
		Vm vm3 = a(vm().ofSize(2));

		balancing(aServerListWith(server1, server2), aVmsListWith(vm1, vm2, vm3));

		assertThat("server 1 should contain the vm 1", server1.contains(vm1));
		assertThat("server 2 should contain the vm 2", server2.contains(vm2));
		assertThat("server 1 should contain the vm 3", server1.contains(vm3));

		assertThat(server1, hasCurrentLoadOf(75.0d));
		assertThat(server2, hasCurrentLoadOf(66.66d));
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
