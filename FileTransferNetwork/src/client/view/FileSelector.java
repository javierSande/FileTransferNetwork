package client.view;

import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

@SuppressWarnings("serial")
public class FileSelector extends JFrame {
    public FileSelector() {
    	this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setPreferredSize(new Dimension(0,0));
        this.setLocation(0, 0);
        this.setBackground(Color.WHITE);
        this.setLayout(null);


        this.setResizable(false);
        this.setSize(10, 10);

        this.pack();
        this.setVisible(false);
    }
    
    public Map<String, String> selectFiles() {
    	Map<String, String> files = new HashMap<String, String>();
    	
    	this.setVisible(true);
    	JFileChooser jfc = new JFileChooser();
	    jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
	    jfc.setMultiSelectionEnabled(true);
	    if( jfc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION ){
	        for(File f: jfc.getSelectedFiles())
	        	files.put(f.getName(), f.getAbsolutePath());
	    }
	    this.setVisible(false);
	    return files;
    }
}