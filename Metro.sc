Metro : UGen {
    *ar {
        arg freq=440.0, phase=0.0, mul=1.0, add=0.0;
		^this.multiNew('audio', freq, phase).madd(mul, add);
	}
}