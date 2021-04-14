package com.chinafocus.hvrskyworthvr.service.event;

public class VrMediaCancelBluetoothLostDelayTask {
    private static final VrMediaCancelBluetoothLostDelayTask vrConnect = new VrMediaCancelBluetoothLostDelayTask();

    public static VrMediaCancelBluetoothLostDelayTask obtain() {
        return vrConnect;
    }
}
