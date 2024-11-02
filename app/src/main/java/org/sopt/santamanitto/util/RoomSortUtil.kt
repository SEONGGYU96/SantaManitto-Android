package org.sopt.santamanitto.util

import org.sopt.santamanitto.main.list.RoomState
import org.sopt.santamanitto.room.data.TempMyManittoModel
import org.sopt.santamanitto.room.data.getRoomState

object RoomSortUtil {
    fun getSortedRooms(rooms: List<TempMyManittoModel>): List<TempMyManittoModel> {
        return rooms.sortedWith(compareBy<TempMyManittoModel> { room ->
            // 1차 정렬: 방 상태 우선순위
            when (room.getRoomState()) {
                RoomState.IN_PROGRESS -> 0
                RoomState.WAITING -> 1
                RoomState.FINISHED -> 2
                RoomState.EXPIRED -> 3
                RoomState.DELETED -> 4
                RoomState.LEFT -> 5
            }
        }.thenByDescending { room ->
            // 2차 정렬: 상태별 세부 정렬
            when (room.getRoomState()) {
                RoomState.IN_PROGRESS -> {
                    // 결과 공개 예정일 기준 오름차순
                    room.expirationDate?.let {
                        TimeUtil.convertUtcToCalendar(it).timeInMillis
                    } ?: 0
                }

                RoomState.WAITING -> {
                    // 방 생성 일자 내림차순
                    -(TimeUtil.convertUtcToCalendar(room.createdAt).timeInMillis)
                }

                RoomState.FINISHED -> {
                    // 방 종료 일자 내림차순
                    room.expirationDate?.let {
                        -(TimeUtil.convertUtcToCalendar(it).timeInMillis)
                    } ?: 0
                }

                RoomState.DELETED -> {
                    // 방 생성 일자 내림차순
                    -(TimeUtil.convertUtcToCalendar(room.createdAt).timeInMillis)
                }

                else -> 0
            }
        })
    }
}
