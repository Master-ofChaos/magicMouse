package de.masterOC.main;

import java.awt.Color;

public class Util {
	public static float clamp(float val, float min, float max) {
	    return Math.max(min, Math.min(max, val));
	}
	

	public static Color getColorFromColorCycle(int i) {
		int[] rgbColour = new int[3];
		
		int dec = (i+256*3) / 256 % 3;
		int inc = dec == 2 ? 0 : dec + 1;
		int val = (i+256*3) % 256;
		
		System.out.println(dec + " - " + inc + " - " + val);
		
		rgbColour[3-inc-dec] = 0;
		rgbColour[dec] = 255 - val;
		rgbColour[inc] = val;
		
		return new Color(rgbColour[0], rgbColour[1], rgbColour[2]);
	}
}
