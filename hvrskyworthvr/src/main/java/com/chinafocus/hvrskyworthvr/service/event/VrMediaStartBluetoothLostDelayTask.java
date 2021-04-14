package com.chinafocus.hvrskyworthvr.service.event;

public class VrMediaStartBluetoothLostDelayTask {
    private static final VrMediaStartBluetoothLostDelayTask vrConnect = new VrMediaStartBluetoothLostDelayTask();

    public static VrMediaStartBluetoothLostDelayTask obtain() {
        return vrConnect;
    }
}
