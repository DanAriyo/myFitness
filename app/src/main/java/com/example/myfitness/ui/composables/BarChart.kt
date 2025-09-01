// ui/composables/MyBarChart.kt

package com.example.myfitness.ui.composables

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate

@Composable
fun MyBarChart(modifier: Modifier = Modifier, entries: List<BarEntry>, labels: List<String>) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            BarChart(context).apply {
                // Rimuovi descrizione (già fatto)
                description.isEnabled = false
                // Rimuovi legenda (già fatto)
                legend.isEnabled = false

                // --- Configurazione Asse X ---
                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM // Posiziona l'asse X in basso
                    setDrawGridLines(false) // ✅ Rimuovi le linee della griglia per l'asse X
                    setDrawAxisLine(true) // Mostra la linea dell'asse X
                    setDrawLabels(true) // Mostra le etichette dell'asse X
                    granularity = 1f // Assicura che le etichette siano per ogni entrata
                    valueFormatter = IndexAxisValueFormatter(labels) // ✅ Formatta le etichette con i nomi degli allenamenti
                }

                // --- Configurazione Asse Y (Left) ---
                axisLeft.apply {
                    setDrawGridLines(false) // ✅ Rimuovi le linee della griglia per l'asse Y
                    setDrawAxisLine(true) // Mostra la linea dell'asse Y
                    setDrawLabels(true) // Mostra le etichette dell'asse Y
                    axisMinimum = 0f // Inizia da 0
                    // Puoi aggiungere un formatore per le etichette Y se necessario
                }

                // --- Configurazione Asse Y (Right) ---
                axisRight.isEnabled = false // ✅ Disabilita l'asse Y di destra

                // Imposta un listener per rendere il grafico interattivo (opzionale)
                // setOnChartValueSelectedListener(this@MyBarChart)
            }
        },
        update = { barChart ->
            if (entries.isNotEmpty()) {
                val dataSet = BarDataSet(entries, "Calorie").apply {
                    colors = ColorTemplate.VORDIPLOM_COLORS.toList()
                    setDrawValues(false) // Non disegnare il valore sopra ogni barra
                }
                barChart.data = BarData(dataSet)
                barChart.invalidate() // Ricarica il grafico
            } else {
                barChart.clear()
            }
        }
    )
}