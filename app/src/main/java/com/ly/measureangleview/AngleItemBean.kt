package com.ly.measureangleview
import androidx.annotation.Keep


@Keep
data class AngleItemBean(
    val angleName: String,
    val guideLineCoordinates: List<GuideLineCoordinate>,
    val keyPiontsStr: String,
    val keyPointsCoordinates: List<KeyPointsCoordinate>,
    val position: String,
    val rule: Rule
)

@Keep
data class GuideLineCoordinate(
    val name: String,
    val x: Int,
    val y: Any
)

@Keep
data class KeyPointsCoordinate(
    val name: String,
    val x: Int,
    val y: Int
)

@Keep
data class Rule(
    val biasPoint: Any,
    val calculateRule: String,
    val firstLine: String,
    val horizontalPass: Any,
    val isCalculateAngle: Boolean,
    val secondLine: String,
    val verticalPass: Any
)