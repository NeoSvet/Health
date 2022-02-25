package ru.neosvet.health.data

class FakeRepository : Repository {
    override suspend fun getList(): List<HealthEntity> = listOf(
        HealthEntity("dsf", 1645777389833L, 137, 71, 59),
        HealthEntity("ddh", 1645776009833L, 126, 67, 49),
        HealthEntity("daw", 1645701389833L, 141, 64, 63),
        HealthEntity("xgn", 1645700009833L, 127, 73, 58),
        HealthEntity("sdf", 1645635389833L, 150, 83, 55),
        HealthEntity("sdf", 1645633009833L, 129, 79, 57)
    )
}