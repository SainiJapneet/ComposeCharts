package ir.ehsannarmani.compose_charts.utils

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import ir.ehsannarmani.compose_charts.models.AnimationMode
import ir.ehsannarmani.compose_charts.models.Bars
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


/*
* RC: Row/Column Animation
* */
@Composable
fun ImplementRCAnimation(
    data:List<Bars>,
    animationMode: AnimationMode,
    spec: (Bars.Data)->AnimationSpec<Float>,
    delay:Long,
    before:()->Unit
) {
    LaunchedEffect(data) {
        before()
        delay(delay)
        data.filter { it.values.any { it.value != 0.0 } }.forEachIndexed { colIndex, columnChart ->
            // animate just values which greater than zero
            columnChart.values.filter { it.value != 0.0 }.forEachIndexed { dataIndex, data ->
                val animate: suspend () -> Unit = {
                    data.animator.animateTo(
                        1f,
                        animationSpec = spec(data)
                    )
                }
                when (animationMode) {
                    is AnimationMode.OneByOne -> {
                        animate()
                    }

                    is AnimationMode.Together -> {
                        launch {
                            delay(animationMode.delayBuilder((colIndex * columnChart.values.count()) + dataIndex))
                            animate()
                        }
                    }
                }
            }
        }
    }
}