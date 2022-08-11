package com.cbi.app.trs.domain.repositories

import com.cbi.app.trs.core.exception.Failure
import com.cbi.app.trs.core.functional.Either
import com.cbi.app.trs.core.platform.NetworkHandler
import com.cbi.app.trs.data.cache.SystemDataCache
import com.cbi.app.trs.data.entities.MovieData
import com.cbi.app.trs.data.entities.ReviewData
import com.cbi.app.trs.data.entities.SystemData
import com.cbi.app.trs.domain.entities.movie.MovieListEntity
import com.cbi.app.trs.domain.entities.system.*
import com.cbi.app.trs.domain.network.BaseNetwork
import com.cbi.app.trs.domain.services.SystemDataService
import javax.inject.Inject

interface SystemDataRepository {
    fun getSystemConfig(): Either<Failure, SystemConfigEntity.Data>
    fun getSystemArea(fromCache: Boolean): Either<Failure, List<SystemData.Area>>
    fun getSystemAreaFilteredByDaily(fromCache: Boolean): Either<Failure, List<SystemData.Area>>
    fun getSystemPainArea(fromCache: Boolean): Either<Failure, List<SystemData.PainArea>>
    fun getSystemEquipment(fromCache: Boolean): Either<Failure, List<SystemData.Equipment>>
    fun getSystemAchievement(fromCache: Boolean): Either<Failure, List<SystemData.Achievement>>
    fun getSystemCollection(fromCache: Boolean): Either<Failure, List<SystemData.Collection>>
    fun getSystemPreWorkout(fromCache: Boolean): Either<Failure, List<SystemData.PreWorkout>>
    fun getSystemPostWorkout(fromCache: Boolean): Either<Failure, List<SystemData.PostWorkout>>
    fun getDownloadedMovie(): Either<Failure, List<MovieData>>
    fun getSystemBonus(fromCache: Boolean): Either<Failure, List<SystemData.Bonus>>
    fun getIntro(): Either<Failure, List<MovieData>>
    fun getReview(): Either<Failure, List<ReviewData>>

