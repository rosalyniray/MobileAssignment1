package com.example.monthlyemicalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.monthlyemicalculator.ui.theme.MonthlyEMICalculatorTheme
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.OutlinedTextField


@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MonthlyEMICalculatorTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.secondary
                ) {
                    MortgageCalculator()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MortgageCalculator() {
    var principal by remember { mutableStateOf("") }
    var interestRate by remember { mutableStateOf("") }
    var amortizationPeriod by remember { mutableStateOf(25f) }
    var paymentFrequency by remember { mutableStateOf("Monthly") }
    var emi by remember { mutableStateOf(0.0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title at the top
        Text(
            text = "Monthly EMI Calculator",
            style = MaterialTheme.typography.headlineMedium.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = principal,
            onValueChange = { principal = it },
            label = { Text("Mortgage Principal Amount", color = Color.White) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color.White,
                textColor = Color.White,
                cursorColor = Color.White,
                focusedLabelColor = Color.White,
                unfocusedLabelColor = Color.White
            )
        )

        OutlinedTextField(
            value = interestRate,
            onValueChange = { interestRate = it },
            label = { Text("Interest Rate (%)", color = Color.White) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color.White,
                textColor = Color.White,
                cursorColor = Color.White,
                focusedLabelColor = Color.White,
                unfocusedLabelColor = Color.White
            )
        )

        Column {
            Text(
                text = "Amortization Period: ${amortizationPeriod.toInt()} years",
                modifier = Modifier.padding(bottom = 8.dp),
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White // Set this text to white
            )
            Slider(
                value = amortizationPeriod,
                onValueChange = { amortizationPeriod = it },
                valueRange = 1f..30f,
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    activeTrackColor = MaterialTheme.colorScheme.primary,
                    thumbColor = Color.White
                )
            )
        }

        DropdownMenuComponent(
            paymentFrequency = paymentFrequency,
            onFrequencySelected = { paymentFrequency = it }
        )

        Button(
            onClick = {
                val principalAmount = principal.toDoubleOrNull() ?: 0.0
                val rate = interestRate.toDoubleOrNull() ?: 0.0
                val period = amortizationPeriod.toDouble()
                emi = calculateEMI(principalAmount, rate, period, paymentFrequency)
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            enabled = principal.isNotEmpty() && interestRate.isNotEmpty()
        ) {
            Text("Calculate Payment", color = Color.White)
        }

        Text(
            text = "Your Payment: $%.2f".format(emi),
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White // Set this text to white
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownMenuComponent(paymentFrequency: String, onFrequencySelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val options = listOf("Monthly", "Bi-Weekly", "Weekly")

    Box {
        OutlinedTextField(
            value = paymentFrequency,
            onValueChange = { },
            label = { Text("Payment Frequency", color = Color.White) },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color.White,
                textColor = Color.White,
                cursorColor = Color.White,
                focusedLabelColor = Color.White,
                unfocusedLabelColor = Color.White
            ),
            trailingIcon = {
                IconButton(onClick = { expanded = true }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowDropDown,
                        contentDescription = null,
                        tint = Color.White // Set arrow icon color to white
                    )
                }
            }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, color = Color.White) }, // Set dropdown text color to white
                    onClick = {
                        onFrequencySelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

fun calculateEMI(principal: Double, annualRate: Double, years: Double, frequency: String): Double {
    val monthlyRate = annualRate / 12 / 100
    val tenureInMonths = years * 12
    val emi = (principal * monthlyRate * Math.pow(1 + monthlyRate, tenureInMonths)) /
            (Math.pow(1 + monthlyRate, tenureInMonths) - 1)

    return when (frequency) {
        "Monthly" -> emi
        "Bi-Weekly" -> emi / 2
        "Weekly" -> emi / 4
        else -> emi
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun MortgageCalculatorPreview() {
    MonthlyEMICalculatorTheme {
        MortgageCalculator()
    }
}
