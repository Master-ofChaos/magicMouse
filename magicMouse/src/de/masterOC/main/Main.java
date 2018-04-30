package de.masterOC.main;

import java.awt.MouseInfo;
import java.awt.Robot;
import java.awt.Toolkit;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle;
import org.knowm.xchart.style.markers.SeriesMarkers;

import com.fazecast.jSerialComm.SerialPort;

public class Main {
	static SerialPort choosenPort = null;
	static double summ=0;
	static int ym = 0;
	static int mx=0, my=0;
	
	public static void main(String[] args) {
		String portName = "COM4";
		
		SerialPort ports[] = SerialPort.getCommPorts();
		
		for(SerialPort port : ports) {
			System.out.println(port.getSystemPortName());
			
			if(port.getSystemPortName().equals(portName)) {
				choosenPort = port;
			}
		}
		
		if(choosenPort.openPort()) {
			System.out.println("Port opened!");
			choosenPort.setComPortTimeouts(SerialPort.TIMEOUT_NONBLOCKING, 0, 0);
			choosenPort.setBaudRate(115200);
			double[][] initDouble = {{0}, {0}};
			
			XYChart chart = QuickChart.getChart("Acceleration (height)", "t in s", "a in m/s^2", "X", initDouble[0], initDouble[1]);
			chart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Line);
			chart.addSeries("Y", initDouble[0], initDouble[1]).setMarker(SeriesMarkers.NONE);
			chart.addSeries("Z", initDouble[0], initDouble[1]).setMarker(SeriesMarkers.NONE);
			SwingWrapper<XYChart> sw = new SwingWrapper<XYChart>(chart);
			sw.displayChart();
			boolean ready = false;
			
			String data = "";
			
			int index=0;
			
			List<Double> dataX = new ArrayList<Double>();
			
			List<Double> dataYX = new ArrayList<Double>();
			List<Double> dataYY = new ArrayList<Double>();
			List<Double> dataYZ = new ArrayList<Double>();
			
			try{
				while(true){
					while (choosenPort.bytesAvailable() == 0) {
						Thread.sleep(20);
					}

			      	byte[] readBuffer = new byte[choosenPort.bytesAvailable()];
			      	int numRead = choosenPort.readBytes(readBuffer, readBuffer.length);
			      	//System.out.println("Read " + numRead + " bytes.");
			      	
			      	String input = new String(readBuffer);
			      	//System.out.print(input);
			      	
			      	if(ready){
			      		data += input;
			      		//System.out.println("(" + data + ")");
			      		
			      		if(data.contains(System.getProperty("line.separator"))) {
			      			String[] splitData = data.split("\\n");
			      			data = splitData[splitData.length-1];
			      			
			      			if(splitData.length > 1) {
			      				
			      				
			      				for(int i=0; i<splitData.length-1; i++) {
			      					//System.out.println(splitData[i].trim());
			      					//System.out.println("--");
			      					
			      					if(!splitData[i].trim().equals("")) {
				      					String[] splitSplitData = splitData[i].replace("ypr", "").trim().split("\t");
				      					
				      					dataX.add((double) index);
				      					
				      					dataYX.add(Double.parseDouble(splitSplitData[0]));
				      					dataYY.add(Double.parseDouble(splitSplitData[1]));
				      					dataYZ.add(Double.parseDouble(splitSplitData[2]));
				      					
				      					if(index > 100) {
				      						//dataX.remove(0);
				      						//dataYZ.remove(0);
				      						//System.out.println(dataX.size());
				      					}
				      					
				      					chart.updateXYSeries("X", dataX, dataYX, null);
				      					//chart.updateXYSeries("Y", dataX, dataYY, null);
				      					//chart.updateXYSeries("Z", dataX, dataYZ, null);
				      					
				      					Robot r = new Robot();
				      					//r.mouseMove((int) (MouseInfo.getPointerInfo().getLocation().x + Double.parseDouble(splitSplitData[0]) / 1000), MouseInfo.getPointerInfo().getLocation().y);
				      					//System.out.println(Double.parseDouble(splitSplitData[0]) / 1000);
				      					
				      					//if(index>1000){
				      						
				      						r.mouseMove((int) (dataYX.get(dataYX.size()-1) * 16) - mx  + MouseInfo.getPointerInfo().getLocation().x, 
				      								(int) (-dataYZ.get(dataYZ.size()-1) * 10) - my + MouseInfo.getPointerInfo().getLocation().y);
				      						//System.out.println(-dataYZ.get(dataYZ.size()-1) * 2  + Toolkit.getDefaultToolkit().getScreenSize().getHeight()/2);
				      						
				      						mx = (int) (dataYX.get(dataYX.size()-1) * 16);
				      						my = (int) (-dataYZ.get(dataYZ.size()-1) * 10);
				      					//}
				      					
				      					sw.repaintChart();
				      					index++;
			      					}
			      				}
			      			}
			      		}
			      	}else if(input.contains("Send any character")) {
			      		ready = true;
			      		choosenPort.getOutputStream().write("GO".getBytes());
			      	}
				}
			}catch(Exception e){ 
				e.printStackTrace(); 
			}
			
		}else {
			System.out.println("Unable to open port!");
			return;
		}
	}

	
}
