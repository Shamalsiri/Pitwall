package com.ssiriwardana.pitwall.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.ssiriwardana.pitwall.domain.model.Driver
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.core.graphics.toColorInt

@Composable
fun DriverDetailDialog(
    driver: Driver,
    onDismiss: () -> Unit,
    isFav: Boolean,
    onFavToggle: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.9f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ){
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = driver.teamColor?.let {
                                Color(
                                    "#$it".toColorInt()
                                )
                            }
                                ?: MaterialTheme.colorScheme.primaryContainer
                        )
                        .padding(16.dp)
                ) {
                    IconButton(
                        modifier = Modifier.align(Alignment.TopEnd),
                        onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White
                        )
                    }

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ){

                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.9f)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (driver.headshotUrl != null) {
                                AsyncImage(
                                    model = driver.headshotUrl,
                                    contentDescription = "Image of ${driver.fullName}",
                                    modifier = Modifier
                                        .size(115.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Text(
                                    text = driver.code,
                                    style = MaterialTheme.typography.displayMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primaryContainer

                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = driver.fullName,
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White

                        )

                        driver.permanentNumber?. let { number ->
                            Text(
                                text = number,
                                style = MaterialTheme.typography.headlineMedium,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }
                }

                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(16.dp)
                    ) {

                        Row(modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = isFav,
                                onCheckedChange = { onFavToggle() }
                                )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Mark as Favorite",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Title(title = "Bio")

                        DetailRow("Driver Code", driver.code)
                        DetailRow("First Name", driver.firstName)
                        DetailRow("Last Name", driver.lastName)
                        DetailRow("Nationality", driver.nationality)

                        driver.dateOfBirth?. let {
                            dob -> val formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy")
                            DetailRow("Date of Birth", dob.format(formatter))
                            driver.age?. let { age ->
                                DetailRow("Age", "$age yrs")
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Title(title = "Team Info")

                        driver.teamName?.let { team ->
                            DetailRow("Current Team", team)
                        }


                        Spacer(modifier = Modifier.height(16.dp))

                        driver.wikiUrl?.let { url ->
                            Title("Additional Info")

                            val uriHandler = LocalUriHandler.current

                            Button(
                                onClick = {uriHandler.openUri(url)},
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("View Wikipedia Page")
                            }
                        }

                    }
                }
            }

        }
    }


@Composable
private fun Title(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
@Preview
fun PreviewDriverDetailDialog() {
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

    DriverDetailDialog(driver, {}, false, {})
}