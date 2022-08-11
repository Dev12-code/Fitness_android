package com.cbi.app.trs.domain.repositories

import com.cbi.app.trs.core.exception.Failure
import com.cbi.app.trs.core.functional.Either
import com.cbi.app.trs.core.platform.NetworkHandler
import com.cbi.app.trs.data.entities.CategoryData
import com.cbi.app.trs.data.entities.MovieData
import com.cbi.app.trs.domain.entities.workout.*
import com.cbi.app.trs.domain.network.BaseNetwork
import com.cbi.app.trs.domain.services.WorkoutService
import javax.inject.Inject

interface WorkoutRepository {
    fun getWorkoutEquipment(userId: Int, limit: Int, page: Int): Either<Failure, WorkoutEntity.Data>
    fun getWorkoutPrePost(userId: Int, limit: Int, page: Int): Either<Failure, WorkoutEntity.Data>
    fun getWorkoutSport(userId: Int, limit: Int, page: Int): Either<Failure, WorkoutEntity.Data>
    fun getWarmUp(userId: Int, categoryId: Int, limit: Int, page: Int): Either<Failure, WorkoutEntity.Data>
    fun getWorkoutArchetype(userId: Int, limit: Int, page: Int): Either<Failure, WorkoutEntity.Data>
    fun getCategoryArchetype(userId: Int?, limit: Int, page: Int): Either<Failure, List<CategoryData>>
    fun getCategorySport(userId: Int?, limit: Int, page: Int): Either<Failure, List<CategoryData>>
    fun getCategoryWorkout(userId: Int?, limit: Int, page: Int): Either<Failure, List<CategoryData>>
    fun getSuggestByCategory(userId: Int?, categoryId: Int, equipmentIds: List<Int>, focusAreas: List<Int>,
                             minDuration: Int?, maxDuration: Int?, prePostFilter: Int?, collectionSlug: String?, limit: Int, page: Int): Either<Failure, SuggestByCategoryEntity.Data>

    fun getRelatedByCategory(userId: Int?, categoryId: Int, equipmentIds: List<Int>, focusAreas: List<Int>,
                             minDuration: Int?, maxDuration: Int?, prePostFilter: Int?, collectionSlug: String?, limit: Int, page: Int): Either<Failure, RelatedByCategoryEntity.Data>

    fun getMovementGuide(userId: Int?, categoryId: Int): Either<Failure, MovieData>

    class Network
    @Inject constructor(private val networkHandler: NetworkHandler,
                        private val service: WorkoutService) : WorkoutRepository, BaseNetwork() {

        override fun getWorkoutEquipment(userId: Int, limit: Int, page: Int): Either<Failure, WorkoutEntity.Data> {
            return when (networkHandler.isConnected) {
                true -> request(service.getWorkoutEquipment(userId, limit, page), {
                    it.data
                }, WorkoutEntity.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun getWorkoutPrePost(userId: Int, limit: Int, page: Int): Either<Failure, WorkoutEntity.Data> {
            return when (networkHandler.isConnected) {
                true -> request(service.getWorkoutPrePost(userId, limit, page), {
                    it.data
                }, WorkoutEntity.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun getWorkoutSport(userId: Int, limit: Int, page: Int): Either<Failure, WorkoutEntity.Data> {
            return when (networkHandler.isConnected) {
                true -> request(service.getWorkoutSport(userId, limit, page), {
                    it.data
                }, WorkoutEntity.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun getWarmUp(userId: Int, categoryId: Int, limit: Int, page: Int): Either<Failure, WorkoutEntity.Data> {
            return when (networkHandler.isConnected) {
                true -> request(service.getWarmUp(userId, categoryId, limit, page), {
                    it.data
                }, WorkoutEntity.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun getWorkoutArchetype(userId: Int, limit: Int, page: Int): Either<Failure, WorkoutEntity.Data> {
            return when (networkHandler.isConnected) {
                true -> request(service.getWorkoutArchetype(userId, limit, page), {
                    it.data
                }, WorkoutEntity.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun getCategoryArchetype(userId: Int?, limit: Int, page: Int): Either<Failure, List<CategoryData>> {
            return when (networkHandler.isConnected) {
                true -> request(service.getCategoryArchetype(userId, limit, page), {
                    it.data
                }, CategoryEntity())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun getCategorySport(userId: Int?, limit: Int, page: Int): Either<Failure, List<CategoryData>> {
            return when (networkHandler.isConnected) {
                true -> request(service.getCategorySport(userId, limit, page), {
                    it.data
                }, CategoryEntity())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun getCategoryWorkout(userId: Int?, limit: Int, page: Int): Either<Failure, List<CategoryData>> {
            return when (networkHandler.isConnected) {
                true -> request(service.getCategoryWorkout(userId, limit, page), {
                    it.data
                }, CategoryEntity())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun getSuggestByCategory(userId: Int?, categoryId: Int, equipmentIds: List<Int>, focusAreas: List<Int>, minDuration: Int?, maxDuration: Int?, prePostFilter: Int?, collectionSlug: String?, limit: Int, page: Int): Either<Failure, SuggestByCategoryEntity.Data> {
            return when (networkHandler.isConnected) {
                true -> request(service.getSuggestByCategory(userId, categoryId, equipmentIds, focusAreas, minDuration, maxDuration, prePostFilter, collectionSlug, limit, page), {
                    it.data
                }, SuggestByCategoryEntity.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun getRelatedByCategory(userId: Int?, categoryId: Int, equipmentIds: List<Int>, focusAreas: List<Int>, minDuration: Int?, maxDuration: Int?, prePostFilter: Int?, collectionSlug: String?, limit: Int, page: Int): Either<Failure, RelatedByCategoryEntity.Data> {
            return when (networkHandler.isConnected) {
                true -> request(service.getRelatedByCategory(userId, categoryId, equipmentIds, focusAreas, minDuration, maxDuration, prePostFilter, collectionSlug, limit, page), {
                    it.data
                }, RelatedByCategoryEntity.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun getMovementGuide(userId: Int?, categoryId: Int): Either<Failure, MovieData> {
            return when (networkHandler.isConnected) {
                true -> request(service.getMovementGuide(userId, categoryId), {
                    it.data
                }, MovementGuideEntity.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }
    }
}