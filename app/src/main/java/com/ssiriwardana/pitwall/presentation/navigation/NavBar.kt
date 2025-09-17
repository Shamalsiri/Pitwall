package com.ssiriwardana.pitwall.presentation.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.ssiriwardana.pitwall.R

@Composable
fun NavBar(
    currentRoute: String,
    onNavigation: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    Surface(
        modifier.fillMaxWidth(), shadowElevation = 80.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .height(80.dp)
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            //Driver
            NavItem(
                imageRes = R.drawable.nav_drivers,
                contentDesc = "Open Drivers View",
                isSelected = currentRoute == NavRoutes.DRIVERS,
                onClick = {onNavigation(NavRoutes.DRIVERS)},
                modifier = Modifier.weight(1f)
            )

            Seperator()

            //Constructor
            NavItem(
                imageRes = R.drawable.nav_constructors,
                contentDesc = "Open Constructors View",
                isSelected = currentRoute == NavRoutes.CONSTRUCTORS,
                onClick = {onNavigation(NavRoutes.CONSTRUCTORS)},
                modifier = Modifier.weight(1f)
            )

            Seperator()

            //Circuits
            NavItem(
                imageRes = R.drawable.nav_circuits,
                contentDesc = "Open circuits View",
                isSelected = currentRoute == NavRoutes.CIRCUITS,
                onClick = {onNavigation(NavRoutes.CIRCUITS)},
                modifier = Modifier.weight(1f)
            )

            Seperator()

            //Races
            NavItem(
                imageRes = R.drawable.nav_races,
                contentDesc = "Open races View",
                isSelected = currentRoute == NavRoutes.RACES,
                onClick = {onNavigation(NavRoutes.RACES)},
                modifier = Modifier.weight(1f)
            )

            Seperator()

            //Setting
            NavItem(
                imageRes = R.drawable.nav_settings,
                contentDesc = "Open setting View",
                isSelected = currentRoute == NavRoutes.SETTINGS,
                onClick = {onNavigation(NavRoutes.SETTINGS)},
                modifier = Modifier.weight(1f)
            )

        }
    }
}

@Composable
private fun NavItem(
    imageRes: Int,
    contentDesc: String,
    isSelected: Boolean,
    onClick: ()-> Unit,
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(bottom = 10.dp)

){
    Box(
        modifier = modifier
            .fillMaxHeight()
            .clickable{onClick()}
            .padding(paddingValues),
        contentAlignment = Alignment.Center
    ){
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = contentDesc,
            modifier = Modifier.size(70.dp),
            colorFilter = if (isSelected) {
                ColorFilter.tint(MaterialTheme.colorScheme.primary)
            } else {
                ColorFilter.tint(Color(0xFFC0B5B5))
            }
        )
    }
}

@Composable
private fun Seperator(){
    Box(
        modifier = Modifier
            .width(2.dp)
            .height(35.dp)
            .background(Color.LightGray)
    )
}