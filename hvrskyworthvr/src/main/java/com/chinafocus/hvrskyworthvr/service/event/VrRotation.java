package com.chinafocus.hvrskyworthvr.service.event;

public class VrRotation {

    public float x;
    public float y;
    public float z;
    public float w;

    private static final VrRotation vrRotation = new VrRotation();

    public static VrRotation obtain() {
        return vrRotation;
    }
}
