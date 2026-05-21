package com.example.githubassignment.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [GithubRepoEntity::class], version = 4, exportSchema = false)
@TypeConverters(Converters::class)
abstract class GithubDatabase : RoomDatabase() {
    abstract fun githubDao(): GithubDao
}
