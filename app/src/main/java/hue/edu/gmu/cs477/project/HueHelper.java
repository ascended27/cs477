package hue.edu.gmu.cs477.project;

import com.philips.lighting.hue.sdk.PHHueSDK;

public class HueHelper {

    private final PHHueSDK phHueSDK;

    public HueHelper(){
        phHueSDK = PHHueSDK.getInstance();
    }

}
