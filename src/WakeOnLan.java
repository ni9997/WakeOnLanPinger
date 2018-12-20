
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.net.*;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;

public class WakeOnLan extends JFrame implements Runnable {
    
    public static final int PORT = 9; 
    static String ipStr = "192.168.178.255";
    static String macStr = "4C:E6:76:FD:BE:DE";
    static  boolean running = false;
    static Thread thread;
    
    JButton start;
    JButton stop;
    
    public WakeOnLan()
    {
    	super("Server");
    	this.setSize(400, 200);
    	
    	thread = new Thread(this);
    	
    	this.setLayout(new FlowLayout());
    	
    	start = new JButton("Start");
    	start.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				thread.start();
			}
		});
    	this.add(start);
    	
    	stop = new JButton("Stop");
    	stop.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				thread.interrupt();
			}
		});
    	this.add(stop);
    	
    	this.setVisible(true);
    	
    }

    
    public static void main(String[] args) {
        
        new WakeOnLan();    
        
        
    }
    
    private static void runWakeOnLan()
    {
    	try {
            byte[] macBytes = getMacBytes(macStr);
            byte[] bytes = new byte[6 + 16 * macBytes.length];
            for (int i = 0; i < 6; i++) {
                bytes[i] = (byte) 0xff;
            }
            for (int i = 6; i < bytes.length; i += macBytes.length) {
                System.arraycopy(macBytes, 0, bytes, i, macBytes.length);
            }
            
            InetAddress address = InetAddress.getByName(ipStr);
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, address, PORT);
            DatagramSocket socket = new DatagramSocket();
            socket.send(packet);
            socket.close();
            
            System.out.println("Wake-on-LAN packet sent.");
        }
        catch (Exception e) {
            System.out.println("Failed to send Wake-on-LAN packet: + e");
            System.exit(1);
        }
    }
    
    private static byte[] getMacBytes(String macStr) throws IllegalArgumentException {
        byte[] bytes = new byte[6];
        String[] hex = macStr.split("(\\:|\\-)");
        if (hex.length != 6) {
            throw new IllegalArgumentException("Invalid MAC address.");
        }
        try {
            for (int i = 0; i < 6; i++) {
                bytes[i] = (byte) Integer.parseInt(hex[i], 16);
            }
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid hex digit in MAC address.");
        }
        return bytes;
    }


	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(!thread.isInterrupted())
		{
			runWakeOnLan();
			try {
				thread.sleep(180000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
    
   
}