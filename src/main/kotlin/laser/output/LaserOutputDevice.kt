package laser.output

import dev.defvs.heliosdac.HeliosDac
import dev.defvs.heliosdac.HeliosDacStatusCode
import laser.LaserObject

abstract class LaserOutputDevice {
    private var devCount: Int = -1

    fun openDevices(): Int {
        if (devCount != -1) throw IllegalStateException("Devices are already initialized!")
        return openDevicesInternal().also { devCount = it }
    }

    protected abstract fun openDevicesInternal(): Int

    open fun closeDevices() {
        devCount = -1
    }

    private fun checkDevNum(devNum: Int) {
        if (devNum >= devCount) throw IllegalArgumentException("Device number Out-of-Bounds ($devNum > $devCount)")
    }

    open fun sendFrame(devNum: Int, frameData: List<LaserObject>) {
        checkDevNum(devNum)
    }

    open fun stopOutput(devNum: Int) {
        checkDevNum(devNum)
    }

    open fun openShutter(devNum: Int) {
        checkDevNum(devNum)
    }

    open fun closeShutter(devNum: Int) {
        checkDevNum(devNum)
    }
}

object HeliosDacOutputDevice : LaserOutputDevice() {
    class HeliosDacException(code: HeliosDacStatusCode) : Exception("HeliosDac returned error code: $code")

    private fun HeliosDacStatusCode.checkErrorCode() {
        if (this != HeliosDacStatusCode.SUCCESS) throw HeliosDacException(this)
    }

    private val native = HeliosDac()

    var pps: Int = 25000
    var blocking: Boolean = true
    var startImmediately = false
    var repeatFrame = true

    override fun openDevicesInternal() = native.openDevices()

    override fun closeDevices() {
        super.closeDevices()
        native.closeDevices().checkErrorCode()
    }

    override fun closeShutter(devNum: Int) {
        super.closeShutter(devNum)
        native.setShutter(devNum, false)
    }

    override fun openShutter(devNum: Int) {
        super.openShutter(devNum)
        native.setShutter(devNum, true)
    }

    override fun stopOutput(devNum: Int) {
        super.stopOutput(devNum)
        native.stop(devNum)
    }

    override fun sendFrame(devNum: Int, frameData: List<LaserObject>) {
        super.sendFrame(devNum, frameData)
        val heliosPoints = TODO("Convert LaserObjects into HelioDac points")
        native.writeFrame(
            devNum,
            pps,
            heliosPoints,
            startImmediately,
            repeatFrame,
            blocking
        ).checkErrorCode()
    }
}