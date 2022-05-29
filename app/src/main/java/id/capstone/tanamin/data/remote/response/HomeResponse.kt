package id.capstone.tanamin.data.remote.response

import com.google.gson.annotations.SerializedName

data class HomeResponse(

	@field:SerializedName("data")
	val data: DataHome?=null,

	@field:SerializedName("status")
	val status: String
)

data class DataHome(

	@field:SerializedName("recent_modul")
	val recentModul: Int,

	@field:SerializedName("modul_title")
	val modul_title: String,

	@field:SerializedName("kelas")
	val kelas: List<ClassesHome>,

	@field:SerializedName("progress")
	val progress: Float
)

data class ClassesHome(

	@field:SerializedName("id_class")
	val idClass: Int,

	@field:SerializedName("detail")
	val detail: String,

	@field:SerializedName("title")
	val title: String,

	@field:SerializedName("picture")
	val picture: String,

	@field:SerializedName("total_module")
	val totalModule: Int
)
