/* $Id$ */
package com.muthuraj.example

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query

/**
 * Created by Muthuraj on 2019-05-05.
 */
@Entity(tableName = "User")
data class UserEntity(
    @PrimaryKey
    val id: String = "",
    val name: String = ""
)

@Entity(
    tableName = "UserDetails",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["user"],
            onDelete = ForeignKey.CASCADE
        )]
)
data class UserDetailsEntity(
    @PrimaryKey
    val user: String = "",
    val email: String = ""
)


data class UserSubset(
    @ColumnInfo(name = UserFields.NAME)
    val name: String,

    @ColumnInfo(name = UserDetailsFields.EMAIL)
    val email: String
)

@Dao
interface UserDao {
    @Insert
    fun insert(user: UserEntity)

    @Query(
        """
        SELECT User.name, UserDetails.email from User join UserDetails
            where User.id = UserDetails.user and User.id = :id
        LIMIT 1
    """
    )
    fun getUserSubset(id: String): UserSubset
}

@Dao
interface UserDetailsDao {
    @Insert
    fun insert(userDetails: UserDetailsEntity)
}