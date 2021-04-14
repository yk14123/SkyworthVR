package com.chinafocus.hvrskyworthvr.service.event;

public class VrMainStartBluetoothLostDelayTask {
    private static final VrMainStartBluetoothLostDelayTask vrConnect = new VrMainStartBluetoothLostDelayTask();

    public static VrMainStartBluetoothLostDelayTask obtain() {
        return vrConnect;
    }
}
