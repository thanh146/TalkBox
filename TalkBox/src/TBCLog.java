import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.text.*;

public class TBCLog extends JFrame
{
	private JFrame frame;
	private JButton read;
	private final JTextArea edit = new JTextArea(30, 60);
	
	public TBCLog() {
		frame = new JFrame("Configurator Log");
		edit.setText("Press button below to see Configurator Log");
        read = new JButton("Read Configurator Log");
        
        read.addActionListener( new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                try
                {
                    FileReader reader = new FileReader( "out.txt" );
                    BufferedReader br = new BufferedReader(reader);
                    edit.read( br, null );
                    br.close();
                    edit.requestFocus();
                }
                catch(Exception e2) { System.out.println(e2); }
            }
        });

		frame.getContentPane().add( new JScrollPane(edit), BorderLayout.NORTH );
        frame.getContentPane().add(read, BorderLayout.WEST);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo( null );
        frame.setVisible(true);
	}
	
	
	
    public static void main(String a[])
    {
        TBCLog log = new TBCLog();
        log.setVisible(true);

    }
}