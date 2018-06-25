package edu.iis.mto.serverloadbalancer;

import static edu.iis.mto.serverloadbalancer.CurrentLoadPercentageMatcher.hasLoadPercentageOf;
import static edu.iis.mto.serverloadbalancer.ServerBuilder.server;
import static edu.iis.mto.serverloadbalancer.ServerVmsCountMatcher.hasVmsCountOf;
import static edu.iis.mto.serverloadbalancer.VmBuilder.vm;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class ServerLoadBalancerParametrizedTest extends ServerLoadBalancerBaseTest {

	private int serverCapacity;
	private int vmSize;

	public ServerLoadBalancerParametrizedTest(int serverCapacity, int vmSize) {
		super();
		this.serverCapacity = serverCapacity;
		this.vmSize = vmSize;
	}

	@Parameters
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] { { 1, 1 }, { 10, 1 }, { 4, 1 }, { 5, 1 }, { 10, 2 }, { 50, 5 } });
	}

	@Test
	public void balancingOneServerWithParamterizedCapacity_andParamterizedSlotVm_fillsTheServerWithTheVm() {
		Server theServer = a(server().withCapacity(serverCapacity));
		Vm theVm = a(vm().ofSize(vmSize));
		balance(aListOfServersWith(theServer), aListOfVmsWith(theVm));

		assertThat(theServer, hasLoadPercentageOf((double) vmSize / (double) serverCapacity * 100.0d));
		assertThat("the server should contain vm", theServer.contains(theVm));
	}

}
