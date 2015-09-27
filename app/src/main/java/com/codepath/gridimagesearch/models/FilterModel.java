package com.codepath.gridimagesearch.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Model for Search filters
 */
public class FilterModel implements Parcelable {
    public enum FileType {
        NO_FILTER, JPG, PNG, GIF, BMP;

        public String toString() {
            switch (this) {
                case NO_FILTER: return "No Filter";
                case JPG: return "jpg";
                case PNG: return "png";
                case GIF: return "gif";
                case BMP: return "bmp";
                default: throw new IllegalArgumentException();
            }
        }
    }

    public enum Colorization {
        NO_FILTER, GRAYSCALE, COLOR;

        public String toString() {
            switch (this) {
                case NO_FILTER: return "No Filter";
                case GRAYSCALE: return "gray";
                case COLOR: return "color";
                default: throw new IllegalArgumentException();
            }
        }
    }

    // TODO: Support larger sizes?
    public enum ImageSize {
        NO_FILTER, ICON, SMALL, MEDIUM, LARGE;

        public String toString() {
            switch (this) {
                case NO_FILTER: return "No Filter";
                case ICON: return "icon";
                case SMALL: return "small";
                case MEDIUM: return "medium";
                case LARGE: return "large";
                default: throw new IllegalArgumentException();
            }
        }
    }

    public enum SafetyLevel {
        ACTIVE, OFF;

        public String toString() {
            switch (this) {
                case ACTIVE: return "active";
                case OFF: return "off";
                default: throw new IllegalArgumentException();
            }
        }

    }

    private FileType fileType;
    private String site;
    private Colorization colorization;
    private ImageSize size;
    private SafetyLevel safetyLevel;


    public FilterModel() {
        fileType = FileType.NO_FILTER;
        site = null;
        colorization = Colorization.NO_FILTER;
        size = ImageSize.NO_FILTER;
        safetyLevel = SafetyLevel.OFF;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public FileType getFileType() {
        return fileType;
    }

    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public Colorization getColorization() {
        return colorization;
    }

    public void setColorization(Colorization colorization) {
        this.colorization = colorization;
    }

    public ImageSize getSize() {
        return size;
    }

    public void setSize(ImageSize size) {
        this.size = size;
    }

    public SafetyLevel getSafetyLevel() {
        return safetyLevel;
    }

    public void setSafetyLevel(SafetyLevel safetyLevel) {
        this.safetyLevel = safetyLevel;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.fileType == null ? -1 : this.fileType.ordinal());
        dest.writeString(this.site);
        dest.writeInt(this.colorization == null ? -1 : this.colorization.ordinal());
        dest.writeInt(this.size == null ? -1 : this.size.ordinal());
        dest.writeInt(this.safetyLevel == null ? -1 : this.safetyLevel.ordinal());
    }

    private FilterModel(Parcel in) {
        int tmpFileType = in.readInt();
        this.fileType = tmpFileType == -1 ? null : FileType.values()[tmpFileType];
        this.site = in.readString();
        int tmpColorization = in.readInt();
        this.colorization = tmpColorization == -1 ? null : Colorization.values()[tmpColorization];
        int tmpSize = in.readInt();
        this.size = tmpSize == -1 ? null : ImageSize.values()[tmpSize];
        int tmpSafetyLevel = in.readInt();
        this.safetyLevel = tmpSafetyLevel == -1 ? null : SafetyLevel.values()[tmpSafetyLevel];
    }

    public static final Parcelable.Creator<FilterModel> CREATOR = new Parcelable.Creator<FilterModel>() {
        public FilterModel createFromParcel(Parcel source) {
            return new FilterModel(source);
        }

        public FilterModel[] newArray(int size) {
            return new FilterModel[size];
        }
    };
}
