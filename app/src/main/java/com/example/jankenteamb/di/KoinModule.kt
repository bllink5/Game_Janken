package com.example.jankenteamb.di

import com.example.jankenteamb.async.FirebaseAsync
import com.example.jankenteamb.repository.firebase.*
import com.example.jankenteamb.repository.room.RoomAchievementRepository
import com.example.jankenteamb.repository.room.RoomUserRepository
import com.example.jankenteamb.utils.FirebaseHelper
import com.example.jankenteamb.utils.PreferenceHelper
import com.example.jankenteamb.viewmodel.*
import org.koin.dsl.module

var koinModule = module {
    factory { PreferenceHelper(get()) }
    factory { FirebaseHelper(get(), get()) }
    factory { FirebaseAsync(get()) }
}

val firebaseRepositoryModule = module {
    factory { FirebaseAchievementRepository(get()) }
    factory { FirebaseFrameCollectionRepository(get()) }
    factory { FirebaseFriendListRepository(get()) }
    factory { FirebaseHistoryRepository(get()) }
    factory { FirebaseNotificationRepository(get()) }
    factory { FirebaseStorageRepository(get()) }
    factory { FirebaseUserDataCollectionRepository(get()) }
    factory { FirebaseUserRepository(get()) }
    factory { FirebaseAuthRepository(get()) }
}

val roomRepositoryModule = module {
    factory { RoomAchievementRepository(get(), get()) }
    factory { RoomUserRepository(get(), get()) }
}

val viewModelModule = module {
    factory { LoginViewModel.Factory(get(), get(), get(), get(), get(), get(), get(), get()) }
    factory { BattleViewModel.Factory(get(), get(), get(), get(), get(), get()) }
    factory { RegisterViewModel.Factory(get(), get(), get(), get()) }
    factory { ProfileViewModel.Factory(get(), get(), get(), get(), get(), get(), get()) }
    factory { EditPhotoProfileViewModel.Factory(get(), get(), get(), get(), get(), get()) }
    factory { ShopViewModel.Factory(get(), get(), get(), get(), get()) }
    factory { FriendListViewModel.Factory(get(), get(), get()) }
    factory { MainMenuViewModel.Factory(get(), get(), get(), get(), get()) }
    factory { FriendSuggestViewModel.Factory(get(), get(), get(), get(), get(), get(), get()) }
    factory { AchievementViewModel.Factory(get(), get(), get(), get(), get()) }
    factory { MenuHistoryViewModel.Factory(get(), get()) }
}