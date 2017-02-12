package utils;

/**
 * Created by haydenchristensen on 2/11/17.
 */

import java.awt.*;

/**
 * A basic implementation of a color in the HSVK space, compatible with LIFX
 * bulbs. This implementation uses colors ranging from {@link #MIN_VALUE}
 * (0x0000) to {@link #MAX_VALUE} (0xFFFF)
 */
public class LifxColor {

    public static final int MIN_VALUE = 0x0000;
    public static final int MAX_VALUE = 0xFFFF;
    public static final int DEFAULT_KELVIN = 3500;


    private int hue; //0-360
    private float saturation; //0.0-1.0
    private float brightness; //0.0-1.0
    private int kelvin; //2500-9000

    public int getHue(){
        return hue;
    }
    public float getSaturation(){
        return saturation;
    }
    public float getBrightness(){
        return brightness;
    }
    public int getKelvin(){
        return kelvin;
    }

    /**
     * Creates a new LIFXColor with unspecified values.
     */
    public LifxColor() {
    }

//	/**
//	 * Creates a new LIFXColor using values from the given light status packet.
//	 * @param packet the packet to copy values from
//	 */
//	public LIFXColor(LightStatusResponse packet) {
//		hue = packet.getHue();
//		saturation = packet.getSaturation();
//		value = packet.getBrightness();
//		kelvin = packet.getKelvin();
//	}

    /**
     * Creates a new LIFXColor using the specified values.
     * @param hue the hue of the color
     * @param saturation the saturation of the color
     * @param brightness The the `brightness` parameter for the HSVK color
     * @param kelvin The kelvin value for the HSVK color. See
     *     {@link #DEFAULT_KELVIN}.
     */
    public LifxColor(int hue, float saturation, float brightness, int kelvin) {
        this.hue = hue;
        this.saturation = saturation;
        this.brightness = brightness;
        this.kelvin = kelvin;
    }

    /**
     * Returns a new LIFXColor with the given hue value, copying other
     * parameters from this instance.
     * @param hue the hue value to use
     * @return a copy of this color with the given hue parameter
     */
    public LifxColor hue(int hue) {
        return new LifxColor(hue, this.saturation, this.brightness, this.kelvin);
    }

    /**
     * Returns a new LIFXColor with the given saturation value, copying other
     * parameters from this instance.
     * @param saturation the saturation value to use
     * @return a copy of this color with the given saturation parameter
     */
    public LifxColor saturation(float saturation) {
        return new LifxColor(this.hue, saturation, this.brightness, this.kelvin);
    }

    /**
     * Returns a new LIFXColor with the given {@code value} parameter, copying
     * other fields from this instance.
     * @param brightness the value parameter to use
     * @return a copy of this color with the given value parameter
     */
    public LifxColor value(float brightness) {
        return new LifxColor(this.hue, this.saturation, brightness, this.kelvin);
    }

    /**
     * Returns a new LIFXColor with the given kelvin value, copying other
     * parameters from this instance.
     * @param kelvin the kelvin value to use
     * @return a copy of this color with the given kelvin parameter
     */
    public LifxColor kelvin(int kelvin) {
        return new LifxColor(this.hue, this.saturation, this.brightness, kelvin);
    }

//    /**
//     * Creates a new LIFXColor in the HSV space from the given red/green/blue
//     * values. Note that values should fall in the range of 0...255 (inclusive).
//     * The kelvin will be a default of {@link #DEFAULT_KELVIN}.
//     * @param red the red component of the color
//     * @param green the green component of the color
//     * @param blue the blue component of the color
//     * @return a LIFXColor based on the given values
//     */
//    public static LifxColor fromRGB(int red, int green, int blue) {
//        float[] hsb = Color.RGBtoHSB(red, green, blue, null);
//        float hue = hsb[0];
//        float saturation = hsb[1];
//        float brightness = hsb[2];
//
//        return new LifxColor((int)hue, saturation, brightness, DEFAULT_KELVIN);
//    }

}
