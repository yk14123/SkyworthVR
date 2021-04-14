package com.chinafocus.hvrskyworthvr.service.event;

public class VrMainCancelBluetoothLostDelayTask {
    private static final VrMainCancelBluetoothLostDelayTask vrConnect = new VrMainCancelBluetoothLostDelayTask();

    public static VrMainCancelBluetoothLostDelayTask obtain() {
        return vrConnect;
    }
}
