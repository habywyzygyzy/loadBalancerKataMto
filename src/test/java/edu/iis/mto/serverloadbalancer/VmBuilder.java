package edu.iis.mto.serverloadbalancer;

public class VmBuilder implements Builder<Vm> {

	private int size;

	public VmBuilder ofSize(int size) {
		this.size = size;
		return this;
	}

	public static VmBuilder vm() {
		return new VmBuilder();
	}

	public Vm build() {
		return new Vm(size);
	}

}
