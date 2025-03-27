package com.youssef.weatherforcast.Navigation


import android.util.Log
import android.view.animation.OvershootInterpolator
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.*
import com.youssef.weatherforcast.R

@Composable
fun SplashScreen(navController: NavController) {

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.splash_animation))
    val progress by animateLottieCompositionAsState(composition)

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier.size(250.dp)
        )
    }

    LaunchedEffect(progress) {
      //  Log.d("tagdata", "Splash progress: $progress") // ✅ تأكد إن progress بيتغير
        if (progress == 1f) {
            Log.d("tagdata", "Navigating to HomeScreen") // ✅ تأكد إن `navigate` بيشتغل
            navController.navigate(Screen.Home.route) {
                popUpTo(Screen.Splash.route) { inclusive = true }
            }
        }
    }
}
