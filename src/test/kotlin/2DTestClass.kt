package laser

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.floats.plusOrMinus
import io.kotest.matchers.shouldBe

class `2DTestClass` : DescribeSpec({
    val tolerance = 1e-5f

    xdescribe("Point") {

        it("should offset a point correctly") {
            val point = Point(1.0f, 2.0f)
            point.offset(3.0f, 4.0f)
            point.x shouldBe (4.0f plusOrMinus tolerance)
            point.y shouldBe (6.0f plusOrMinus tolerance)
        }

        it("should not rotate a point with angle 0 correctly") {
            val point = Point(1.0f, 2.0f)
            point.rotate(0.0f, 0.0f, 0.0f)
            point.x shouldBe (1.0f plusOrMinus tolerance)
            point.y shouldBe (2.0f plusOrMinus tolerance)
        }

        it("should rotate a point around the origin correctly") {
            val point = Point(1.0f, 0.0f)
            point.rotate(0.5f, 0.0f, 0.0f)  // Rotate 180 degrees (0.5 of a full turn)
            point.x shouldBe (-1.0f plusOrMinus tolerance)
            point.y shouldBe (0.0f plusOrMinus tolerance)
        }

        it("should rotate a point around an anchor point correctly") {
            val point = Point(1.0f, 1.0f)
            point.rotate(0.25f, 1.0f, 0.0f)  // Rotate 90 degrees (0.25 of a full turn)
            point.x shouldBe (0.0f plusOrMinus tolerance)
            point.y shouldBe (1.0f plusOrMinus tolerance)
        }
    }
})
