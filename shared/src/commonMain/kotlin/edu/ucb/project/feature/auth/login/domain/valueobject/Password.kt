package edu.ucb.project.feature.auth.login.domain.valueobject

data class Password(
    val value:String
){
    init {
        require(value.length >= 6){
            "La contraseña debe tener mínimo 6 caracteres"
        }
    }
}