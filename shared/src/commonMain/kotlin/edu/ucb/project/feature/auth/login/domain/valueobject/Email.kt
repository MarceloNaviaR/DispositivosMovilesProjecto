package edu.ucb.project.feature.auth.login.domain.valueobject


data class Email(
    val value:String
){
    init {
        require(value.contains("@")){
            "Email inválido"
        }
    }
}