package com.jpz.workoutnotebook.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement
import com.jpz.workoutnotebook.R
import kotlinx.android.synthetic.main.fragment_statistics.*


class StatisticsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_statistics, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        val aaChartModel: AAChartModel = AAChartModel()
            .chartType(AAChartType.Line)
            .title("Push up")
            .yAxisTitle("Repetitions")
            .backgroundColor(R.color.colorTextSecondary)
            .categories(arrayOf("18/10", "19/10", "20/10"))
            .series(
                arrayOf(
                    AASeriesElement()
                        .name("Set 1")
                        .data(arrayOf(20, 20, 20)),
                    AASeriesElement()
                        .name("Set 2")
                        .data(arrayOf(19, 20, 20)),
                    AASeriesElement()
                        .name("Set 3")
                        .data(arrayOf(18, 19, 19)),
                    AASeriesElement()
                        .name("Set 4")
                        .data(arrayOf(14, 14, 16))
                )
            )
        // The chart view object calls the instance object of AAChartModel and draws the final graphic
        fragmentStatisticsChartView.aa_drawChartWithChartModel(aaChartModel)
    }
}