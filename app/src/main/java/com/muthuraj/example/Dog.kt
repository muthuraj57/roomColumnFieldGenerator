/* $Id$ */
package com.muthuraj.example

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Insert
import androidx.room.PrimaryKey

/**
 * Created by Muthuraj on 2019-04-22.
 */
@Entity(tableName = "DogTable")
data class Dog(

    @PrimaryKey
    @ColumnInfo(name = DogTableFields.NAME)
    val id: String = "",

    @ColumnInfo(name = DogTableFields.ID)
    val name: String = ""
)

@Entity
class Test{

    @ColumnInfo(name = TestFields.FIELD1)
    var field1: String = ""

    @ColumnInfo(name = TestFields.FIELD_TWO)
    var fieldTwo: Int = -1

    @delegate:Ignore
    val test by lazy { "something" }
}

@Dao
interface DogDao {
    @Insert
    fun insert(dog: Dog)
}