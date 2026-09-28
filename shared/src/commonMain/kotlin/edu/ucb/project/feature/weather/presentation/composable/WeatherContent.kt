package edu.ucb.project.feature.weather.presentation.composable


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


import edu.ucb.project.feature.weather.domain.model.Weather



@Composable
fun WeatherContent(

    weather: Weather?,

    loading:Boolean

){


    Column(

        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)

    ){



        Text(

            text = "Clima actual",

            fontSize = 26.sp,

            style = MaterialTheme.typography.headlineMedium

        )



        Spacer(
            modifier = Modifier.height(12.dp)
        )



        if(loading){


            CircularProgressIndicator()



        }else if(weather != null){



            Card(

                modifier = Modifier
                    .fillMaxWidth()

            ){


                Column(

                    modifier = Modifier
                        .padding(20.dp),

                    horizontalAlignment = Alignment.CenterHorizontally

                ){



                    Text(

                        text = "${weather.temperature} °C",

                        fontSize = 48.sp

                    )



                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )



                    Text(

                        text = "Velocidad viento: ${weather.windSpeed} km/h",

                        fontSize = 18.sp

                    )



                    Text(

                        text = "Dirección viento: ${weather.windDirection}°",

                        fontSize = 18.sp

                    )



                    Text(

                        text = "Código clima: ${weather.weatherCode}",

                        fontSize = 18.sp

                    )



                    Text(

                        text = weather.time,

                        fontSize = 14.sp

                    )



                }


            }


        }



    }


}