/* $Id$ */
package com.muthuraj.example

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Created by Muthuraj on 2019-04-22.
 */
@Database(entities = [Dog::class], version = 1)
abstract class Database : RoomDatabase() {
    abstract fun getDogDao(): DogDao
}