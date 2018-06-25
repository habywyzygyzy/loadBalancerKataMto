package edu.iis.mto.serverloadbalancer;

import java.util.ArrayList;
import java.util.List;

import org.hamcrest.Matcher;

public class Server {

	public static final double MAXIMUM_LOAD = 100.0d;
	private double currenLoadPercentage;
	private int capacity;

	private List<Vm> vms = new ArrayList<Vm>();

	public boolean contains(Vm theVm) {
		return vms.contains(theVm);
	}

	public Server(int capacity) {
		super();
		this.capacity = capacity;
	}

	public void addVm(Vm vm) {
		currenLoadPercentage += loadOfVm(vm);
		this.vms.add(vm);
	}

	private double loadOfVm(Vm vm) {
		return (double) vm.getSize() / (double) capacity * MAXIMUM_LOAD;
	}

	public int countVms() {
		return vms.size();
	}

	public boolean canFit(Vm vm) {
		return currenLoadPercentage + (loadOfVm(vm)) <= MAXIMUM_LOAD;
	}

	public int getCapacity() {
		return capacity;
	}

	public double getCurrenLoadPercentage() {
		return currenLoadPercentage;
	}
}
