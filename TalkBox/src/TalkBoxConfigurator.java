import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class TalkBoxConfigurator extends JFrame implements TalkBoxConfiguration {
	
	private JButton buttonRecord = new JButton("Record");
	private JButton addSet = new JButton("Add new set");
	public static SoundRecordingUtil recorder = new SoundRecordingUtil();
	private boolean isRecording = false;

	// Icons used for buttons
	private ImageIcon iconRecord = new ImageIcon(getClass().getResource("Record.gif"));
	private ImageIcon iconStop = new ImageIcon(getClass().getResource("Stop.gif"));
	static ArrayList<JButton> sound_buttons = new ArrayList<JButton>();
	static ArrayList<JButton> img_buttons = new ArrayList<JButton>();
	private JComboBox<String> setBox;
	static int setCount = 1;
	static String[] setList = {"Set_1"};
	static String currentSet = "Set_1";
	ConfiguratorController cc = new ConfiguratorController();
	static StringBuilder builder = new StringBuilder();
	public static int counter = 0;
	static int RecordCounter = 0;
	public Clip clip;
	int height = 300;
	int width = 600;


	public TalkBoxConfigurator() throws URISyntaxException, IOException {
		
		super("Configuration Window");
		addWindowListener(new WindowAdapter() {
			
            @Override
            public void windowClosing(WindowEvent e) {
            	
        		int x = JOptionPane.showConfirmDialog(null, "Would you like to save and exit? [You will lose your current configuration and previous configurations (if any) if you don't save]",
        				"Exit", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
        		
        		if(x == JOptionPane.NO_OPTION) {
        			
        			try {
						deleteDirectoryRecursion(new File(ConfiguratorController.jDirectory));
					} catch (IOException e1) {

						e1.printStackTrace();
					}
            		
                	System.exit(0);
        			
        		}
        		
        		else {
        			
        			System.exit(0);
        			
        		}
                
            }
            
        });
		JButton log = new JButton("Simulator Log");
		log.addActionListener(new LogListener());
		cc.createDirectories();
		addSet.addActionListener(new addSetListener());
		JPanel a = new JPanel();
	
		a.setLayout(new BoxLayout(a, 1));
		setSize(width, height);
		setLayout(new GridLayout(1, 1));
		JButton button = new JButton("Add Button");
		a.add(button);
		button.addActionListener(new addListener());
		JButton reset = new JButton("Reset");
		a.add(addSet);
		reset.addActionListener(new resetListener());
		a.add(buttonRecord);
		a.add(reset);
		
		buttonRecord.setIcon(iconRecord);
		setBox = new JComboBox<String>(setList);
		
		setBox.addItemListener(
				
				new ItemListener() {
					
					public void itemStateChanged(ItemEvent event) {
						
						if(event.getStateChange() == ItemEvent.SELECTED) {
							
							JOptionPane.showMessageDialog(null, "Set saved. " + currentSet + " can no longer be edited.");
		        			currentSet = event.getItem().toString();
							counter = 0;
							revalidate();
							
						}
						
					}
					
				});
		
		setBox.setMaximumSize(setBox.getPreferredSize());
		float alignment = Component.LEFT_ALIGNMENT;
		a.add(setBox);
	    BoxLayout layout = new BoxLayout(a, BoxLayout.Y_AXIS);
	    a.setLayout(layout);
	    button.setAlignmentX(alignment);
	    reset.setAlignmentX(alignment);
	    addSet.setAlignmentX(alignment);
	    setBox.setAlignmentX(alignment);
	    buttonRecord.setAlignmentX(alignment);
		add(a);
		a.add(log);
		setVisible(true);
		

		// add(buttonRecord);
		buttonRecord.addActionListener(new recordListener());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
	}
	public class LogListener implements ActionListener {
		public void actionPerformed(ActionEvent cc) {
			System.out.println(java.util.Calendar.getInstance().getTime()+":");
			System.out.println("Log open");
			final JTextArea edit = new JTextArea(30, 60);
	        edit.setText("Press button below to see Simulator Log");
	     //   edit.append("\nfour\nfive");

	        JButton read = new JButton("Read Simulator Log");
	        read.addActionListener( new ActionListener()
	        {
	            public void actionPerformed(ActionEvent e)
	            {
	                try
	                {
	                    FileReader reader = new FileReader( "simulator.txt" );
	                    BufferedReader br = new BufferedReader(reader);
	                    edit.read( br, null );
	                    br.close();
	                    edit.requestFocus();
	                }
	                catch(Exception e2) { System.out.println(e2); }
	            }
	        });

	        JFrame frame = new JFrame("Configurator Log");
	        frame.getContentPane().add( new JScrollPane(edit), BorderLayout.NORTH );
	        frame.getContentPane().add(read, BorderLayout.WEST);
	    //    frame.getContentPane().add(write, BorderLayout.EAST);
	      //  frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        frame.pack();
	        frame.setLocationRelativeTo( null );
	        frame.setVisible(true);
	    }
		}
	
	public class addSetListener implements ActionListener {
		
		public void actionPerformed(ActionEvent ev) {
			System.out.println(java.util.Calendar.getInstance().getTime()+":");
			System.out.println("New Set added");
			setCount++;
			setBox.addItem("Set_" + setCount);
			setBox.getItemAt(setCount-1);
			sound_buttons.clear();
			img_buttons.clear();
			revalidate();
			
			
		}
		
	}
	
	public class resetListener implements ActionListener {
		
		public void actionPerformed(ActionEvent event) {
			System.out.println(java.util.Calendar.getInstance().getTime()+":");
			System.out.println("Reset button pressed");
			cc.resetDirectories();
			sound_buttons.clear();
			img_buttons.clear();
			builder = new StringBuilder();
			cc.filenames.clear();
			cc.images.clear();
			cc.builders.clear();
			ConfiguratorController.saveFilePaths.clear();
			ConfiguratorController.saveFilePath = "";
			counter = 0;
			setCount = 1;
			dispose();
			
		}
		
	}

	public class recordListener implements ActionListener {
		
		public void actionPerformed(ActionEvent event) {
			
			JButton button = (JButton) event.getSource();
			
			if (button == buttonRecord) {
				
				if (!isRecording) {
					System.out.println(java.util.Calendar.getInstance().getTime()+":");
					System.out.println("Recording");
					startRecording();
					setSize(width += 80, height);
					img_buttons.add(new JButton("Drag image file... "));
					add(img_buttons.get(counter));
					sound_buttons.add(new JButton("Press to play "));
					sound_buttons.get(counter).addActionListener(new soundListener());
					getContentPane().add(new javax.swing.JScrollPane(sound_buttons.get(counter)), java.awt.BorderLayout.CENTER);

					new FileDrop(System.out, img_buttons.get(counter), /* dragBorder, */ new FileDrop.Listener() {

						public void filesDropped(java.io.File[] files) {

							for (int i = 0; i < files.length; i++) {

								try {
									
									cc.imagesFileDrop(files, i);
									ImageIcon img = new ImageIcon("" + cc.images.get(counter - 1));
									img_buttons.get(counter - 1).setIcon(img);
									img_buttons.get(counter - 1).setText(files[i].getName());
									img_buttons.get(counter - 1).setHorizontalTextPosition(JLabel.CENTER);
									img_buttons.get(counter - 1).setVerticalTextPosition(JLabel.BOTTOM);

								} // end try

								catch (java.io.IOException e) {

								}
								
							}
							
							// end for: through each dropped file
						} // end filesDropped

					});

				} else {
					System.out.println(java.util.Calendar.getInstance().getTime()+":");
					System.out.println("Stop Recording");
					stopRecording();

				}
				
			}

		}

		private void startRecording() {
			
			Thread recordThread = new Thread(new Runnable() {
				
				@Override
				public void run() {
					
					try {
						
						isRecording = true;
						buttonRecord.setText("Stop");
						buttonRecord.setIcon(iconStop);

						recorder.start();

					} catch (LineUnavailableException ex) {
						

						ex.printStackTrace();
						
					}
					
				}
				
			});
			
			recordThread.start();

		}

		/**
		 * Stop recording and save the sound into a WAV file
		 */
		private void stopRecording() {
			
			isRecording = false;
			
			try {
				
				buttonRecord.setText("Record");
				buttonRecord.setIcon(iconRecord);
				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				recorder.stop();
				setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				saveFile();

			} catch (IOException ex) {

				ex.printStackTrace();
				
			}
			
		}

		/**
		 * Save the recorded sound into a WAV file.
		 */
		private void saveFile() {
			System.out.println(java.util.Calendar.getInstance().getTime()+":");
			System.out.println("Record file saved");
			cc.save();
			RecordCounter++;
			counter++;
			
		}
		
	}

	public class soundListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			
			for (int k = 0; k < counter; k++) {
				
				for (int j = 0; j < RecordCounter; j++) {
					
					if (e.getSource().equals(sound_buttons.get(k)) && cc.filenames.get(k).equals(ConfiguratorController.saveFilePaths.get(j))) {

						try {

							File soundFile = new File(ConfiguratorController.saveFilePaths.get(j)); // you could also get the sound file with
									System.out.print(cc.filenames.get(k)+ "Playing");											// an URL
							
					/* Here Here Here Here */if (clip == null) {
								AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
								System.out.println(java.util.Calendar.getInstance().getTime()+":");
								System.out.println(ConfiguratorController.saveFilePaths.get(j));
								// Get a sound clip resource.
								clip = AudioSystem.getClip();
								// Open audio clip and load samples from the audio input stream.
								clip.open(audioIn);
								clip.start();
								getAudioFileNames();
								
								// getRelativePathToAudioFiles();
							}
							/* here */else if (isPlaying()) {
								System.out.println(java.util.Calendar.getInstance().getTime()+":");
								System.out.println(cc.filenames.get(k)+"	STOP playing");
				//if the sound is playing, stop it then open a new one to play;				
								clip.stop();
								AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
								//	System.out.println(ConfiguratorController.saveFilePaths.get(j));
									// Get a sound clip resource.
									clip = AudioSystem.getClip();
									clip.open(audioIn);
								clip.start();
							/* here */} else {
								AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
								System.out.println(java.util.Calendar.getInstance().getTime()+":");
								System.out.println(ConfiguratorController.saveFilePaths.get(j));
								// Get a sound clip resource.
								clip = AudioSystem.getClip();
								clip.open(audioIn);
								clip.start();
							}
						

						} catch (UnsupportedAudioFileException e1) {

							e1.printStackTrace();

						} catch (IOException e1) {

							e1.printStackTrace();

						} catch (LineUnavailableException e1) {

							e1.printStackTrace();

						}

					}

				}
			}

		}

	}
	public class addListener implements ActionListener {

		public void actionPerformed(ActionEvent event) {
			
			// int NewCounter = counter+RecordCounter;
			System.out.println(java.util.Calendar.getInstance().getTime()+":");
			System.out.println("New Button added");
			setSize(width += 80, height);
			builder = new StringBuilder();
			img_buttons.add(new JButton("Drag image file... "));
			add(img_buttons.get(counter));
			img_buttons.get(counter).addActionListener(new PlayListener1());
			sound_buttons.add(new JButton("Drag sound file... "));
			getContentPane().add(new javax.swing.JScrollPane(sound_buttons.get(counter)), java.awt.BorderLayout.CENTER);

			new FileDrop(System.out, sound_buttons.get(counter), /* dragBorder, */ new FileDrop.Listener() {

				public void filesDropped(java.io.File[] files) {

					for (int i = 0; i < files.length; i++) {

						try {
							if(cc.wrongSoundFormat(files, i)) {
								
								break;
								
							}
							
							cc.audioFileDrop(files, i);
							File source = new File(files[i].getCanonicalPath());
							System.out.println(java.util.Calendar.getInstance().getTime()+":");
							System.out.println(files[i].getCanonicalPath()+"	Sound added");
							sound_buttons.get(counter - 1).setText(files[i].getName());
							sound_buttons.get(counter - 1).setIcon(new ImageIcon("../icon/sound.png"));
							sound_buttons.get(counter - 1).setHorizontalTextPosition(JLabel.CENTER);
							sound_buttons.get(counter - 1).setVerticalTextPosition(JLabel.BOTTOM);
							sound_buttons.get(counter - 1).addActionListener(new ActionListener() {
								
								@Override
								public void actionPerformed(ActionEvent e) {
									
									try {
										System.out.println(java.util.Calendar.getInstance().getTime()+":");
										System.out.println(source.toString() + "	Playing");
										/* Here Here Here Here */if (clip == null) {
											AudioInputStream audioIn = AudioSystem.getAudioInputStream(source);
											//System.out.println(ConfiguratorController.saveFilePaths.get(j));
											// Get a sound clip resource.
											clip = AudioSystem.getClip();
											// Open audio clip and load samples from the audio input stream.
											clip.open(audioIn);
											clip.start();
											getAudioFileNames();
											
											// getRelativePathToAudioFiles();
										}
										/* here */else if (isPlaying()) {
											System.out.println(java.util.Calendar.getInstance().getTime()+":");
											System.out.println(source.toString() + " 	stop playing");
										//	System.out.println("STOP");
							//if the sound is playing, stop it then open a new one to play;				
											clip.stop();
											AudioInputStream audioIn = AudioSystem.getAudioInputStream(source);
											//	System.out.println(ConfiguratorController.saveFilePaths.get(j));
												// Get a sound clip resource.
												clip = AudioSystem.getClip();
												clip.open(audioIn);
											clip.start();
										/* here */} else {
											AudioInputStream audioIn = AudioSystem.getAudioInputStream(source);
										//	System.out.println(ConfiguratorController.saveFilePaths.get(j));
											// Get a sound clip resource.
											clip = AudioSystem.getClip();
											clip.open(audioIn);
											clip.start();
										}
									} catch (Exception e1) {
										
										e1.printStackTrace();
										
									}
									
								}
								
							});
							
						} // end try

						catch (java.io.IOException e) {

						}

					}
					// end for: through each dropped file
				} // end filesDropped

			}); // end FileDrop.Listener

			new FileDrop(System.out, img_buttons.get(counter), /* dragBorder, */ new FileDrop.Listener() {

				public void filesDropped(java.io.File[] files) {

					for (int i = 0; i < files.length; i++) {

						try {
							
							if(cc.wrongImageFormat(files, i)) {
								
								break;
								
							}
							
							cc.imageFileDrop(files, i);
							ImageIcon img = new ImageIcon("" + cc.images.get(counter - 1));
							System.out.println(java.util.Calendar.getInstance().getTime()+":");
							System.out.println(cc.images.get(counter-1)+"	Icon added");
							img_buttons.get(counter - 1).setIcon(img);
							img_buttons.get(counter - 1).setText(files[i].getName());
							img_buttons.get(counter - 1).setHorizontalTextPosition(JLabel.CENTER);
							img_buttons.get(counter - 1).setVerticalTextPosition(JLabel.BOTTOM);

						} // end try

						catch (java.io.IOException e) {

						}
					}
					// end for: through each dropped file
				} // end filesDropped

			});

			counter++;
			revalidate();

		}
		
	}

	public class PlayListener1 implements ActionListener {

		public void actionPerformed(ActionEvent e) {

			for (int k = 0; k < counter; k++) {

				if (e.getSource().equals(sound_buttons.get(k))
						&& cc.builders.get(k).toString().equals(sound_buttons.get(k).getText())) {

					try {
						System.out.println(java.util.Calendar.getInstance().getTime()+":");
						System.out.println(cc.builders.get(k).toString()+"plaing");
						File soundFile = new File(cc.filenames.get(k)); // you could also get the sound file with an URL
						/* Here Here Here Here */if (clip == null) {
							AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
							//System.out.println(ConfiguratorController.saveFilePaths.get(j));
							// Get a sound clip resource.
							clip = AudioSystem.getClip();
							// Open audio clip and load samples from the audio input stream.
							clip.open(audioIn);
							clip.start();
							getAudioFileNames();
							
							// getRelativePathToAudioFiles();
						}
						/* here */else if (isPlaying()) {
							System.out.println(java.util.Calendar.getInstance().getTime()+":");
							System.out.println(cc.builders.get(k).toString()+"STOP playing");
			//if the sound is playing, stop it then open a new one to play;				
							clip.stop();
							AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
							//	System.out.println(ConfiguratorController.saveFilePaths.get(j));
								// Get a sound clip resource.
								clip = AudioSystem.getClip();
								clip.open(audioIn);
							clip.start();
						/* here */} else {
							AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
						//	System.out.println(ConfiguratorController.saveFilePaths.get(j));
							// Get a sound clip resource.
							clip = AudioSystem.getClip();
							clip.open(audioIn);
							clip.start();
						}

						getAudioFileNames();
						getRelativePathToAudioFiles();

					} catch (UnsupportedAudioFileException e1) {

						e1.printStackTrace();

					} catch (IOException e1) {

						e1.printStackTrace();

					} catch (LineUnavailableException e1) {

						e1.printStackTrace();

					}

				}

			}

		}

	}

	@Override
	public int getNumberOfAudioButtons() {

		return counter;
		
	}

	@Override
	public int getNumberOfAudioSets() {

		return counter;
	}

	@Override
	public int getTotalNumberOfButtons() {

		return counter + 1;
	}

	@Override
	public Path getRelativePathToAudioFiles() {

		return cc.RRfilenames;
	}

	static void deleteDirectoryRecursion(File file) throws IOException {
		
		  if (file.isDirectory()) {
			  System.out.println(java.util.Calendar.getInstance().getTime()+":");
			  System.out.println("Delete files");
		    File[] entries = file.listFiles();
		    
		    if (entries != null) {
		    	
		      for (File entry : entries) {
		    	  
		        deleteDirectoryRecursion(entry);
		        
		      }
		      
		    }
		    
		  }
		  
		  if (!file.delete()) {
			  
		    throw new IOException("Failed to delete " + file);
		    
		  }
		  
		}
	
	public boolean isPlaying() {
		
		return clip.isActive();
		
	}


	@Override
	public String[][] getAudioFileNames() {
		
		return null;
		
	}
	public static void main(String args[]) throws IOException, URISyntaxException {

		TalkBoxConfigurator talkBoxConf = new TalkBoxConfigurator();
		PrintStream out = new PrintStream( new FileOutputStream("out.txt"));
		System.setOut(out);
        // Show the frame
		talkBoxConf.setVisible(true);

	}
}
