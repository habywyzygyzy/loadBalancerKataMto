package edu.iis.mto.serverloadbalancer;

import static org.hamcrest.MatcherAssert.assertThat;
import static edu.iis.mto.serverloadbalancer.ServerBuilder.*;
import static edu.iis.mto.serverloadbalancer.CurrentLoadPercentageMatcher.*;
import static org.hamcrest.Matchers.equalTo;
import static edu.iis.mto.serverloadbalancer.VmBuilder.*;
import static edu.iis.mto.serverloadbalancer.ServerVmsCountMatcher.*;

import org.hamcrest.Matcher;
import org.junit.Test;

public class ServerLoadBalancerTest {
	@Test
	public void itCompiles() {
		assertThat(true, equalTo(true));
	}

	@Test
	public void balancingServerWithNoVms_serverStaysEmpty() {

		Server theServer = a(server().withCapacity(1));

		balancing(aServersListWith(theServer), anEmptyListOfVms());
		assertThat(theServer, hasCurrentLoadOf(0.0d));
	}

	@Test
	public void balancingOneServerWithOneSlotCapacity_addOneSlotVm_fillsServerWithTheVm() {
		Server theServer = a(server().withCapacity(1));
		Vm theVm = a(vm().ofSize(1));

		balancing(aServersListWith(theServer), aVmsListWith(theVm));
		assertThat(theServer, hasCurrentLoadOf(100.0d));
		assertThat("server should contain the vm", theServer.contains(theVm));
	}

	@Test
	public void balancingOneServerWithTenSlotsCapacity_addOneSlotVm_fillsTheServerWithTenPercent() {
		Server theServer = a(server().withCapacity(10));
		Vm theVm = a(vm().ofSize(1));

		balancing(aServersListWith(theServer), aVmsListWith(theVm));
		assertThat(theServer, hasCurrentLoadOf(10.0d));
		assertThat("server should contain the vm", theServer.contains(theVm));
	}

	@Test
	public void balancingOneServerWithEnoughRoom_fillsTheServerWithAllVms() {
		Server theServer = a(server().withCapacity(100));
		Vm vm1 = a(vm().ofSize(1));
		Vm vm2 = a(vm().ofSize(1));

		balancing(aServersListWith(theServer), aVmsListWith(vm1, vm2));

		assertThat(theServer, hasAVmsCountOf(2));
		assertThat("server should contain the vm1", theServer.contains(vm1));
		assertThat("server should contain the vm2", theServer.contains(vm2));
	}

	@Test
	public void vmsShouldBeBalancedOnLessLoadedServerFirst() {
		Server lessLoadedServer = a(server().withCapacity(100).withCurrentLoadOf(45.0d));
		Server moreLoadedServer = a(server().withCapacity(100).withCurrentLoadOf(50.0d));
		Vm theVm = a(vm().ofSize(10));

		balancing(aServersListWith(lessLoadedServer, moreLoadedServer), aVmsListWith(theVm));

		assertThat("less loaded server should contain the vm", lessLoadedServer.contains(theVm));
		assertThat("more loaded server should not contain the vm", !moreLoadedServer.contains(theVm));
	}

	@Test
	public void balancingServerWithNotEnoughRoom_shouldNotBeFilledWithTheVm() {
		Server theServer = a(server().withCapacity(10).withCurrentLoadOf(90.0d));
		Vm theVm = a(vm().ofSize(2));

		balancing(aServersListWith(theServer), aVmsListWith(theVm));

		assertThat("the server should not contain the vm", !theServer.contains(theVm));
	}

	@Test
	public void balancingServersAndVms() {
		Server server1 = a(server().withCapacity(4));
		Server server2 = a(server().withCapacity(6));
		Vm vm1 = a(vm().ofSize(1));
		Vm vm2 = a(vm().ofSize(4));
		Vm vm3 = a(vm().ofSize(2));

		balancing(aServersListWith(server1, server2), aVmsListWith(vm1, vm2, vm3));
		assertThat(server1, hasCurrentLoadOf(75.0d));
		assertThat(server2, hasCurrentLoadOf(66.66d));
		assertThat("server1 should contain the vm1", server1.contains(vm1));
		assertThat("server2 should contain the vm2", server2.contains(vm2));
		assertThat("server1 should contain the vm3", server1.contains(vm3));
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

	private Server[] aServersListWith(Server... servers) {
		return servers;
	}

	private <T> T a(Builder<T> builder) {
		return builder.build();
	}

}
