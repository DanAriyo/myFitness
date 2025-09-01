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
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.Entry

@Composable
fun MyLineChart(modifier: Modifier = Modifier, entries: List<Entry>, labels: List<String>) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            LineChart(context).apply {
                // Configurazione di base
                description.isEnabled = false
                legend.isEnabled = false

                // Configurazione dell'asse X (come nel grafico a barre)
                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(false)
                    setDrawAxisLine(true)
                    setDrawLabels(true)
                    granularity = 1f
                    valueFormatter = IndexAxisValueFormatter(labels)
                }

                // Configurazione dell'asse Y
                axisLeft.apply {
                    setDrawGridLines(false)
                    setDrawAxisLine(true)
                    setDrawLabels(true)
                    axisMinimum = 0f
                }
                axisRight.isEnabled = false
            }
        },
        update = { lineChart ->
            if (entries.isNotEmpty()) {
                val dataSet = LineDataSet(entries, "Allenamenti Settimanali").apply {
                    color = ColorTemplate.getHoloBlue()
                    setCircleColor(ColorTemplate.getHoloBlue())
                    setDrawValues(false)
                    mode = LineDataSet.Mode.CUBIC_BEZIER
                }
                lineChart.data = LineData(dataSet)
                lineChart.invalidate()
            } else {
                lineChart.clear()
            }
        }
    )
}