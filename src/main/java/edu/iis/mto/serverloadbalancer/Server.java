package edu.iis.mto.serverloadbalancer;

import java.util.ArrayList;
import java.util.List;

import org.hamcrest.Matcher;

public class Server {

	public static final double MAXIMUM_LOAD = 100.0d;
	public double currenLoadPercentage;
	public int capacity;

	private List<Vm> vms = new ArrayList<Vm>();

	public boolean contains(Vm theVm) {
		return vms.contains(theVm);
	}

	public Server(int capacity) {
		super();
		this.capacity = capacity;
	}

	public void addVm(Vm vm) {
		currenLoadPercentage = (double) vm.size / (double) capacity * MAXIMUM_LOAD;
		this.vms.add(vm);
	}

	public int countVms() {
		return vms.size();
	}

	public boolean canFit(Vm vm) {
		return currenLoadPercentage + ((double) vm.size / this.capacity * MAXIMUM_LOAD) <= MAXIMUM_LOAD;
	}

}
