package ru.neosvet.health.data

import java.util.*

class FakeRepository : Repository {
    companion object {
        private val list = mutableListOf<HealthEntity>(
            HealthEntity("dsf", 1645777389833L, 137, 71, 59),
            HealthEntity("ddh", 1645776009833L, 126, 67, 49),
            HealthEntity("daw", 1645701389833L, 141, 64, 63),
            HealthEntity("xgn", 1645700009833L, 127, 73, 58),
            HealthEntity("sdf", 1645635389833L, 150, 83, 55),
            HealthEntity("sdf", 1645633009833L, 129, 79, 57)
        )
    }

    override suspend fun getList(): List<HealthEntity> = list.sortedWith(compareByDescending { it.time })

    override suspend fun delete(id: String) {
       for (item in list) {
           if(item.id == id) {
               list.remove(item)
               return
           }
       }
    }

    override suspend fun add(time: Long, highPressure: Int, lowPressure: Int, pulse: Int) {
        list.add(
            HealthEntity(
                id = UUID.randomUUID().toString(),
                time = time,
                highPressure = highPressure,
                lowPressure = lowPressure,
                pulse = pulse
            )
        )
    }
}