    class Network
    @Inject constructor(private val networkHandler: NetworkHandler,
                        private val service: SystemDataService,
                        private val systemDataCache: SystemDataCache) : SystemDataRepository, BaseNetwork() {
        override fun getSystemConfig(): Either<Failure, SystemConfigEntity.Data> {
//            if (systemDataCache.get().config != null) {
//                return Either.Right(systemDataCache.get().config!!)
//            }

            return when (networkHandler.isConnected) {
                true -> request(service.systemConfig(), {
                    it.data
                }, SystemConfigEntity.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun getSystemArea(fromCache: Boolean): Either<Failure, List<SystemData.Area>> {
            if (fromCache && systemDataCache.get().area != null) {
                return Either.Right(systemDataCache.get().area!!)
            }

            return when (networkHandler.isConnected) {
                true -> request(service.systemArea(), {
                    it.data.map { it1 -> SystemData.Area(it1.area_title, it1.area_id) }.also { it2 ->
                        systemDataCache.put(systemDataCache.get().apply {
                            area = it2
                        })
                    }
                }, SystemAreaEntity.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun getSystemAreaFilteredByDaily(fromCache: Boolean): Either<Failure, List<SystemData.Area>> {
            if (fromCache && systemDataCache.get().areaFiltered != null) {
                return Either.Right(systemDataCache.get().areaFiltered!!)
            }

            return when (networkHandler.isConnected) {
                true -> request(service.systemAreaFilteredByDaily(), {
                    it.data.map { it1 -> SystemData.Area(it1.area_title, it1.area_id) }.also { it2 ->
                        systemDataCache.put(systemDataCache.get().apply {
                            areaFiltered = it2
                        })
                    }
                }, SystemAreaEntity.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun getSystemPainArea(fromCache: Boolean): Either<Failure, List<SystemData.PainArea>> {
            if (fromCache && systemDataCache.get().painArea != null) {
                return Either.Right(systemDataCache.get().painArea!!)
            }

            return when (networkHandler.isConnected) {
                true -> request(service.systemPainArea(), {
                    it.data.map { it1 -> SystemData.PainArea(it1.pain_area_title, it1.pain_area_key, it1.pain_area_id, it1.pain_area_type) }.also { it2 ->
                        systemDataCache.put(systemDataCache.get().apply {
                            painArea = it2
                        })
                    }
                }, SystemPainAreaEntity.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun getSystemEquipment(fromCache: Boolean): Either<Failure, List<SystemData.Equipment>> {
            if (fromCache && systemDataCache.get().equipment != null) {
                return Either.Right(systemDataCache.get().equipment!!)
            }

            return when (networkHandler.isConnected) {
                true -> request(service.systemEquipment(), {
                    it.data.map { it1 -> SystemData.Equipment(it1.equipment_title, it1.equipment_id) }.also { it2 ->
                        systemDataCache.put(systemDataCache.get().apply {
                            equipment = it2
                        })
                    }
                }, SystemEquipmentEntity.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun getSystemAchievement(fromCache: Boolean): Either<Failure, List<SystemData.Achievement>> {
            if (fromCache && systemDataCache.get().achievement != null) {
                return Either.Right(systemDataCache.get().achievement!!)
            }

            return when (networkHandler.isConnected) {
                true -> request(service.systemAchievement(), {
                    it.data.map { it1 -> SystemData.Achievement(it1.achievement_id, it1.achievement_title, it1.achievement_description, it1.achievement_milestone) }.also { it2 ->
                        systemDataCache.put(systemDataCache.get().apply {
                            achievement = it2
                        })
                    }
                }, SystemAchievementEntity.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun getSystemCollection(fromCache: Boolean): Either<Failure, List<SystemData.Collection>> {
            if (fromCache && systemDataCache.get().collection != null) {
                return Either.Right(systemDataCache.get().collection!!)
            }

            return when (networkHandler.isConnected) {
                true -> request(service.systemCollection(), {
                    it.data.map { it1 -> SystemData.Collection(it1.collection_title, it1.collection_id) }.also { it2 ->
                        systemDataCache.put(systemDataCache.get().apply {
                            collection = it2
                        })
                    }
                }, SystemCollectionEntity.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun getSystemPreWorkout(fromCache: Boolean): Either<Failure, List<SystemData.PreWorkout>> {
            if (fromCache && systemDataCache.get().preWorkout != null) {
                return Either.Right(systemDataCache.get().preWorkout!!)
            }

            return when (networkHandler.isConnected) {
                true -> request(service.systemPreWorkout(), {
                    it.data.map { it1 -> SystemData.PreWorkout(it1.pre_title, it1.pre_id) }.also { it2 ->
                        systemDataCache.put(systemDataCache.get().apply {
                            preWorkout = it2
                        })
                    }
                }, SystemPreWorkoutEntity.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun getSystemPostWorkout(fromCache: Boolean): Either<Failure, List<SystemData.PostWorkout>> {
            if (fromCache && systemDataCache.get().postWorkout != null) {
                return Either.Right(systemDataCache.get().postWorkout!!)
            }

            return when (networkHandler.isConnected) {
                true -> request(service.systemPostWorkout(), {
                    it.data.map { it1 -> SystemData.PostWorkout(it1.post_title, it1.post_id) }.also { it2 ->
                        systemDataCache.put(systemDataCache.get().apply {
                            postWorkout = it2
                        })
                    }
                }, SystemPostWorkoutEntity.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun getDownloadedMovie(): Either<Failure, List<MovieData>> {
            TODO("Not yet implemented")
        }

        override fun getSystemBonus(fromCache: Boolean): Either<Failure, List<SystemData.Bonus>> {
            if (fromCache && systemDataCache.get().bonus != null) {
                return Either.Right(systemDataCache.get().bonus!!)
            }

            return when (networkHandler.isConnected) {
                true -> request(service.systemBonus(), {
                    it.data.also { it2 ->
                        systemDataCache.put(systemDataCache.get().apply {
                            bonus = it2
                        })
                    }
                }, SystemBonusEntity.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun getIntro(): Either<Failure, List<MovieData>> {
            return when (networkHandler.isConnected) {
                true -> request(service.getIntroMovie(), {
                    it.data
                }, MovieListEntity.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun getReview(): Either<Failure, List<ReviewData>> {
            return when (networkHandler.isConnected) {
                true -> request(service.getReview(), {
                    it.data
                }, ReviewEntity())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }
    }
}