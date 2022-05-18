package id.capstone.tanamin.data.local.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Classes(
    @PrimaryKey
    val id_class: Int,
    val title:String,
    val detail:String,
    val picture:String,
    val total_module:Int
)

@Entity
data class Moduls(
    @PrimaryKey
    val id_moduls:Int,
    val classes_id:Int,
    val title:String,
    val content:String,
    val picture:String,
    val quiz_id:Int
)

@Entity
data class Informations(
    @PrimaryKey
    val id_informations:Int,
    val name:String,
    val content:String,
    val benefit:String,
    val classes_id:Int
)