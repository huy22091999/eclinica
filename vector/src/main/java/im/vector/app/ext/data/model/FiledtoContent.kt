package im.vector.app.ext.data.model

import com.google.gson.annotations.SerializedName
import im.vector.app.ext.data.model.FileDescriptor

data class FiledtoContent(

    @SerializedName("id"              ) var id              : String?  = null,
    @SerializedName("type"            ) var type            : Int?     = null,
    @SerializedName("patient"         ) var patient         : String?  = null,
    @SerializedName("file"            ) var file            : FileDescriptor?    = FileDescriptor(),
    @SerializedName("description"     ) var description     : String?  = null,
    @SerializedName("attackImageType" ) var attackImageType : Int?     = null
)
