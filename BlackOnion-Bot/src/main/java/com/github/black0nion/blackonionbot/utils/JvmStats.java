package com.github.black0nion.blackonionbot.utils;

import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;

public class JvmStats {

    private static final Runtime instance = Runtime.getRuntime();

    public static long totalMemory() {
	return instance.totalMemory();
    }

    public static long freeMemory() {
	return instance.freeMemory();
    }

    public static long usedMemory() {
	return instance.totalMemory() - instance.freeMemory();
    }

    public static long maxMemory() {
	return instance.maxMemory();
    }

    private static final OperatingSystem os = new SystemInfo().getOperatingSystem();
    private static final int processId = os.getProcessId();
    private static final SystemInfo systemInfo = new SystemInfo();
    private static final HardwareAbstractionLayer hal = systemInfo.getHardware();
    private static final int logicalProcessors = hal.getProcessor().getLogicalProcessorCount();
    private static final OSProcess process = os.getProcess(processId);

    public static void test() {
	try {
	    process.updateAttributes();
	    System.out.println(String.format("%.1f", (100d * process.getProcessCpuLoadCumulative() / logicalProcessors)));
	    hal.getNetworkIFs().forEach(c -> System.out.println(c));
	    System.out.println(Thread.activeCount());
	} catch (final Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }
}