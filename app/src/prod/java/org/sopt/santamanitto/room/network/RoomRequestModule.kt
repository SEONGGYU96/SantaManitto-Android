package org.sopt.santamanitto.room.network

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
class RoomRequestModule {

    @Provides
    fun provideCreateRoomRequest(
        roomService: RoomService,
    ): RoomRequest =
        RoomRequestImpl(roomService)
}