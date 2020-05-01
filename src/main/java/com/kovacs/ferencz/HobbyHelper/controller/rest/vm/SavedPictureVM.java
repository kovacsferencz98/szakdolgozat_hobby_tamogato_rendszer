package com.kovacs.ferencz.HobbyHelper.controller.rest.vm;

import com.kovacs.ferencz.HobbyHelper.service.dto.PictureDTO;

public class SavedPictureVM {
    private Long id;

    private String fileName;

    private String fileType;

    public static SavedPictureVM fromPictureDTO(PictureDTO pictureDTO) {
        SavedPictureVM savedPictureVM = new SavedPictureVM();
        savedPictureVM.setFileName(pictureDTO.getFileName());
        savedPictureVM.setFileType(pictureDTO.getFileType());
        savedPictureVM.setId(pictureDTO.getId());
        return savedPictureVM;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SavedPictureVM that = (SavedPictureVM) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (fileName != null ? !fileName.equals(that.fileName) : that.fileName != null) return false;
        return fileType != null ? fileType.equals(that.fileType) : that.fileType == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (fileName != null ? fileName.hashCode() : 0);
        result = 31 * result + (fileType != null ? fileType.hashCode() : 0);
        return result;
    }
}
