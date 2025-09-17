package com.ssiriwardana.pitwall.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import coil.compose.AsyncImage
import com.ssiriwardana.pitwall.domain.model.Driver
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Driver card composable to be displayed in the grid
 */
@Composable
fun DCard(modifier: Modifier = Modifier, onClick: () -> Unit, driver: Driver) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),

        ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(
                        color = driver.teamColor?.let {
                            MaterialTheme.colorScheme.surfaceVariant
                            Color("#$it".toColorInt())
                        } ?: MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                if (driver.headshotUrl != null) {
                    AsyncImage(
                        model = driver.headshotUrl,
                        contentDescription = "Image of ${driver.fullName}",
                        modifier = Modifier
                            .size(75.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text = driver.code,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            driver.permanentNumber?.let { number ->
                Text(
                    text = number,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Text(
                text = driver.firstName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
            Text(
                text = driver.lastName,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 4.dp)
            )

            Text(
                text = driver.code,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.height(4.dp))

            driver.teamName?.let { team ->
                Text(
                    text = team,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Text(
                    text = driver.nationality,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
                driver.age?.let { age ->
                    Text(
                        text = "• Age: $age",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }

    }
}

@Composable
fun DriverCard(modifier: Modifier = Modifier,
               onClick: () -> Unit, driver: Driver,
               isFav: Boolean = false) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceEvenly
        ){
            Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(
                            color = driver.teamColor?.let {
                                MaterialTheme.colorScheme.surfaceVariant
                                Color("#$it".toColorInt())
                            } ?: MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    if (driver.headshotUrl != null) {
                        AsyncImage(
                            model = driver.headshotUrl,
                            contentDescription = "Image of ${driver.fullName}",
                            modifier = Modifier
                                .size(75.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text(
                            text = driver.code,
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                driver.age?.let { age ->
                    Spacer(modifier.height(4.dp))
                    Text(
                        text = "Age: $age",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            Column(modifier = Modifier.fillMaxWidth(0.75f).padding(start = 8.dp)) {
                Text(
                    text = driver.firstName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )

                Text(
                    text = driver.lastName,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )

                driver.teamName?.let { team ->
                    Text(
                        text = team,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(horizontal = 4.dp)

                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    modifier = Modifier.padding(horizontal = 4.dp),


                ) {
                    Text(
                        text = driver.nationality,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )

                }
            }

            Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = driver.permanentNumber.toString(),
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(horizontal = 4.dp),
                    color = if (isFav) {
                        Color(0xFFFFD700)
                    }else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
                Text(
                    text = driver.code,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )


            }
        }
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun PreviewDriverCard() {
    val formatter = DateTimeFormatter.ofPattern("M/d/yyyy")
    val driver = Driver(
        id = "test1",
        permanentNumber = "55",
        code = "SIR",
        firstName = "Shamal",
        lastName = "Siriwardana",
        fullName = "Shamal V Siriwardana",
        dateOfBirth = LocalDate.parse("8/8/1996", formatter),
        nationality = "Sri Lankan",
        teamName = "testTeam",
        teamColor = "2D4E5F",
        headshotUrl = "https://www.formula1.com/content/dam/fom-website/drivers/M/MAXVER01_Max_Verstappen/maxver01.png.transform/1col/image.png",
        wikiUrl = "https://wikiUrl.com"
    )

    Column(Modifier.fillMaxHeight(), verticalArrangement = Arrangement.Center) {
        DriverCard(onClick = {}, driver = driver)

    }
}
