/* $Id$ */
package com.muthuraj.example

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room

/**
 * Created by Muthuraj on 2019-04-22.
 *
 * Jambav, Zoho Corporation
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = Room.databaseBuilder(applicationContext, Database::class.java, "database.db")
            .build()
    }
}