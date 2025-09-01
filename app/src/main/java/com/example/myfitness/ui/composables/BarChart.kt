package com.example.myfitness.ui.composables

// BarChartWrapper.kt

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.utils.ColorTemplate

@Composable
fun MyBarChart(modifier: Modifier = Modifier, entries: List<BarEntry>) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            // Create a BarChart instance
            BarChart(context).apply {
                // Initial chart configuration
                description.isEnabled = false
                legend.isEnabled = false
            }
        },
        update = { barChart ->
            // Update the chart data
            if (entries.isNotEmpty()) {
                val dataSet = BarDataSet(entries, "Dati Allenamento").apply {
                    colors = ColorTemplate.VORDIPLOM_COLORS.toList()
                    setDrawValues(true)
                }
                barChart.data = BarData(dataSet)
                barChart.invalidate() // Refresh the chart
            } else {
                barChart.clear() // Clear the chart if there are no entries
            }
        }
    )
}