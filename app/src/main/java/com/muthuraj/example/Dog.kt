/* $Id$ */
package com.muthuraj.example

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Insert
import androidx.room.PrimaryKey

/**
 * Created by Muthuraj on 2019-04-22.
 *
 * Jambav, Zoho Corporation
 */
@Entity(tableName = "DogTable")
data class Dog(
    @PrimaryKey
    val id: String = "",
    val name: String = ""
)

@Entity
class Test{
    var field1: String = ""
    var fieldTwo: Int = -1

    @delegate:Ignore
    val test by lazy { "something" }
}

@Dao
interface DogDao {
    @Insert
    fun insert(dog: Dog)
}