package org.sopt.santamanitto.main.list

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import org.sopt.santamanitto.room.network.RoomRequest
import org.sopt.santamanitto.user.data.source.UserMetadataSource

@InstallIn(FragmentComponent::class)
@Module
class AdapterModule {
    @Provides
    fun provideJoinedRoomAdapter(
        userMetadataSource: UserMetadataSource,
        roomRequest: RoomRequest
    ): MyManittoListAdapter = MyManittoListAdapter(userMetadataSource, roomRequest)
}