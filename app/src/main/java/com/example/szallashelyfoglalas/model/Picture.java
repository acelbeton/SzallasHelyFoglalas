
package com.example.szallashelyfoglalas.model;

public class Picture {
    private String id;
    private String extension;
    private String uploader;

    public Picture() {
    }

    public Picture(String id, String extension, String uploader) {
        this.id = id;
        this.extension = extension;
        this.uploader = uploader;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getUploader() {
        return uploader;
    }

    public void setUploader(String uploader) {
        this.uploader = uploader;
    }
}
