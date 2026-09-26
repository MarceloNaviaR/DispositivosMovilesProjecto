package edu.ucb.project.navigation


import kotlinx.serialization.Serializable



@Serializable
sealed class NavRoute {


    @Serializable
    data object Login: NavRoute()



    @Serializable
    data object Profile: NavRoute()



    @Serializable
    data object ProfileEdit: NavRoute()


}