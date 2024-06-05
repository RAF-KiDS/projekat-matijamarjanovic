package servent.model;

import app.AppConfig;
import app.ChordState;

import java.io.*;

import static app.ChordState.CHORD_SIZE;

public class ChordFile {

    private int creatorId;
    private File file;
    private String privacy;
    private String path;
    private String fileName;
    private int chordId;

    public ChordFile(String privacy, String path, int creatorId) {
        this.privacy = privacy;
        this.path = path;
        try {
            this.file = new File(AppConfig.root + "/" +path);
            uploadAndSaveFile(this.file, AppConfig.localDBpath+ "/" + file.getName());
        }catch (Exception e){
            this.file = null;
        }
        this.creatorId = creatorId;
        this.fileName = file.getName();
        this.chordId = generateChordId(fileName);
    }

    public static int generateChordId(String fileName){
        int asciiCodeSum = 0;
        for (int i = 0; i < fileName.length(); i++) {
            asciiCodeSum += fileName.charAt(i);
        }

        return ChordState.chordHash(asciiCodeSum);
    }

    public int getChordId() {
        return chordId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getPrivacy() {
        return privacy;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return path + "," + privacy + "," + creatorId;
    }


    public static void uploadAndSaveFile(File sourceFile, String targetPath) {
        File targetFile = new File(targetPath);
        try (FileInputStream fis = new FileInputStream(sourceFile);
             FileOutputStream fos = new FileOutputStream(targetFile)) {

            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }

            System.out.println("File uploaded and saved successfully at " + targetPath);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
