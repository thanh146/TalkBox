import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

public class ConfiguratorController {
	
	public static String jDirectory;
	public List<String> filenames = new ArrayList<String>();
	public List<String> images = new ArrayList<String>();
	public Path RRfilenames;
	public String filename = "";
	public ArrayList<StringBuilder> builders = new ArrayList<StringBuilder>();
	public static ArrayList<String> saveFilePaths = new ArrayList<String>();
	public static String saveFilePath;
	
    public void createDirectories() throws IOException, URISyntaxException{

        CodeSource cs = TalkBoxConfigurator.class.getProtectionDomain().getCodeSource();
        File jF = new File(cs.getLocation().toURI().getPath());
        jDirectory = jF.getParentFile().getPath() + "/config";
        Files.createDirectories(Paths.get(jDirectory + "/audio"));
        Files.createDirectories(Paths.get(jDirectory + "/images"));
        Files.createDirectories(Paths.get(jDirectory + "/serialize"));

    }
    
    public void resetDirectories() {
    	
		try {
			
			TalkBoxConfigurator reset = new TalkBoxConfigurator();
			File AudioDirectory = new File(jDirectory + "/audio");
			
			for(File f: AudioDirectory.listFiles()) 
				
				  f.delete();
			
			File IconDirectory = new File(jDirectory + "/images");
			
			for(File f: IconDirectory.listFiles()) 
				
				  f.delete();
			
		} catch (URISyntaxException e) {
			
			e.printStackTrace();
			
		} catch (IOException e) {

			e.printStackTrace();
			
		}
    	
    }
    
	public static String getFileExtension(File file) {

		String fileName = file.getName();

		if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {

			return fileName.substring(fileName.lastIndexOf(".") + 1);
			
		}

		else
			
			return "";

	}
	
	public void save() {
		
		saveFilePath = jDirectory + "/audio/" + "aud" + (TalkBoxConfigurator.counter+1) + TalkBoxConfigurator.currentSet + ".wav";
		File wavFile = new File(saveFilePath);
		ConfiguratorController.saveFilePaths.add(saveFilePath);
		filenames.add(saveFilePath);

		try {
			
			TalkBoxConfigurator.recorder.save(wavFile);

		} catch (IOException ex) {

			ex.printStackTrace();
			
		}
		
	}
	
	public void imagesFileDrop(java.io.File[] files, int i) throws IOException {
		
		images.add(TalkBoxConfigurator.counter - 1, files[i].getCanonicalPath());
		File source = new File(files[i].getCanonicalPath());
		String extension = ConfiguratorController.getFileExtension(source);
		File dest = new File(jDirectory + "/images/img" + TalkBoxConfigurator.counter + TalkBoxConfigurator.currentSet + "." + extension);
		Files.copy(source.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
		
	}
	
	public void imageFileDrop(java.io.File[] files, int i) throws IOException {
		
		images.add(TalkBoxConfigurator.counter - 1, files[i].getCanonicalPath());
		File source = new File(files[i].getCanonicalPath());
		File dest = new File(jDirectory + "/images/img" + TalkBoxConfigurator.counter + TalkBoxConfigurator.currentSet + "." + ConfiguratorController.getFileExtension(source));
		Files.copy(source.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
		
	}
	
	public void audioFileDrop(java.io.File[] files, int i) throws IOException {
		
		TalkBoxConfigurator.builder.append(files[i].getCanonicalPath() + "\n");
		filename = "" + files[i].getCanonicalPath();
		RRfilenames = files[i].toPath().getParent();
		filenames.add(filename);
		builders.add(TalkBoxConfigurator.builder);
		File source = new File(filename);
		File dest = new File(jDirectory + "/audio/aud" + TalkBoxConfigurator.counter + TalkBoxConfigurator.currentSet + "." + ConfiguratorController.getFileExtension(source));
		Files.copy(source.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
		
	}
	
	public boolean wrongImageFormat(File[] files, int i) {
		
		if(!ConfiguratorController.getFileExtension(files[i]).contains("png")) {
			JOptionPane.showMessageDialog( null, "Incorrect file format, please use .png file", "Error", JOptionPane.ERROR_MESSAGE);
			return true;
		}
		else {
			return false;	
		}	
	}
	
	public boolean wrongSoundFormat(File[] files, int i) {
		
		if(!ConfiguratorController.getFileExtension(files[i]).contains("wav")) {
			
			JOptionPane.showMessageDialog( null, "Incorrect file format, please use .wav file", "Error", JOptionPane.ERROR_MESSAGE);
			return true;
			
		}
		else {
			
			return false;
			
		}
		
	}

}


