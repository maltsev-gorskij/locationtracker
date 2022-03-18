package ru.lyrian.location_tracker.model.local_database.entities

import androidx.room.*
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface UserEntityDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun add(userEntity: UserEntity): Completable

    @Update
    fun update(userEntity: UserEntity): Completable

    @Query("SELECT * FROM ${UserEntity.TABLE_NAME} WHERE email LIKE :email")
    fun findByEmail(email: String): Single<UserEntity>

    @Query("DELETE FROM ${UserEntity.TABLE_NAME}")
    fun deleteAll(): Completable
}