package  com.reemsib.carscashpoint.model

import android.os.Parcel
import android.os.Parcelable

data class User(
   // var id: String,
    //var img: Int,
    var company: String,
    var model: String,
    var mobile: String) : Parcelable {

    constructor(parcel: Parcel) : this(
     //   parcel.readString()!!,
     //   parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!


    )override fun writeToParcel(parcel: Parcel, flags: Int) {
     //  parcel.writeString(id)
      //  parcel.writeInt(img)
        parcel.writeString(company)
        parcel.writeString(model)
        parcel.writeString(mobile)

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }


}
