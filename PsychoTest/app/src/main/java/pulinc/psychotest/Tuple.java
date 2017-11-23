package pulinc.psychotest;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by makoto on 11/16/17.
 */

public class Tuple implements Parcelable {

    boolean standard;
    int value, index;
    public Tuple(boolean standard, int value, int index) {
        this.standard = standard;
        this.value = value;
        this.index = index;
    }

    protected Tuple(Parcel in) {
        standard = in.readByte() != 0;
        value = in.readInt();
        index = in.readInt();
    }

    public static final Creator<Tuple> CREATOR = new Creator<Tuple>() {
        @Override
        public Tuple createFromParcel(Parcel in) {
            return new Tuple(in);
        }

        @Override
        public Tuple[] newArray(int size) {
            return new Tuple[size];
        }
    };

    public boolean getStandard(){
        return standard;
    }

    public int getValue(){
        return value;
    }

    public int getIndex(){ return index; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeByte((byte) (standard ? 1 : 0));
        parcel.writeInt(value);
        parcel.writeInt(index);
    }
}
