package id.ac.itats.skripsi.shortestpath.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Path implements Parcelable {
	private String pathId;
	private int pathStep;
	private String pathName;
	private String pathLenght;
	private int turnImage;

	public Path(String pathId, int pathStep, String pathName, String pathLenght) {
		super();
		this.pathId = pathId;
		this.pathStep = pathStep;
		this.pathName = pathName;
		this.pathLenght = pathLenght;
	}

	public String getPathId() {
		return pathId;
	}

	public void setPathId(String pathId) {
		this.pathId = pathId;
	}

	public int getPathStep() {
		return pathStep;
	}

	public void setPathStep(int pathStep) {
		this.pathStep = pathStep;
	}

	public String getPathName() {
		return pathName;
	}

	public void setPathName(String pathName) {
		this.pathName = pathName;
	}

	public String getPathLenght() {
		return pathLenght;
	}

	public void setPathLenght(String pathLenght) {
		this.pathLenght = pathLenght;
	}

	public int getTurnImage() {
		return turnImage;
	}

	public void setTurnImage(int turnImage) {
		this.turnImage = turnImage;
	}

	@Override
	public String toString() {
		return "Path [pathId=" + pathId + ", pathName=" + pathName + ", pathLenght=" + pathLenght + "]";
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(pathId);
		dest.writeInt(pathStep);
		dest.writeString(pathName);
		dest.writeString(pathLenght);
		dest.writeInt(turnImage);

	}

	public static final Parcelable.Creator<Path> CREATOR = new Parcelable.Creator<Path>() {

		@Override
		public Path createFromParcel(Parcel in) {
			return new Path(in);
		}

		@Override
		public Path[] newArray(int size) {
			return new Path[size];
		}

	};

	private Path(Parcel in) {
		pathId = in.readString();
		pathStep = in.readInt();
		pathName = in.readString();
		pathLenght = in.readString();
		turnImage = in.readInt();
	}
}
