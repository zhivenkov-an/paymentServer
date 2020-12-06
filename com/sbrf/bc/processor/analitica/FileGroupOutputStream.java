package com.sbrf.bc.processor.analitica;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import com.sbrf.bc.processor.FileMetadata;


public class FileGroupOutputStream extends OutputStream{
    protected OutputStream currentStream;
    protected final String outputDirectory;
    protected final List filesMetadata;
    protected final boolean single;
    protected String groupName;
    protected File newFile;
    protected boolean hasFile;
    
    public FileGroupOutputStream(String outputDirectory, boolean single){
        this.outputDirectory = outputDirectory;
        this.single = single;
        this.filesMetadata = new ArrayList();
        
    }
    public void write(int b) throws IOException {
        currentStream.write(b);
        
    }
    public void start(){
              hasFile = false;
    }
    public void startBlock(String qName)throws IOException{
        if (single){
            if (!hasFile){
                nextFile(qName);
                hasFile = true;
            }
        }else{
            nextFile(qName);
        }
        
    }
    public void endBlock()throws IOException{
        if(!single){
            currentStream.close();
        }
    }
    
    public void stop()throws IOException{
        if (currentStream != null){
            currentStream.close();
        }
    }
    private void nextFile(String qName)throws IOException{
        
        if (currentStream != null){
            currentStream.close();
        }
        File file = File.createTempFile(qName,"");
        currentStream = new FileOutputStream(file);
        FileMetadata newFileMetadata = new FileMetadata(file, outputDirectory, qName);
        filesMetadata.add(newFileMetadata);
    }
    public FileMetadata[] getFilesMetadata() {
        return (FileMetadata[]) filesMetadata.toArray(new FileMetadata[filesMetadata.size()]);
    }
}
