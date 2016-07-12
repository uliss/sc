GLevelInd : LevelIndicator {
    *new {
        arg parent, bounds = Rect(0, 0, 20, 120);
        ^super.new(parent, bounds).init
    }

    init {
        this.value = 0.01;

        this.drawsPeak = true;
        this.peakLevel = 0.5;
        this.numTicks = 13;
        this.numMajorTicks = 5;
        this.background = Color.gray(0.4);

        this.meterColor = Color.hsv(0.12, 0.9, 1);
        this.warningColor = Color.hsv(0.12, 0.9, 1);
        this.criticalColor = Color.hsv(0, 0.9, 1);
    }
}