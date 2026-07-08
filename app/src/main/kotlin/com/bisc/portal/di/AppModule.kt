package com.bisc.portal.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.bisc.portal.data.db.PortalDatabase
import com.bisc.portal.data.db.SectionDao
import com.bisc.portal.data.db.TileClickDao
import com.bisc.portal.data.db.TileDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "portal_settings")

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext ctx: Context): PortalDatabase =
        Room.databaseBuilder(ctx, PortalDatabase::class.java, "portal.db")
            .addMigrations(PortalDatabase.MIGRATION_3_4, PortalDatabase.MIGRATION_4_5, PortalDatabase.MIGRATION_5_6, PortalDatabase.MIGRATION_6_7, PortalDatabase.MIGRATION_7_8, PortalDatabase.MIGRATION_8_9, PortalDatabase.MIGRATION_9_10, PortalDatabase.MIGRATION_10_11, PortalDatabase.MIGRATION_11_12, PortalDatabase.MIGRATION_12_13, PortalDatabase.MIGRATION_13_14, PortalDatabase.MIGRATION_14_15)
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()

    @Provides fun provideSectionDao(db: PortalDatabase): SectionDao = db.sectionDao()

    @Provides fun provideTileDao(db: PortalDatabase): TileDao = db.tileDao()

    @Provides fun provideTileClickDao(db: PortalDatabase): TileClickDao = db.tileClickDao()

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext ctx: Context): DataStore<Preferences> = ctx.dataStore
}
